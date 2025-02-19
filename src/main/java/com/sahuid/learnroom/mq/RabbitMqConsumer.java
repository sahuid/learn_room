package com.sahuid.learnroom.mq;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.sahuid.learnroom.constants.RabbitMqConstant;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.model.entity.Message;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.service.MessageService;
import com.sahuid.learnroom.service.QuestionService;
import com.sahuid.learnroom.utils.ThrowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: mcj
 * @Description: rabbit mq 消费者
 * @DateTime: 2025/2/18 13:30
 **/
@Component
@Slf4j
public class RabbitMqConsumer {

    @Resource
    private QuestionService questionService;

    @Resource
    private TransactionTemplate transactionTemplate;
    
    @Resource
    private MessageService messageService;

    @RabbitListener(queues = RabbitMqConstant.SAVE_QUESTION_2_DB_QUEUE)
    public void questionSave2DB(Message message, Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        log.info("保存题目到数据库的消费者接收到消息，消息内容：{}", message);
        Long messageId = message.getMessageId();
        try {
            Message messageData = messageService.queryMessageByIdAndStatus(messageId, "success");
            if (messageData != null) {
                // 已经处理过了，直接返回
                channel.basicAck(tag, false);
                return;
            }
            // 处理数据
            processQuestionDataAndMessageStatus(message.getContent(), messageId);
            // 手动确认
            channel.basicAck(tag, false);
            log.info("消息消费成功，题目成功保存到数据库");
        }catch (Exception e) {
            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ex) {
                log.error("Nack failed", ex);
            }
        }
    }

    private void processQuestionDataAndMessageStatus(String jsonData, Long messageId) {
        List<Question> list = JSONUtil.toBean(jsonData, new TypeReference<List<Question>>() {
        }, false);
        Boolean tracsaction = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try {
                    questionService.saveBatch(list);
                    messageService.updateMessageSuccess(messageId);
                    return true;
                } catch (Exception e) {
                    //事务回滚
                    status.setRollbackOnly();
                    return false;
                }
            }
        });
        if (Boolean.FALSE.equals(tracsaction)) {
            // 事务执行失败
            log.error("mq 消息消费失败：消息id{}", messageId);
            throw new DataOperationException("mq 消息消费失败");
        }
    }
}
