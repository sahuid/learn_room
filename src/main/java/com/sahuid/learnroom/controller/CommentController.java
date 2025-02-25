package com.sahuid.learnroom.controller;

import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.req.comment.PublishCommentRequest;
import com.sahuid.learnroom.model.req.comment.QueryCommentByPageRequest;
import com.sahuid.learnroom.model.vo.CommentVo;
import com.sahuid.learnroom.service.CommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: mcj
 * @Description: 评论功能
 * @DateTime: 2025/1/12 0:42
 **/
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @PostMapping("/publish")
    public R<Void> publishComment(@RequestBody PublishCommentRequest publishCommentRequest) {
        commentService.publishComment(publishCommentRequest);
        return R.ok("发布成功");
    }

    @GetMapping("/queryByPage")
    public R<PageResult<CommentVo>> queryRootCommentByPage(QueryCommentByPageRequest queryCommentByPageRequest) {
        PageResult<CommentVo> result = commentService.queryRootCommentByPage(queryCommentByPageRequest);
        return R.ok(result);
    }

    @GetMapping("/queryReplyComment")
    public R<List<CommentVo>> queryReplyComment(Long commentId) {
        List<CommentVo> list = commentService.queryReplyComment(commentId);
        return R.ok(list);
    }

    @GetMapping("/delete")
    public R<Void> deleteComment(Long commentId) {
        commentService.deleteComment(commentId);
        return R.ok();
    }
}
