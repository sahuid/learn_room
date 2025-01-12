package com.sahuid.learnroom.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: mcj
 * @Description: 评论Vo
 * @DateTime: 2025/1/12 1:40
 **/
@Data
public class CommentVo {
    private Long id;

    /**
     * 内容
     */
    private String content;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 目标id
     */
    private Long targetId;

    /**
     * 根评论id
     */
    private Long rootId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userPicture;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 子评论
     */
    List<CommentVo> subComment;

    /**
     * 回复数
     */
    private Long replyCount;
}
