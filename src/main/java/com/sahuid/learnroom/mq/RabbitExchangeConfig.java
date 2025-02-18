package com.sahuid.learnroom.mq;

import com.sahuid.learnroom.constants.RabbitMqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: mcj
 * @Description: rabbitmq 交换机配置类
 * @DateTime: 2025/2/17 14:18
 **/
@Configuration
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

}
