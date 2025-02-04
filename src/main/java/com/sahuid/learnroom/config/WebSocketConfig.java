package com.sahuid.learnroom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author: mcj
 * @Description: WebSocket 配置类
 * @DateTime: 2025/2/4 14:25
 **/
@Configuration
public class WebSocketConfig {

    /**
     * 添加对 ServerEndpoint 的扫描
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
