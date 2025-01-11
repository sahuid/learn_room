package com.sahuid.learnroom.service;

import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.model.dto.comment.PublishCommentRequest;
import com.sahuid.learnroom.model.dto.comment.QueryCommentByPageRequest;
import com.sahuid.learnroom.model.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sahuid.learnroom.model.vo.CommentVo;

/**
* @author mcj
* @description 针对表【comment】的数据库操作Service
* @createDate 2025-01-12 00:38:03
*/
public interface CommentService extends IService<Comment> {

    /**
     * 发布评论
     * @param publishCommentRequest
     */
    void publishComment(PublishCommentRequest publishCommentRequest);

    /**
     * 分页查询评论
     * @param queryCommentByPageRequest
     * @return
     */
    PageResult<CommentVo> queryCommentByPage(QueryCommentByPageRequest queryCommentByPageRequest);
}
