package com.sahuid.learnroom.mq;

import com.sahuid.learnroom.constants.RabbitMqConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: mcj
 * @Description: rabbitmq 交换机配置类
 * @DateTime: 2025/2/17 14:18
 **/
@Configuration
@Slf4j
public class RabbitExchangeConfig {

    @Bean
    public DirectExchange save2DBExchange(){
        return new DirectExchange(RabbitMqConstant.SAVE_QUESTION_2_DB_DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public Queue save2DBQueue() {
        return new Queue(RabbitMqConstant.SAVE_QUESTION_2_DB_QUEUE, true);
    }

    @Bean
    public Binding bindSavaQueueAndExchange() {
        return BindingBuilder.bind(save2DBQueue()).to(save2DBExchange()).with(RabbitMqConstant.SAVE_QUESTION_2_DB_ROUTING_KEY);
    }


    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback() {
        return (correlationData, ack, cause) -> {
            if(!ack) {
                log.error("消息发送到交换机失败，原因是：{}" ,cause);
            }else {
                log.info("消息成功发送到交换机");
            }
        };
    }

    @Bean
    public RabbitTemplate.ReturnsCallback returnsCallback() {
        return returnedMessage -> log.error("交换机发送到队列失败，返回信息:{}", returnedMessage);
    }

}
