package com.sahuid.learnroom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.exception.DataBaseAbsentException;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.model.req.questionbank.*;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.model.entity.QuestionBank;
import com.sahuid.learnroom.model.entity.QuestionBankQuestion;
import com.sahuid.learnroom.model.vo.QuestionBankVo;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.QuestionBankQuestionService;
import com.sahuid.learnroom.service.QuestionBankService;
import com.sahuid.learnroom.mapper.QuestionBandMapper;
import com.sahuid.learnroom.service.QuestionService;
import com.sahuid.learnroom.service.UserService;
import com.sahuid.learnroom.utils.ThrowUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 * @description 针对表【question_band】的数据库操作Service实现
 * @createDate 2024-12-12 12:08:39
 */
@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBandMapper, QuestionBank>
        implements QuestionBankService {

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Override
    public void addQuestionBank(AddQuestionBankRequest addQuestionBankRequest) {
        if (addQuestionBankRequest == null) {
            throw new RequestParamException("请求参数不能为空");
        }
        String title = addQuestionBankRequest.getTitle();
        if (StrUtil.isBlank(title)) {
            throw new RequestParamException("标题不能为空");
        }
        QuestionBank questionBand = new QuestionBank();
        BeanUtil.copyProperties(addQuestionBankRequest, questionBand, false);

        UserVo currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        questionBand.setUserId(userId);

        boolean save = this.save(questionBand);
        ThrowUtil.throwIf(!save, () -> new DataOperationException("题库添加失败"));
    }

    @Override
    public void updateQuestionBank(UpdateQuestionBankRequest updateQuestionBankRequest) {
        if (updateQuestionBankRequest == null) {
            throw new RequestParamException("请求参数不能为空");
        }

        Long id = updateQuestionBankRequest.getId();
        if (id == null || id <= 0) {
            throw new RequestParamException("请求参数错误");
        }

        QuestionBank questionBand = this.getById(id);
        if (questionBand == null) {
            throw new DataBaseAbsentException("当前题库不存在");
        }
        String title = updateQuestionBankRequest.getTitle();
        String description = updateQuestionBankRequest.getDescription();
        String picture = updateQuestionBankRequest.getPicture();
        if (StrUtil.isNotBlank(title)) {
            questionBand.setTitle(title);
        }
        if (StrUtil.isNotBlank(description)) {
            questionBand.setDescription(description);
        }
        if (StrUtil.isNotBlank(picture)) {
            questionBand.setPicture(picture);
        }
        boolean updateById = this.updateById(questionBand);
        ThrowUtil.throwIf(!updateById, () -> new DataOperationException("题库修改失败"));
    }

    @Override
    public QuestionBankVo queryBankById(QueryQuestionBankOneRequest queryQuestionBankOneRequest) {
        ThrowUtil.throwIf(queryQuestionBankOneRequest == null, () -> new RequestParamException("请求参数错误"));

        Long id = queryQuestionBankOneRequest.getId();
        ThrowUtil.throwIf(id == null || id <= 0, () -> new RequestParamException("请求参数错误"));

        Boolean showQuestion = queryQuestionBankOneRequest.getShowQuestion();
        QuestionBank questionBank = this.getById(id);
        ThrowUtil.throwIf(questionBank == null, () -> new DataBaseAbsentException("题库不存在"));
        QuestionBankVo questionBankVo = new QuestionBankVo();
        if (BooleanUtil.isTrue(showQuestion)) {
            queryQuestionInCurrentBank(queryQuestionBankOneRequest, id, questionBankVo);
        }
        questionBankVo.setQuestionBank(questionBank);

        return questionBankVo;
    }

    private void queryQuestionInCurrentBank(QueryQuestionBankOneRequest queryQuestionBankOneRequest, Long id, QuestionBankVo questionBankVo) {
        LambdaQueryWrapper<QuestionBankQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(QuestionBankQuestion::getQuestionId);
        wrapper.eq(QuestionBankQuestion::getQuestionBandId, id);
        List<QuestionBankQuestion> questionBankQuestions = questionBankQuestionService.list(wrapper);

        List<Long> questionIdList = questionBankQuestions.stream().map(QuestionBankQuestion::getQuestionId).collect(Collectors.toList());
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
        // todo 条件分页
        int page = queryQuestionBankOneRequest.getPage();
        int pageSize = queryQuestionBankOneRequest.getPageSize();
        Page<Question> questionPage = new Page<>(page, pageSize);
        if (!questionIdList.isEmpty()) {
            questionWrapper.in(Question::getId, questionIdList);
            questionService.page(questionPage, questionWrapper);
            questionBankVo.setQuestionPage(questionPage);
        }else {
            questionBankVo.setQuestionPage(null);
        }
    }

    @Override
    public Page<QuestionBank> queryQuestionBankByPage(QueryQuestionBankByPageRequest queryQuestionBankByPageRequest) {
        if (queryQuestionBankByPageRequest == null) {
            throw new RequestParamException("请求参数错误");
        }

        int currentPage = queryQuestionBankByPageRequest.getPage();
        int pageSize = queryQuestionBankByPageRequest.getPageSize();
        Page<QuestionBank> page = new Page<>(currentPage, pageSize);

        String title = queryQuestionBankByPageRequest.getTitle();
        String description = queryQuestionBankByPageRequest.getDescription();
        LambdaQueryWrapper<QuestionBank> queryWrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(title)){
            queryWrapper.like(QuestionBank::getTitle, title);
        }
        if (StrUtil.isNotBlank(description)) {
            queryWrapper.like(QuestionBank::getDescription, description);
        }
        this.page(page, queryWrapper);
        return page;
    }

    @Override
    public void deleteQuestionBanks(List<Long> ids) {
        this.removeBatchByIds(ids);
    }


    /**
     * 不分页查询所有的题库列表
     * @return
     */
    @Override
    public List<QuestionBank> queryBankList() {
        return this.list();
    }
}




