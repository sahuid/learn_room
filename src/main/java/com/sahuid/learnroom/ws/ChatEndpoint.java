package com.sahuid.learnroom.ws;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.sahuid.learnroom.ai.AiManager;
import com.sahuid.learnroom.config.GetHttpSessionConfig;
import com.sahuid.learnroom.service.UserService;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
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
    private static final Map<String, Session> ONLINE_USER_SESSION_MAP = new ConcurrentHashMap<>();

    private HttpSession httpSession;

    private volatile static AiManager aiManager ;
    private volatile static UserService userService;

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
        // 保存连接关系
        ONLINE_USER_SESSION_MAP.put(session.getId(), session);
        log.info("websocket 建立链接成功, sessionId: {}", session.getId());
        httpSession = (HttpSession) sec.getUserProperties().get(HttpSession.class.getName());
    }


    /**
     * 断开连接
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        ONLINE_USER_SESSION_MAP.remove(session.getId());
        log.info("websocket 链接关闭，sessionId：{}",session.getId());
    }


    /**
     * 接受处理消息
     * @param session
     * @param message
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("ai 接受到消息，sessionId:{}, 内容：{}",session.getId(), message);
        // 获取消息
        Flowable<GenerationResult> resultFlowable;
        try {
            resultFlowable = aiManager.streamAiTalk(message);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 使用日志框架记录异常信息
            log.error("调用 AI 服务失败: {}", e.getMessage());
            sendError(session, e);
            return;
        }
        // 启动流式处理
        resultFlowable
                .subscribeOn(Schedulers.io())
                .subscribe(
                        result -> sendChunk(session, result), // 发送数据块
                        error -> sendError(session, error),   // 发送错误
                        () -> sendComplete(session)          // 发送完成信号
                );

    }

    private void sendChunk(Session session, GenerationResult result) {
        try {
            if (session.isOpen()) {
                String message = aiManager.resolveResult(result); // 自定义转换逻辑
                session.getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            log.error("WebSocket 发送消息失败: {}", e.getMessage());
        }
    }


    private void sendError(Session session, Throwable error) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText("错误: " + error.getMessage());
            }
        } catch (IOException e) {
            log.error("WebSocket 发送错误消息失败: {}", e.getMessage());
        } finally {
            closeSession(session);
        }
    }

    private void sendComplete(Session session) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText("生成结束");
            }
        } catch (IOException e) {
            log.error("WebSocket 发送完成消息失败: {}", e.getMessage());
        } finally {
            closeSession(session);
        }
    }

    private void closeSession(Session session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            log.error("关闭 WebSocket 会话失败: {}", e.getMessage());
        }
    }


    /**
     * 处理异常
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.info("websocket 链接出现问题，错误原因:{}", throwable.getMessage());
        ONLINE_USER_SESSION_MAP.remove(session.getId());
    }

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
