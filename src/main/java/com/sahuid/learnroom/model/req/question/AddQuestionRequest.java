package com.sahuid.learnroom.model.req.question;

import lombok.Data;

import java.util.List;

@Data
public class AddQuestionRequest {

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String context;


    /**
     * 推荐答案
     */
    private String answer;

    /**
     * 标签(json 数组)
     */
    private List<String> tags;
}
