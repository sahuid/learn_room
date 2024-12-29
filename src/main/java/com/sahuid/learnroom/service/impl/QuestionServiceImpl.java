package com.sahuid.learnroom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.exception.DataBaseAbsentException;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.model.dto.question.*;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.model.entity.QuestionView;
import com.sahuid.learnroom.model.vo.QuestionViewHistoryVo;
import com.sahuid.learnroom.model.vo.QuestionVo;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.LikesService;
import com.sahuid.learnroom.service.QuestionService;
import com.sahuid.learnroom.mapper.QuestionMapper;
import com.sahuid.learnroom.service.QuestionViewService;
import com.sahuid.learnroom.service.UserService;
import com.sahuid.learnroom.utils.ThrowUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【question】的数据库操作Service实现
* @createDate 2024-12-12 12:08:39
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

    @Resource
    private UserService userService;

    @Resource
    private LikesService likesService;

    @Resource
    private QuestionViewService questionViewService;

    @Override
    public void addQuestion(AddQuestionRequest addQuestionRequest, HttpServletRequest request) {
        if (addQuestionRequest == null) {
            throw new RequestParamException("请求参数错误");
        }

        String title = addQuestionRequest.getTitle();
        String answer = addQuestionRequest.getAnswer();
        if (StrUtil.isBlank(title) || StrUtil.isBlank(answer)) {
            throw new RequestParamException("题目或者答案不能为空");
        }

        Question question = new Question();
        BeanUtil.copyProperties(addQuestionRequest, question, false);

        List<String> tags = addQuestionRequest.getTags();
        String tagsStr = JSONUtil.parseArray(JSONUtil.toJsonStr(tags)).toString();
        question.setTags(tagsStr);

        UserVo currentUser = userService.getCurrentUser(request);
        question.setUserId(currentUser.getId());
        boolean save = this.save(question);
        ThrowUtil.throwIf(!save, () -> new DataOperationException("添加失败"));

    }

    @Override
    public void updateQuestion(UpdateQuestionRequest updateQuestionRequest) {
        if (updateQuestionRequest == null) {
            throw new RequestParamException("请求参数错误");
        }

        Long id = updateQuestionRequest.getId();
        Question question = this.getById(id);
        if (question == null) {
            throw new DataBaseAbsentException("当前题目不存在");
        }

        String answer = updateQuestionRequest.getAnswer();
        String context = updateQuestionRequest.getContext();
        String title = updateQuestionRequest.getTitle();
        List<String> tags = updateQuestionRequest.getTags();
        if (StrUtil.isNotBlank(answer)) {
            question.setAnswer(answer);
        }
        if (StrUtil.isNotBlank(context)) {
            question.setAnswer(context);
        }
        if (StrUtil.isNotBlank(title)) {
            question.setTitle(title);
        }
        if (tags != null) {
            String tagsStr = JSONUtil.parseArray(JSONUtil.toJsonStr(tags)).toString();
            question.setTags(tagsStr);
        }
        boolean updateById = this.updateById(question);
        ThrowUtil.throwIf(!updateById, () -> new DataOperationException("修改失败"));
    }

    @Override
    public QuestionVo queryQuestionById(Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new RequestParamException("请求参数错误");
        }
        Question question = this.getById(id);
        if (question == null) {
            throw new DataBaseAbsentException("当前题目不存在");
        }
        QuestionVo questionVo = new QuestionVo();
        BeanUtil.copyProperties(question, questionVo, false);
        return questionVo;
    }

    /**
     * 分页查询题目列表
     * @param queryQuestionByPageRequest
     * @return
     */
    @Override
    public Page<Question> queryQuestionByPage(QueryQuestionByPageRequest queryQuestionByPageRequest) {
        ThrowUtil.throwIf(queryQuestionByPageRequest == null, () -> new RequestParamException("请求参数错误"));
        int currPage = queryQuestionByPageRequest.getPage();
        int pageSize = queryQuestionByPageRequest.getPageSize();
        Page<Question> page = new Page<>(currPage, pageSize);
        return this.page(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseQuestionViewCount(IncreaseQuestionViewCountRequest increaseQuestionViewCountRequest) {
        ThrowUtil.throwIf(increaseQuestionViewCountRequest == null, () -> new RequestParamException("请求参数错误"));
        Long questionId = increaseQuestionViewCountRequest.getQuestionId();
        Long userId = increaseQuestionViewCountRequest.getUserId();
        ThrowUtil.throwIf(questionId == null || userId == null || questionId <= 0 || userId <= 0,
                () -> new RequestParamException("请求参数错误"));
        // 修改浏览量
        LambdaUpdateWrapper<Question> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Question::getId, questionId);
        wrapper.setSql("viewCount = viewCount + 1");
        boolean update = this.update(wrapper);
        ThrowUtil.throwIf(!update, () -> new DataOperationException("计数失败"));
        // 插入或修改浏览记录
        LambdaQueryWrapper<QuestionView> viewLambdaQueryWrapper = new LambdaQueryWrapper<>();
        viewLambdaQueryWrapper.eq(QuestionView::getUserId, userId);
        viewLambdaQueryWrapper.eq(QuestionView::getQuestionId, questionId);
        QuestionView questionView = questionViewService.getOne(viewLambdaQueryWrapper);
        if (questionView == null) {
            // 不存在创建一条数据
            questionView = new QuestionView();
            questionView.setQuestionId(questionId);
            questionView.setUserId(userId);
            boolean save = questionViewService.save(questionView);
            ThrowUtil.throwIf(!save, () -> new DataOperationException("计数失败"));
            return;
        }
        // 存在修改查询次数和查看时间
        questionView.setViewCount(questionView.getViewCount() + 1);
        questionView.setViewTime(new Date(System.currentTimeMillis()));
        boolean updateById = questionViewService.updateById(questionView);
        ThrowUtil.throwIf(!updateById, () -> new DataOperationException("计数失败"));
    }

    @Override
    public PageResult<QuestionViewHistoryVo> getQuestionViewHistory(QueryQuestionViewHistoryRequest queryQuestionViewHistoryRequest) {
        ThrowUtil.throwIf(queryQuestionViewHistoryRequest == null, () -> new RequestParamException("请求参数错误"));
        Long userId = queryQuestionViewHistoryRequest.getUserId();
        ThrowUtil.throwIf(userId == null || userId <= 0, () -> new RequestParamException("请求参数错误"));
        int currentPage = queryQuestionViewHistoryRequest.getPage();
        int pageSize = queryQuestionViewHistoryRequest.getPageSize();
        Page<QuestionView> page = new Page<>(currentPage, pageSize);
        // 查询记录表
        LambdaQueryWrapper<QuestionView> viewLambdaQueryWrapper = new LambdaQueryWrapper<>();
        viewLambdaQueryWrapper.eq(QuestionView::getUserId, userId);
        viewLambdaQueryWrapper.orderBy(true, false, QuestionView::getViewTime);
        questionViewService.page(page, viewLambdaQueryWrapper);
        // 查询题目
        List<QuestionView> records = page.getRecords();
        List<Long> questionIds = records.stream().map(QuestionView::getQuestionId).collect(Collectors.toList());
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        // 确保顺序
        wrapper.in(Question::getId, questionIds);
        wrapper.last("ORDER BY FIELD(id," + StrUtil.join(",", questionIds) + ")");
        List<Question> list = this.list(wrapper);
        // 组装 VO
        Map<Long, QuestionView> idAndViewLogMap = records.stream().collect(Collectors.toMap(QuestionView::getQuestionId, questionView -> questionView));
        List<QuestionViewHistoryVo> resultList = list.stream().map(question -> {
            QuestionViewHistoryVo questionViewHistoryVo = new QuestionViewHistoryVo();
            BeanUtil.copyProperties(question, questionViewHistoryVo);
            QuestionView questionView = idAndViewLogMap.get(question.getId());
            questionViewHistoryVo.setViewTime(questionView.getViewTime());
            questionViewHistoryVo.setViewCount(questionView.getViewCount());
            return questionViewHistoryVo;
        }).collect(Collectors.toList());
        PageResult<QuestionViewHistoryVo> pageResult = new PageResult<>();
        pageResult.setData(resultList);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }
}




