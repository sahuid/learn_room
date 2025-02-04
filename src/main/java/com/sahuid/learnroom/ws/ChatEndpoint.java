package com.sahuid.learnroom.ws;

import com.sahuid.learnroom.config.GetHttpSessionConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * @Author: mcj
 * @Description: websocket 端点
 * @DateTime: 2025/2/4 14:32
 **/
@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfig.class)
@Component
@Slf4j
@CrossOrigin(origins = "*")
public class ChatEndpoint {

    /**
     * 创建链接时
     * @param session
     * @param sec
     */
    @OnOpen
    public void OnOpen (Session session, EndpointConfig sec) {
        log.info("websocket 建立链接成功, sessionId: {}", session.getId());
    }


    /**
     * 断开连接
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        log.info("websocket 链接关闭，sessionId：{}",session.getId());
    }


    /**
     * 接受处理消息
     * @param session
     * @param message
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("websocket 接受到消息， 消息内容：{}", message);
    }


    /**
     * 处理异常
     * @param throwable
     */
    @OnError
    public void onError(Throwable throwable) {
        log.info("websocket 链接出现问题，错误原因:{}", throwable.getMessage());
    }

}
