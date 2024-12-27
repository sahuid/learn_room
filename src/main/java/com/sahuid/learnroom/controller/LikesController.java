package com.sahuid.learnroom.controller;

import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.dto.like.HasLikedRequest;
import com.sahuid.learnroom.model.dto.like.LikeCountRequest;
import com.sahuid.learnroom.model.dto.like.LikeRequest;
import com.sahuid.learnroom.model.dto.like.UnLikeRequest;
import com.sahuid.learnroom.service.LikesService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 点赞 controller
 */
@RestController
@RequestMapping("/likes")
public class LikesController {

    @Resource
    private LikesService likesService;

    /**
     * 点赞
     * @param likeRequest
     * @param request
     * @return
     */
    @PostMapping
    public R<Void> like(@RequestBody LikeRequest likeRequest, HttpServletRequest request) {
        likesService.like(likeRequest, request);
        return R.ok("点赞成功");
    }

    /**
     * 判断是否点过赞
     * @param hasLikedRequest
     * @return
     */
    @GetMapping("/hasLike")
    public R<Boolean> hasLiked(HasLikedRequest hasLikedRequest, HttpServletRequest request) {
        Boolean res = likesService.hasLiked(hasLikedRequest, request);
        return R.ok(res);
    }

    /**
     * 获取点赞数量
     * @param likeCountRequest
     * @return
     */
    @GetMapping("/count")
    public R<Integer> getLikeCount(LikeCountRequest likeCountRequest) {
        Integer count = likesService.getLikeCount(likeCountRequest);
        return R.ok(count);
    }

    @PostMapping("/unlike")
    public R<Void> unLike(@RequestBody  UnLikeRequest unLikeRequest) {
        likesService.unLike(unLikeRequest);
        return R.ok("取消成功");
    }
}
