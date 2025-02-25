package com.sahuid.learnroom.model.req.collect;

import lombok.Data;

import java.io.Serializable;

@Data
public class HasCollectQuestionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 用户 id
     */
    private Long userId;
}
