package com.sahuid.learnroom.model.req.questionbank;

import com.sahuid.learnroom.common.PageRequest;
import lombok.Data;

@Data
public class QueryQuestionBankByPageRequest extends PageRequest {

    /**
     * 题库名称
     */
    private String title;

    /**
     * 题库描述
     */
    private String description;
}
