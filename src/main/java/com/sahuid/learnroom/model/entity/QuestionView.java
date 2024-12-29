package com.sahuid.learnroom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName question_view
 */
@TableName(value ="question_view")
@Data
public class QuestionView implements Serializable {
    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 浏览时间
     */
    private Date viewTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}