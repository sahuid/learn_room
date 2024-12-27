package com.sahuid.learnroom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName likes_count
 */
@TableName(value ="likes_count")
@Data
public class LikesCount implements Serializable {
    /**
     * 点赞实体id
     */
    private Long targetId;

    /**
     * 点赞实体类型:0-题目
     */
    private Integer targetType;

    /**
     * 点赞数
     */
    private Integer count;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}