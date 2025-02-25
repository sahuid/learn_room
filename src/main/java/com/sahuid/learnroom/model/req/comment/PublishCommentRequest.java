package com.sahuid.learnroom.model.req.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mcj
 * @Description: 发布评论请求
 * @DateTime: 2025/1/12 0:54
 **/
@Data
public class PublishCommentRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 父评论Id
     */
    private Long parentId;

    /**
     * 根评论Id
     */
    private Long rootId;

}
