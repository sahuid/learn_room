package com.sahuid.learnroom.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author: mcj
 * @Description: rabbitmq 配置类
 * @DateTime: 2025/2/19 20:21
 **/
@Configuration
public class RabbitMqConfig {

    @Resource
    private RabbitTemplate.ConfirmCallback confirmCallback;

    @Resource
    private RabbitTemplate.ReturnsCallback returnsCallback;
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnsCallback(returnsCallback);
        rabbitTemplate.setMandatory(true); // 确保消息无法路由时返回
        return rabbitTemplate;
    }
}
