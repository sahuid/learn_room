package com.sahuid.learnroom.model.req.questionbank;

import com.sahuid.learnroom.common.PageRequest;
import lombok.Data;

@Data
public class QueryQuestionBankOneRequest extends PageRequest {

    private Long id;

    private Boolean showQuestion;

    /**
     * 题目名称
     */
    private String title;

    /**
     * 题目名称
     */
    private String description;
}
