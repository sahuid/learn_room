package com.sahuid.learnroom.model.req.mockInterview;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mcj
 * @Description: 模拟面试事件请求
 * @DateTime: 2025/3/1 15:45
 **/
@Data
public class HandleMockInterviewChatEventRequest implements Serializable {

    private Long id;

    private String event;

    private String content;

    private static final long serialVersionUID = 1L;
}
