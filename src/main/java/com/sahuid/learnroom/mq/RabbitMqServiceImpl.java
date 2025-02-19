package com.sahuid.learnroom.mq;

import cn.hutool.core.util.StrUtil;
import com.sahuid.learnroom.constants.RabbitMqConstant;
import com.sahuid.learnroom.model.entity.Message;
import com.sahuid.learnroom.model.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * @Author: mcj
 * @Description: rabbit mq 服务实现类
 * @DateTime: 2025/2/18 13:28
 **/
@Component
@Slf4j
public class RabbitMqServiceImpl implements RabbitMqService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendQuestion2DBMessage(Message message) {
        log.info("接收到题目保存数据库的消息，消息内容：{}", message);
        rabbitTemplate.convertAndSend(
                RabbitMqConstant.SAVE_QUESTION_2_DB_DIRECT_EXCHANGE,
                RabbitMqConstant.SAVE_QUESTION_2_DB_ROUTING_KEY,
                message);
    }
}
