package com.sahuid.learnroom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName collect_count
 */
@TableName(value ="collect_count")
@Data
public class CollectCount implements Serializable {
    /**
     * 题目id
     */
    @TableId
    private Long questionId;

    /**
     * 收藏数
     */
    private Integer collectCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}