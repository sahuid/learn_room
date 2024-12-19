package com.sahuid.learnroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.exception.DataBaseAbsentException;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.NoLoginException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.model.dto.questionAndBank.BatchAddQuestionToBankRequest;
import com.sahuid.learnroom.model.dto.questionAndBank.BatchRemoveQuestionToBankRequest;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.model.entity.QuestionBank;
import com.sahuid.learnroom.model.entity.QuestionBankQuestion;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.QuestionBankQuestionService;
import com.sahuid.learnroom.mapper.QuestionBankQuestionMapper;
import com.sahuid.learnroom.service.QuestionBankService;
import com.sahuid.learnroom.service.QuestionService;
import com.sahuid.learnroom.service.UserService;
import com.sahuid.learnroom.utils.ThrowUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【question_bank_question】的数据库操作Service实现
* @createDate 2024-12-12 12:08:39
*/
@Service
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion>
    implements QuestionBankQuestionService{

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    @Resource
    @Lazy
    private QuestionBankService questionBankService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestionToBank(BatchAddQuestionToBankRequest batchAddQuestionToBankRequest, HttpServletRequest request) {
        ThrowUtil.throwIf(batchAddQuestionToBankRequest == null, () -> new RequestParamException("请求参数错误"));
        Long questionBankId = batchAddQuestionToBankRequest.getQuestionBankId();
        List<Long> questionIds = batchAddQuestionToBankRequest.getQuestionIds();
        // 校验参数合法性
        ThrowUtil.throwIf(questionBankId == null || questionBankId <= 0, () -> new RequestParamException("题库不存在"));
        ThrowUtil.throwIf(questionIds.isEmpty(), () -> new RequestParamException("未选择题库"));
        // 校验用户
        UserVo currentUser = userService.getCurrentUser(request);
        ThrowUtil.throwIf(currentUser == null, () -> new NoLoginException("当前未登录"));
        // 查询是否存在这些题目
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.in(Question::getId, questionIds);
        List<Question> questions = questionService.list(questionWrapper);
        ThrowUtil.throwIf(questions.isEmpty(), () -> new DataBaseAbsentException("不存在当前题目"));
        // 查询是否存在这个题库
        LambdaQueryWrapper<QuestionBank> bankWrapper = new LambdaQueryWrapper<>();
        bankWrapper.eq(QuestionBank::getId, questionBankId);
        QuestionBank questionBank = questionBankService.getOne(bankWrapper);
        ThrowUtil.throwIf(questionBank == null, () -> new DataBaseAbsentException("不存在当前题库"));
        // 过滤不存在的题目
        Set<Long> existQuestionId = questions.stream().map(Question::getId).collect(Collectors.toSet());
        List<Long> validIdList = questionIds.stream().filter(existQuestionId::contains).collect(Collectors.toList());
        // 去掉题库中已存在的题目
        LambdaQueryWrapper<QuestionBankQuestion> questionAndBankWrapper = new LambdaQueryWrapper<>();
        questionAndBankWrapper.eq(QuestionBankQuestion::getQuestionBandId, questionBankId);
        List<QuestionBankQuestion> questionAndBankList = this.list(questionAndBankWrapper);
        Set<Long> relationQuestionIds = questionAndBankList.stream().map(QuestionBankQuestion::getQuestionId).collect(Collectors.toSet());
        List<Long> notExistRelationQuestionIds = validIdList.stream().filter(id -> !relationQuestionIds.contains(id)).collect(Collectors.toList());
        // 将题目添加到题库中
        for (Long questionId : notExistRelationQuestionIds) {
            QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
            questionBankQuestion.setQuestionBandId(questionBankId);
            questionBankQuestion.setUserId(currentUser.getId());
            questionBankQuestion.setQuestionId(questionId);
            boolean save = this.save(questionBankQuestion);
            ThrowUtil.throwIf(!save, () -> new DataOperationException("添加题目失败"));
        }
    }

    @Override
    public void batchRemoveQuestionToBank(BatchRemoveQuestionToBankRequest batchRemoveQuestionToBankRequest, HttpServletRequest request) {
        ThrowUtil.throwIf(batchRemoveQuestionToBankRequest == null, () -> new RequestParamException("请求参数错误"));
        Long questionBankId = batchRemoveQuestionToBankRequest.getQuestionBankId();
        List<Long> questionIds = batchRemoveQuestionToBankRequest.getQuestionIds();
        // 校验参数合法性
        ThrowUtil.throwIf(questionBankId == null || questionBankId <= 0, () -> new RequestParamException("题库不存在"));
        ThrowUtil.throwIf(questionIds.isEmpty(), () -> new RequestParamException("未选择题库"));
        // 校验用户
        UserVo currentUser = userService.getCurrentUser(request);
        ThrowUtil.throwIf(currentUser == null, () -> new NoLoginException("当前未登录"));
        // 删除题库
        for (Long questionId : questionIds) {
            LambdaQueryWrapper<QuestionBankQuestion> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(QuestionBankQuestion::getQuestionId, questionId);
            wrapper.eq(QuestionBankQuestion::getQuestionBandId, questionBankId);
            boolean remove = this.remove(wrapper);
            ThrowUtil.throwIf(!remove, () -> new DataOperationException("删除题目失败"));
        }
    }
}




