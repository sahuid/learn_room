package com.sahuid.learnroom.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class QuestionVo implements Serializable {


    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String context;

    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 推荐答案
     */
    private String answer;

    /**
     * 标签(json 数组)
     */
    private String tags;

}
