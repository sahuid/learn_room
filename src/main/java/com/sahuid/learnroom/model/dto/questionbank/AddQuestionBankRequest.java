package com.sahuid.learnroom.model.dto.questionbank;

import lombok.Data;

@Data
public class AddQuestionBankRequest {


    /**
     * 题库名称
     */
    private String title;

    /**
     * 题库描述
     */
    private String description;


}
