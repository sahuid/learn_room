package com.sahuid.learnroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.model.dto.like.HasLikedRequest;
import com.sahuid.learnroom.model.dto.like.LikeCountRequest;
import com.sahuid.learnroom.model.dto.like.LikeRequest;
import com.sahuid.learnroom.model.dto.like.UnLikeRequest;
import com.sahuid.learnroom.model.entity.Likes;
import com.sahuid.learnroom.model.entity.LikesCount;
import com.sahuid.learnroom.model.enums.LikeTargetTypeEnums;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.LikesCountService;
import com.sahuid.learnroom.service.LikesService;
import com.sahuid.learnroom.mapper.LikesMapper;
import com.sahuid.learnroom.service.UserService;
import com.sahuid.learnroom.utils.ThrowUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Lenovo
 * @description 针对表【likes】的数据库操作Service实现
 * @createDate 2024-12-27 12:12:50
 */
@Service
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes>
        implements LikesService {

    @Resource
    private LikesCountService likesCountService;

    /**
     * 点赞
     *
     * @param likeRequest
     * @param request
     * @return
     */
    @Override
    public void like(LikeRequest likeRequest, HttpServletRequest request) {
        ThrowUtil.throwIf(likeRequest == null, () -> new RequestParamException("请求参数错误"));
        Long targetId = likeRequest.getTargetId();
        Integer targetType = likeRequest.getTargetType();
        Long userId = likeRequest.getUserId();
        ThrowUtil.throwIf(targetType == null || targetId == null || userId == null, () -> new RequestParamException("请求参数错误"));
        LikeTargetTypeEnums likeTargetType = LikeTargetTypeEnums.getLikeTargetType(targetType);
        ThrowUtil.throwIf(likeTargetType == null || targetId <= 0 || userId <= 0, () -> new RequestParamException("请求参数错误"));
        // 创建点赞记录
        Likes likes = new Likes();
        likes.setTargetId(targetId);
        likes.setTargetType(targetType);
        likes.setUserId(userId);
        // 添加点赞记录和更新数据
        // 事务会失效, 需要使用代理
        LikesService likesService = (LikesService) AopContext.currentProxy();
        likesService.addLikeAndUpdateCount(likes, targetId, targetType);
    }

    /**
     * 同时添加点赞记录和修改数量,防止长事务
     *
     * @param likes
     * @param targetId
     * @param targetType
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addLikeAndUpdateCount(Likes likes, Long targetId, Integer targetType) {
        // 添加点赞记录
        boolean save = this.save(likes);
        ThrowUtil.throwIf(!save, () -> new DataOperationException("点赞失败"));
        // 判断点赞数量数据是否存在
        LambdaQueryWrapper<LikesCount> likesCountWrapper = new LambdaQueryWrapper<>();
        likesCountWrapper.eq(LikesCount::getTargetId, targetId);
        likesCountWrapper.eq(LikesCount::getTargetType, targetType);
        LikesCount likesCount = likesCountService.getOne(likesCountWrapper);
        if (likesCount == null) {
            // 不存在向数据库中添加数量记录,由于是第一次所以且到这里,所以 count = 1
            likesCount = new LikesCount();
            likesCount.setTargetId(targetId);
            likesCount.setTargetType(targetType);
            likesCount.setCount(1);
            boolean res = likesCountService.save(likesCount);
            ThrowUtil.throwIf(!res, () -> new DataOperationException("点赞失败"));
            return;
        }
        // 存在,数量 + 1
        LambdaUpdateWrapper<LikesCount> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LikesCount::getTargetId, targetId);
        updateWrapper.eq(LikesCount::getTargetType, targetType);
        updateWrapper.setSql("count = count + 1");
        boolean update = likesCountService.update(updateWrapper);
        ThrowUtil.throwIf(!update, () -> new DataOperationException("点赞失败"));
    }

    /**
     * 判断是否点过赞
     *
     * @param hasLikedRequest
     * @return
     */
    @Override
    public Boolean hasLiked(HasLikedRequest hasLikedRequest, HttpServletRequest request) {
        ThrowUtil.throwIf(hasLikedRequest == null, () -> new RequestParamException("请求参数错误"));
        Long targetId = hasLikedRequest.getTargetId();
        Integer targetType = hasLikedRequest.getTargetType();
        Long userId = hasLikedRequest.getUserId();
        ThrowUtil.throwIf(targetType == null || targetId == null || userId == null, () -> new RequestParamException("请求参数错误"));
        LikeTargetTypeEnums likeTargetType = LikeTargetTypeEnums.getLikeTargetType(targetType);
        ThrowUtil.throwIf(likeTargetType == null || targetId <= 0 || userId <= 0, () -> new RequestParamException("请求参数错误"));
        // 判断是否点过赞
        LambdaQueryWrapper<Likes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Likes::getTargetId, targetId);
        wrapper.eq(Likes::getTargetType, targetType);
        wrapper.eq(Likes::getUserId, userId);
        Likes likes = this.getOne(wrapper);
        return likes != null;
    }

    /**
     * 获取点赞数量
     *
     * @param likeCountRequest
     * @return
     */
    @Override
    public Integer getLikeCount(LikeCountRequest likeCountRequest) {
        ThrowUtil.throwIf(likeCountRequest == null, () -> new RequestParamException("请求参数错误"));
        Long targetId = likeCountRequest.getTargetId();
        Integer targetType = likeCountRequest.getTargetType();
        ThrowUtil.throwIf(targetType == null || targetId == null, () -> new RequestParamException("请求参数错误"));
        LikeTargetTypeEnums likeTargetType = LikeTargetTypeEnums.getLikeTargetType(targetType);
        ThrowUtil.throwIf(likeTargetType == null || targetId <= 0, () -> new RequestParamException("请求参数错误"));
        // 查询数量
        LambdaQueryWrapper<LikesCount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikesCount::getTargetId, targetId);
        wrapper.eq(LikesCount::getTargetType, targetType);
        LikesCount likesCount = likesCountService.getOne(wrapper);
        if (likesCount == null) {
            return 0;
        }
        return likesCount.getCount();
    }

    /**
     * 删除点赞记录
     * @param unLikeRequest
     */
    @Override
    public void unLike(UnLikeRequest unLikeRequest) {
        ThrowUtil.throwIf(unLikeRequest == null, () -> new RequestParamException("请求参数错误"));
        Long targetId = unLikeRequest.getTargetId();
        Integer targetType = unLikeRequest.getTargetType();
        Long userId = unLikeRequest.getUserId();
        ThrowUtil.throwIf(targetType == null || targetId == null || userId == null, () -> new RequestParamException("请求参数错误"));
        LikeTargetTypeEnums likeTargetType = LikeTargetTypeEnums.getLikeTargetType(targetType);
        ThrowUtil.throwIf(likeTargetType == null || targetId <= 0 || userId <= 0, () -> new RequestParamException("请求参数错误"));
        // 删除点赞记录
        LambdaQueryWrapper<Likes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Likes::getUserId, userId);
        wrapper.eq(Likes::getTargetId, targetId);
        wrapper.eq(Likes::getTargetType, targetType);
        LikesService likesService = (LikesService) AopContext.currentProxy();
        likesService.unLikeAndUpdateCount(wrapper, targetId, targetType);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unLikeAndUpdateCount(LambdaQueryWrapper<Likes> wrapper, Long targetId, Integer targetType) {
        // 删除点赞记录
        boolean remove = this.remove(wrapper);
        ThrowUtil.throwIf(!remove, () -> new DataOperationException("取消失败"));
        // 判断点赞数量数据是否存在
        LambdaUpdateWrapper<LikesCount> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LikesCount::getTargetId, targetId);
        updateWrapper.eq(LikesCount::getTargetType, targetType);
        updateWrapper.setSql("count = count - 1");
        // 存在,数量 + 1
        boolean update = likesCountService.update(updateWrapper);
        ThrowUtil.throwIf(!update, () -> new DataOperationException("取消失败"));
    }
}




