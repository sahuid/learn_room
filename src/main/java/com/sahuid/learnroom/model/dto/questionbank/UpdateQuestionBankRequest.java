package com.sahuid.learnroom.model.dto.questionbank;

import lombok.Data;

@Data
public class UpdateQuestionBankRequest {

    private Long id;

    /**
     * 题库名称
     */
    private String title;

    /**
     * 题库描述
     */
    private String description;

    /**
     * 题库图片
     */
    private String picture;


}
