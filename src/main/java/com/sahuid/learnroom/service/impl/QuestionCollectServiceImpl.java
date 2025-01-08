package com.sahuid.learnroom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.model.dto.collect.CollectQuestionRequest;
import com.sahuid.learnroom.model.dto.collect.GetCollectQuestionRequest;
import com.sahuid.learnroom.model.dto.collect.HasCollectQuestionRequest;
import com.sahuid.learnroom.model.dto.collect.UnCollectQuestionRequest;
import com.sahuid.learnroom.model.entity.CollectCount;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.model.entity.QuestionCollect;
import com.sahuid.learnroom.model.vo.QuestionViewHistoryVo;
import com.sahuid.learnroom.service.CollectCountService;
import com.sahuid.learnroom.service.QuestionCollectService;
import com.sahuid.learnroom.mapper.QuestionCollectMapper;
import com.sahuid.learnroom.service.QuestionService;
import com.sahuid.learnroom.utils.ThrowUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【question_collect】的数据库操作Service实现
* @createDate 2024-12-30 11:12:31
*/
@Service
public class QuestionCollectServiceImpl extends ServiceImpl<QuestionCollectMapper, QuestionCollect>
    implements QuestionCollectService{

    @Resource
    private CollectCountService collectCountService;

    @Resource
    private QuestionService questionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void questionCollect(CollectQuestionRequest collectQuestionRequest) {
        ThrowUtil.throwIf(collectQuestionRequest == null, () -> new RequestParamException("请求参数错误"));
        Long userId = collectQuestionRequest.getUserId();
        Long questionId = collectQuestionRequest.getQuestionId();
        ThrowUtil.throwIf(userId == null || questionId == null || userId <= 0 || questionId <= 0,
                () -> new RequestParamException("请求参数错误"));
        // 添加收藏记录
        QuestionCollect questionCollect = new QuestionCollect();
        questionCollect.setQuestionId(questionId);
        questionCollect.setUserId(userId);
        boolean save = this.save(questionCollect);
        ThrowUtil.throwIf(!save, () -> new DataOperationException("收藏失败"));
        // 修改收藏数
        LambdaQueryWrapper<CollectCount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CollectCount::getQuestionId, questionId);
        CollectCount collectCount = collectCountService.getOne(wrapper);
        if (collectCount == null) {
            // 没有创建一条记录
            collectCount = new CollectCount();
            collectCount.setCollectCount(1);
            collectCount.setQuestionId(questionId);
            boolean collectSave = collectCountService.save(collectCount);
            ThrowUtil.throwIf(!collectSave, () -> new DataOperationException("收藏失败"));
            return;
        }
        // 存在修改数量
        LambdaUpdateWrapper<CollectCount> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.setSql("collectCount = collectCount + 1");
        updateWrapper.eq(CollectCount::getQuestionId, questionId);
        boolean update = collectCountService.update(updateWrapper);
        ThrowUtil.throwIf(!update, () -> new DataOperationException("收藏失败"));
    }

    @Override
    public int getCollectCount(Long questionId) {
        ThrowUtil.throwIf(questionId <= 0, () -> new RequestParamException("请求参数错误"));
        LambdaQueryWrapper<CollectCount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CollectCount::getQuestionId, questionId);
        CollectCount collectCount = collectCountService.getOne(wrapper);
        return collectCount == null ? 0 : collectCount.getCollectCount();
    }

    @Override
    public Boolean hasCollectQuestion(HasCollectQuestionRequest hasCollectQuestionRequest) {
        ThrowUtil.throwIf(hasCollectQuestionRequest == null, () -> new RequestParamException("请求参数错误"));
        Long userId = hasCollectQuestionRequest.getUserId();
        Long questionId = hasCollectQuestionRequest.getQuestionId();
        ThrowUtil.throwIf(userId == null || questionId == null || userId <= 0 || questionId <= 0,
                () -> new RequestParamException("请求参数错误"));
        // 判断是否存在记录
        LambdaQueryWrapper<QuestionCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionCollect::getUserId, userId);
        wrapper.eq(QuestionCollect::getQuestionId, questionId);
        QuestionCollect questionCollect = this.getOne(wrapper);
        return questionCollect != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unCollectQuestion(UnCollectQuestionRequest unCollectQuestionRequest) {
        ThrowUtil.throwIf(unCollectQuestionRequest == null, () -> new RequestParamException("请求参数错误"));
        Long userId = unCollectQuestionRequest.getUserId();
        Long questionId = unCollectQuestionRequest.getQuestionId();
        ThrowUtil.throwIf(userId == null || questionId == null || userId <= 0 || questionId <= 0,
                () -> new RequestParamException("请求参数错误"));
        // 删除记录
        LambdaQueryWrapper<QuestionCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionCollect::getUserId, userId);
        wrapper.eq(QuestionCollect::getQuestionId, questionId);
        boolean remove = this.remove(wrapper);
        ThrowUtil.throwIf(!remove, () -> new DataOperationException("取消失败"));
        // 计数减少 1
        LambdaUpdateWrapper<CollectCount> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CollectCount::getQuestionId, questionId);
        updateWrapper.setSql("collectCount = collectCount - 1");
        boolean update = collectCountService.update(updateWrapper);
        ThrowUtil.throwIf(!update, () -> new DataOperationException("取消失败"));
    }

    @Override
    public PageResult<QuestionViewHistoryVo> getCollectQuestionList(GetCollectQuestionRequest getCollectQuestionRequest) {
        ThrowUtil.throwIf(getCollectQuestionRequest == null, () -> new RequestParamException("请求参数错误"));
        Long userId = getCollectQuestionRequest.getUserId();
        ThrowUtil.throwIf(userId == null || userId <= 0,
                () -> new RequestParamException("请求参数错误"));
        // 分页查询记录数据
        int currentPage = getCollectQuestionRequest.getPage();
        int pageSize = getCollectQuestionRequest.getPageSize();
        Page<QuestionCollect> page = new Page<>();
        LambdaQueryWrapper<QuestionCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionCollect::getUserId, userId);
        wrapper.orderBy(true, false, QuestionCollect::getCreateTime);
        this.page(page, wrapper);
        List<QuestionCollect> records = page.getRecords();
        // 非空判断
        PageResult<QuestionViewHistoryVo> pageResult = new PageResult<>();
        if (records.isEmpty()) {
            pageResult.setData(Collections.emptyList());
            pageResult.setTotal(0L);
            return pageResult;
        }
        // 查询题目信息
        Map<Long, QuestionCollect> questionIdAndLogMap = records.stream().collect(Collectors.toMap(QuestionCollect::getQuestionId, questionCollect -> questionCollect));
        List<Long> questionIds = records.stream().map(QuestionCollect::getQuestionId).collect(Collectors.toList());
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Question::getId, questionIds);
        queryWrapper.last("ORDER BY FIELD (id," + StrUtil.join(",", questionIds) + ")");
        List<Question> list = questionService.list(queryWrapper);
        // 组装数据
        List<QuestionViewHistoryVo> result = list.stream().map(question -> {
            QuestionViewHistoryVo questionViewHistoryVo = new QuestionViewHistoryVo();
            BeanUtil.copyProperties(question, questionViewHistoryVo, false);
            QuestionCollect questionCollect = questionIdAndLogMap.get(question.getId());
            questionViewHistoryVo.setViewTime(questionCollect.getCreateTime());
            return questionViewHistoryVo;
        }).collect(Collectors.toList());
        pageResult.setTotal(page.getTotal());
        pageResult.setData(result);
        return pageResult;
    }
}




