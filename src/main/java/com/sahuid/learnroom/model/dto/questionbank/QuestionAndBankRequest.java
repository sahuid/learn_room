package com.sahuid.learnroom.model.dto.questionbank;

import lombok.Data;

@Data
public class QuestionAndBankRequest {

    /**
     * 题库id
     */
    private Long questionBankId;

    /**
     * 题目id
     */
    private Long questId;
}
