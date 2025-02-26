package com.sahuid.learnroom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.exception.DataBaseAbsentException;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.NoAuthException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.model.req.comment.PublishCommentRequest;
import com.sahuid.learnroom.model.req.comment.QueryCommentByPageRequest;
import com.sahuid.learnroom.model.entity.Comment;
import com.sahuid.learnroom.model.entity.User;
import com.sahuid.learnroom.model.vo.CommentVo;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.CommentService;
import com.sahuid.learnroom.mapper.CommentMapper;
import com.sahuid.learnroom.service.UserService;
import com.sahuid.learnroom.utils.ThrowUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author mcj
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2025-01-12 00:38:03
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Resource
    private UserService userService;

    @Override
    public void publishComment(PublishCommentRequest publishCommentRequest) {
        validComment(publishCommentRequest);
        Comment comment = new Comment();
        BeanUtil.copyProperties(publishCommentRequest, comment, false);
        boolean save = this.save(comment);
        ThrowUtil.throwIf(!save, () -> new DataOperationException("发布失败"));
    }

    @Override
    public PageResult<CommentVo> queryRootCommentByPage(QueryCommentByPageRequest queryCommentByPageRequest) {
        ThrowUtil.throwIf(queryCommentByPageRequest == null, () -> new RequestParamException("请求参数错误"));
        int currentPage = queryCommentByPageRequest.getPage();
        int pageSize = queryCommentByPageRequest.getPageSize();
        Page<Comment> page = new Page<>(currentPage, pageSize);
        Long targetId = queryCommentByPageRequest.getTargetId();

        // 分页查询根评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getTargetId, targetId);
        wrapper.isNull(Comment::getRootId);
        wrapper.isNull(Comment::getParentId);
        wrapper.orderByAsc(Comment::getCreateTime);
        this.page(page, wrapper);
        // 转化为 Vo
        PageResult<CommentVo> result = new PageResult<>();
        result.setTotal(page.getTotal());
        List<Comment> records = page.getRecords();
        List<CommentVo> list = commentToVo(records);
        result.setData(list);
        return result;
    }

    @NotNull
    private List<CommentVo> commentToVo(List<Comment> records) {
        if (records.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        List<CommentVo> list = records.stream().map(comment -> {
            CommentVo commentVo = new CommentVo();
            BeanUtil.copyProperties(comment, commentVo, false);
            Long userId = comment.getUserId();
            // TODO 优化
            User user = userService.getById(userId);
            commentVo.setUserName(user.getUserName());
            commentVo.setUserPicture(user.getUserPicture());
            return commentVo;
        }).collect(Collectors.toList());
        // 统计回复数
        List<Long> commentIdList = list.stream().map(CommentVo::getId).collect(Collectors.toList());
        wrapper.in(Comment::getParentId, commentIdList);
        wrapper.select(Comment::getParentId);
        List<Long> rootIds = this.listObjs(wrapper, obj -> (Long) obj);
        Map<Long, Long> replayCountMap = rootIds.stream().collect(Collectors.groupingBy(k -> k, Collectors.counting()));
        list.forEach(commentVo -> {
            Long count = replayCountMap.get(commentVo.getId());
            if(count == null) {
                commentVo.setReplyCount(0L);
            }else {
                commentVo.setReplyCount(count);
            }
        });
        return list;
    }

    @Override
    public List<CommentVo> queryReplyComment(Long commentId) {
        ThrowUtil.throwIf(commentId == null || commentId <= 0, () -> new RequestParamException("请求参数错误"));
        Comment comment = this.getById(commentId);
        ThrowUtil.throwIf(comment == null, () -> new DataBaseAbsentException("评论不存在"));
        // 查询回复评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, commentId);
        wrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> commentList = this.list(wrapper);
        return commentToVo(commentList);
    }

    @Override
    public void deleteComment(Long commentId) {
        ThrowUtil.throwIf(commentId == null || commentId <= 0, () -> new RequestParamException("请求参数错误"));
        Comment comment = this.getById(commentId);
        ThrowUtil.throwIf(comment == null, () -> new DataBaseAbsentException("当前评论不存在"));
        // 判断是否是自己发的评论
        UserVo currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();
        ThrowUtil.throwIf(!userId.equals(comment.getUserId()), () -> new NoAuthException("没有权力删除别人的评论"));
        // 如果是根评论全部删除
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (comment.getRootId() == null) {
            wrapper.in(Comment::getRootId, comment.getId());
        }else {
            // 如果是子评论，把回复相关内容全部删除
            wrapper.in(Comment::getParentId, comment.getId());
        }
        wrapper.or().eq(Comment::getId, commentId);
        boolean remove = this.remove(wrapper);
        ThrowUtil.throwIf(!remove, () -> new DataBaseAbsentException("删除失败"));
    }

    public void validComment(PublishCommentRequest publishCommentRequest) {
        ThrowUtil.throwIf(publishCommentRequest == null, () -> new RequestParamException("请求参数错误"));
        String content = publishCommentRequest.getContent();
        ThrowUtil.throwIf(StringUtils.isBlank(content), () -> new RequestParamException("内容不能为空"));
        Long userId = publishCommentRequest.getUserId();
        ThrowUtil.throwIf(userId == null || userId <= 0, () -> new RequestParamException("用户信息错误"));
        Long rootId = publishCommentRequest.getRootId();
        Long parentId = publishCommentRequest.getParentId();
        if(rootId != null && parentId != null) {
            LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Comment::getId, rootId).or().eq(Comment::getId, parentId);
            List<Comment> list = this.list(wrapper);
            if (rootId.equals(parentId)) {
                ThrowUtil.throwIf(list.size() != 1, () -> new DataBaseAbsentException("根或父评论出错"));
            }else{
                ThrowUtil.throwIf(list.size() != 2, () -> new DataBaseAbsentException("根或父评论出错"));
            }
        }
    }
}




