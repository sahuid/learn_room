package com.sahuid.learnroom.controller;

import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.dto.comment.PublishCommentRequest;
import com.sahuid.learnroom.model.dto.comment.QueryCommentByPageRequest;
import com.sahuid.learnroom.model.vo.CommentVo;
import com.sahuid.learnroom.service.CommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    public R<PageResult<CommentVo>> queryCommentByPage(QueryCommentByPageRequest queryCommentByPageRequest) {
        PageResult<CommentVo> result = commentService.queryCommentByPage(queryCommentByPageRequest);
        return R.ok(result);
    }
}
