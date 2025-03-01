package com.sahuid.learnroom.model.req.mockInterview;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mcj
 * @Description: 创建模拟面试请求
 * @DateTime: 2025/3/1 15:45
 **/
@Data
public class CreateMockInterviewRequest implements Serializable {


    /**
     * 工作年限
     */
    private String workExperience;

    /**
     * 工作岗位
     */
    private String jobPosition;

    /**
     * 面试难度
     */
    private String difficulty;


    private static final long serialVersionUID = 1L;
}
