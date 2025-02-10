package com.sahuid.learnroom.ws;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.sahuid.learnroom.ai.AiManager;
import com.sahuid.learnroom.config.GetHttpSessionConfig;
import com.sahuid.learnroom.model.enums.MessageRoleEnums;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.MessageHistoryService;
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
public class ChatService {

    private static ApplicationContext applicationContext;

    /**
     * 用户聊天记录 map
     */
    private static final Map<Long, List<Message>> USER_MESSAGE_HISTORY = new ConcurrentHashMap<>();

    private volatile static AiManager aiManager ;
    private volatile static UserService userService;

    private volatile static MessageHistoryService messageHistoryService;

    /**
     * 用户 id 作为唯一标识
     */
    private Long userId;

    /**
     * 最大上下文长度
     */
    private static final Integer MAX_CONTENT_LENGTH = 1000;

    /**
     * websocket 消息结束标识
     */
    private static final String COMPLETE_FLAG = "生成结束";

    /**
     * 通过启动类设置 spring 的上下文
     * @param applicationContext
     */
    public static void setApplicationContext(ApplicationContext applicationContext){
        ChatService.applicationContext = applicationContext;
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
        // 保存连接关系
        this.userId = Long.valueOf((String) StpUtil.getLoginId());
        log.info("websocket 建立链接成功, userId: {}", userId);
    }


    /**
     * 断开连接
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
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
        // 获取上下文记录
        List<Message> msgManager = USER_MESSAGE_HISTORY.computeIfAbsent(userId, k -> new ArrayList<>());
        // 限制最大上下文内容
        if (msgManager.size() > MAX_CONTENT_LENGTH) {
            sendText(session, "超过最大上下文内容，请清理内容" + COMPLETE_FLAG);
            return;
        }
        // 保存用户提问记录
        messageHistoryService.addMessageHistory(userId, message, MessageRoleEnums.USER);
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
        // 记录回复信息
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

    /**
     * 发送消息代码块
     * @param session
     * @param result
     * @param stringBuilder
     */
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
        // ai 回复消息
        Message replayMessage = Message.builder()
                .role(Role.ASSISTANT.getValue())
                .content(stringBuilder.toString())
                .build();
        List<Message> msgManager = USER_MESSAGE_HISTORY.get(userId);
        msgManager.add(replayMessage);
        // 保存 ai 回复消息记录
        messageHistoryService.addMessageHistory(userId, stringBuilder.toString(), MessageRoleEnums.AI);
    }

    /**
     * 处理异常
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("websocket 链接出现问题，错误原因:{}", throwable.getMessage());
    }

    /**
     * 初始化 spring bean
     */
    private void initSpringBean() {
        if(aiManager != null && userService != null && messageHistoryService != null) {
            return;
        }
        if(aiManager == null || userService == null || messageHistoryService == null) {
            synchronized (ChatService.class) {
                if (aiManager == null) {
                    aiManager = applicationContext.getBean(AiManager.class);
                }
                if (userService == null) {
                    userService = applicationContext.getBean(UserService.class);
                }
                if(messageHistoryService == null) {
                    messageHistoryService = applicationContext.getBean(MessageHistoryService.class);
                }
            }
        }
    }

    /**
     * 删除指定用户的上下文内容
     * @param userId
     */
    public static void clearChatMessage(Long userId) {
        USER_MESSAGE_HISTORY.remove(userId);
    }
}
