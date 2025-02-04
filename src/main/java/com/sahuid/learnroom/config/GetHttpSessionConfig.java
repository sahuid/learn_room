package com.sahuid.learnroom.config;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @Author: mcj
 * @Description: websocket 端点配置类
 * @DateTime: 2025/2/4 14:27
 **/
public class GetHttpSessionConfig extends ServerEndpointConfig.Configurator {

    /**
     * 获取 httpsession
     * @param sec
     * @param request
     * @param response
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        // 获取 httpsession
        HttpSession httpSession = (HttpSession) request.getHttpSession();

        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}