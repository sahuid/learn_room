package com.sahuid.learnroom.model.req.question;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mcj
 * @Description: ai 生成题目请求
 * @DateTime: 2025/2/28 17:43
 **/
@Data
public class AIGenerateQuestionRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 题目类型：例如 java
     */
    private String questionType;

    /**
     * 题目数量
     */
    private Integer number;

}
