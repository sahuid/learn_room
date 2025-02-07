package com.sahuid.learnroom.ws;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.sahuid.learnroom.ai.AiManager;
import com.sahuid.learnroom.config.GetHttpSessionConfig;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.UserService;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: mcj
 * @Description: websocket 端点
 * @DateTime: 2025/2/4 14:32
 **/
@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfig.class)
@Component
@Slf4j
@CrossOrigin(origins = "*")
public class ChatEndpoint{

    private static ApplicationContext applicationContext;

    /**
     * 管理在线用户和连接session的map
     */
    private static final Map<Long, Session> ONLINE_USER_SESSION_MAP = new ConcurrentHashMap<>();
    /**
     * 用户聊天记录 map
     */
    private static final Map<Long, List<Message>> USER_MESSAGE_HISTORY = new ConcurrentHashMap<>();

    private volatile static AiManager aiManager ;
    private volatile static UserService userService;

    /**
     * 用户 id 作为唯一标识
     */
    private Long userId;

    /**
     * 最大上下文长度
     */
    private static final Integer MAX_CONTENT_LENGTH = 2;

    /**
     * websocket 消息结束标识
     */
    private static final String COMPLETE_FLAG = "生成结束";

    /**
     * 通过启动类设置 spring 的上下文
     * @param applicationContext
     */
    public static void setApplicationContext(ApplicationContext applicationContext){
        ChatEndpoint.applicationContext = applicationContext;
    }


    /**
     * 创建链接时
     * @param session
     * @param sec
     */
    @OnOpen
    public void OnOpen (Session session, EndpointConfig sec) {
        // 初始化 bean
        initSpringBean();
        HttpSession httpSession = (HttpSession) sec.getUserProperties().get(HttpSession.class.getName());
        // 保存连接关系
        UserVo user = (UserVo) httpSession.getAttribute("user");
        this.userId = user.getId();
        ONLINE_USER_SESSION_MAP.put(userId, session);
        log.info("websocket 建立链接成功, userId: {}", userId);

    }


    /**
     * 断开连接
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        ONLINE_USER_SESSION_MAP.remove(userId);
        log.info("websocket 链接关闭，userId：{}", userId);
    }


    /**
     * 接受处理消息
     * @param session
     * @param message
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("ai 接受到消息，userId:{}, 内容：{}", userId, message);
        // 获取历史记录
        List<Message> msgManager = USER_MESSAGE_HISTORY.computeIfAbsent(userId, k -> new ArrayList<>());
        // 限制最大上下文内容
        if (msgManager.size() > MAX_CONTENT_LENGTH) {
            sendText(session, "超过最大上下文内容，请清理内容" + COMPLETE_FLAG);
            return;
        }
        // 获取消息
        Flowable<GenerationResult> resultFlowable;
        try {
            resultFlowable = aiManager.streamAiTalk(message, msgManager);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 使用日志框架记录异常信息
            log.error("调用 AI 服务失败: {}", e.getMessage());
            sendError(session, e);
            return;
        }
        // 保存回复记录
        StringBuilder stringBuilder = new StringBuilder();
        // 启动流式处理
        resultFlowable
                .subscribeOn(Schedulers.io())
                .subscribe(
                        result -> sendChunk(session, result, stringBuilder), // 发送数据块
                        error -> sendError(session, error),   // 发送错误
                        () -> sendComplete(session, stringBuilder)          // 发送完成信号
                );

    }

    /**
     * 发送单条消息
     * @param session
     * @param content
     */
    private void sendText(Session session, String content) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(content);
            }
        } catch (IOException e) {
            log.error("WebSocket 发送消息失败: {}", e.getMessage());
        }
    }

    private void sendChunk(Session session, GenerationResult result, StringBuilder stringBuilder) {
        try {
            if (session.isOpen()) {
                String message = aiManager.resolveResult(result); // 自定义转换逻辑
                stringBuilder.append(message);
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            log.error("WebSocket 发送消息失败: {}", e.getMessage());
        }
    }


    private void sendError(Session session, Throwable error) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText("系统错误，请重试" + COMPLETE_FLAG);
            }
        } catch (IOException e) {
            log.error("WebSocket 发送错误消息失败: {}", e.getMessage());
        }
    }

    private void sendComplete(Session session, StringBuilder stringBuilder) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(COMPLETE_FLAG);
            }
        } catch (IOException e) {
            log.error("WebSocket 发送完成消息失败: {}", e.getMessage());
        }
        Message replayMessage = Message.builder()
                .role(Role.ASSISTANT.getValue())
                .content(stringBuilder.toString())
                .build();
        List<Message> msgManager = USER_MESSAGE_HISTORY.get(userId);
        msgManager.add(replayMessage);
    }

    /**
     * 处理异常
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("websocket 链接出现问题，错误原因:{}", throwable.getMessage());
        ONLINE_USER_SESSION_MAP.remove(userId);
    }

    /**
     * 初始化 spring bean
     */
    private void initSpringBean() {
        if(aiManager != null && userService != null) {
            return;
        }
        if(aiManager == null) {
            synchronized (ChatEndpoint.class) {
                if (aiManager == null) {
                    aiManager = applicationContext.getBean(AiManager.class);
                }
            }
        }
        if(userService == null) {
            synchronized (ChatEndpoint.class) {
                if (userService == null) {
                    userService = applicationContext.getBean(UserService.class);
                }
            }
        }
    }

}
