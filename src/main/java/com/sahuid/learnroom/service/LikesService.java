package com.sahuid.learnroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sahuid.learnroom.model.req.like.HasLikedRequest;
import com.sahuid.learnroom.model.req.like.LikeCountRequest;
import com.sahuid.learnroom.model.req.like.LikeRequest;
import com.sahuid.learnroom.model.req.like.UnLikeRequest;
import com.sahuid.learnroom.model.entity.Likes;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
* @author Lenovo
* @description 针对表【likes】的数据库操作Service
* @createDate 2024-12-27 12:12:50
*/
public interface LikesService extends IService<Likes> {

    /**
     * 点赞
     * @param likeRequest
     * @param request
     * @return
     */
    void like(LikeRequest likeRequest, HttpServletRequest request);

    /**
     * 同时添加点赞记录和修改数量,(防止长事务)
     * @param likes
     * @param targetId
     * @param targetType
     */
    @Transactional(rollbackFor = Exception.class)
    void addLikeAndUpdateCount(Likes likes, Long targetId, Integer targetType);

    /**
     * 判断是否点过赞
     * @param hasLikedRequest
     * @return
     */
    Boolean hasLiked(HasLikedRequest hasLikedRequest, HttpServletRequest request);

    /**
     * 获取点赞数量
     * @param likeCountRequest
     * @return
     */
    Integer getLikeCount(LikeCountRequest likeCountRequest);

    /**
     * 取消点赞
     * @param unLikeRequest
     */
    void unLike(UnLikeRequest unLikeRequest);

    @Transactional(rollbackFor = Exception.class)
    void unLikeAndUpdateCount(LambdaQueryWrapper<Likes> wrapper, Long targetId, Integer targetType);
}
