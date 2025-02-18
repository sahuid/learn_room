package com.sahuid.learnroom.constants;

/**
 * @Author: mcj
 * @Description: rabbitmq 常量类
 * @DateTime: 2025/2/18 13:18
 **/
public class RabbitMqConstant {

    /**
     * 将题目保存到数据库的交换机
     */
    public static final String SAVE_QUESTION_2_DB_DIRECT_EXCHANGE = "save2DB";

    /**
     * 将题目保存到数据库的队列
     */
    public static final String SAVE_QUESTION_2_DB_QUEUE = "save2DB";

    public static final String SAVE_QUESTION_2_DB_ROUTING_KEY = "question";

}
