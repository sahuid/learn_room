package com.sahuid.learnroom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.exception.DataBaseAbsentException;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.model.dto.comment.PublishCommentRequest;
import com.sahuid.learnroom.model.dto.comment.QueryCommentByPageRequest;
import com.sahuid.learnroom.model.entity.Comment;
import com.sahuid.learnroom.model.entity.User;
import com.sahuid.learnroom.model.vo.CommentVo;
import com.sahuid.learnroom.service.CommentService;
import com.sahuid.learnroom.mapper.CommentMapper;
import com.sahuid.learnroom.service.UserService;
import com.sahuid.learnroom.utils.ThrowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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
    public PageResult<CommentVo> queryCommentByPage(QueryCommentByPageRequest queryCommentByPageRequest) {
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
        List<CommentVo> list = records.stream().map(comment -> {
            CommentVo commentVo = new CommentVo();
            BeanUtil.copyProperties(comment, commentVo, false);
            Long userId = comment.getUserId();
            User user = userService.getById(userId);
            commentVo.setUserName(user.getUserName());
            commentVo.setUserPicture(user.getUserPicture());
            return commentVo;
        }).collect(Collectors.toList());
        result.setData(list);
        return result;
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
            wrapper.eq(Comment::getRootId, rootId).or().eq(Comment::getParentId, parentId);
            List<Comment> list = this.list(wrapper);
            if (rootId.equals(parentId)) {
                ThrowUtil.throwIf(list.size() != 1, () -> new DataBaseAbsentException("根或父评论出错"));
            }else{
                ThrowUtil.throwIf(list.size() != 2, () -> new DataBaseAbsentException("根或父评论出错"));
            }
        }
    }
}




