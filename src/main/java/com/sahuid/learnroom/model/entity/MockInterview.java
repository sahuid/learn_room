package com.sahuid.learnroom.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.sahuid.learnroom.common.UserThreadLocalData;
import com.sahuid.learnroom.model.enums.MockInterviewStatusEnums;
import lombok.Data;

/**
 * 
 * @TableName mock_interview
 */
@TableName(value ="mock_interview")
@Data
public class MockInterview implements Serializable {
    /**
     * 主键 id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工作年限
     */
    private String workExperience;

    /**
     * 工作岗位
     */
    private String jobPosition;

    /**
     * 面试难度
     */
    private String difficulty;

    /**
     * 创建人（用户id）
     */
    private Long userId;

    /**
     * 消息列表（JSON数组对象，包括总结）
     */
    private String messages;

    /**
     * 状态（0-待开始，1-开始，2-结束）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public static MockInterview createInstance(String difficulty, String workExperience, String jobPosition) {
        Long userId = UserThreadLocalData.getUserData();
        MockInterview mockInterview = new MockInterview();
        mockInterview.setDifficulty(difficulty);
        mockInterview.setJobPosition(jobPosition);
        mockInterview.setWorkExperience(workExperience);
        mockInterview.setStatus(MockInterviewStatusEnums.WAITING_BEING.getStatus());
        mockInterview.setUserId(userId);
        return mockInterview;
    }
}