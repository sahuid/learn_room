package com.sahuid.learnroom.mq;

import com.sahuid.learnroom.constants.RabbitMqConstant;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.service.QuestionService;
import com.sahuid.learnroom.utils.ThrowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    @RabbitListener(queues = RabbitMqConstant.SAVE_QUESTION_2_DB_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void questionSave2DB(List<Question> list) {
        log.info("保存题目到数据库的消费者接收到消息，消息内容：{}", list);
        boolean save = questionService.saveBatch(list);
        ThrowUtil.throwIf(!save, () -> new DataOperationException("批量保存题目失败"));
        log.info("批量保存题目成功");
    }
}
