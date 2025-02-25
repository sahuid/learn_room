package com.sahuid.learnroom.model.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Author: mcj
 * @Description: 题目的 dto 类
 * @DateTime: 2025/2/25 15:57
 **/
@Data
public class QuestionDto {

    @Id
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

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    private Integer isDelete;
}
