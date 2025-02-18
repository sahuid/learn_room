package com.sahuid.learnroom.mq;

import com.sahuid.learnroom.model.entity.Question;

import java.util.List;

/**
 * @Author: mcj
 * @Description: rabbit mq 发送消息服务类
 * @DateTime: 2025/2/18 13:26
 **/
public interface RabbitMqService {

    void sendQuestion2DBMessage(List<Question> list);
}
