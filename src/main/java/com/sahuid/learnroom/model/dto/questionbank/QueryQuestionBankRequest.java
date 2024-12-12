package com.sahuid.learnroom.model.dto.questionbank;

import com.sahuid.learnroom.common.PageRequest;
import lombok.Data;

@Data
public class QueryQuestionBankRequest extends PageRequest {

    /**
     * 题库名称
     */
    private String title;

    /**
     * 题库描述
     */
    private String description;
}
