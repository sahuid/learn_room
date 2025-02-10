package com.sahuid.learnroom.service;

import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.model.dto.comment.PublishCommentRequest;
import com.sahuid.learnroom.model.dto.comment.QueryCommentByPageRequest;
import com.sahuid.learnroom.model.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sahuid.learnroom.model.vo.CommentVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 分页查询根评论
     * @param queryCommentByPageRequest
     * @return
     */
    PageResult<CommentVo> queryRootCommentByPage(QueryCommentByPageRequest queryCommentByPageRequest);

    /**
     * 查询回复评论
     * @param commentId
     * @return
     */
    List<CommentVo> queryReplyComment(Long commentId);


    /**
     * 删除评论
     * @param commentId
     */
    void deleteComment(Long commentId);
}
