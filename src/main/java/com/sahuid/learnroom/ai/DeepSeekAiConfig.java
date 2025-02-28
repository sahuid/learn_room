package com.sahuid.learnroom.ai;

import com.volcengine.ark.runtime.service.ArkService;
import lombok.Data;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author: mcj
 * @Description: deepseek AI模型
 * @DateTime: 2025/2/28 16:44
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.volcengine")
public class DeepSeekAiConfig {

    private String apiKey;


    @Bean
    public ArkService arkService() {
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        return ArkService.builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl("https://ark.cn-beijing.volces.com/api/v3")
                .apiKey(apiKey).build();
    }
}
