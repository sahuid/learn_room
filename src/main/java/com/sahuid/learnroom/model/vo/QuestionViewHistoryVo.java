package com.sahuid.learnroom.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QuestionViewHistoryVo implements Serializable {

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
     * 标签(json 数组)
     */
    private String tags;

    /**
     * 浏览时间
     */
    private Date viewTime;

    /**
     * 浏览次数
     */
    private Integer viewCount;
}
