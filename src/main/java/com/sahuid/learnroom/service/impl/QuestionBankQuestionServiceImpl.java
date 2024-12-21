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
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【question_bank_question】的数据库操作Service实现
* @createDate 2024-12-12 12:08:39
*/
@Service
@Slf4j
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
        Long userId = currentUser.getId();
        // 查询是否存在这些题目
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.in(Question::getId, questionIds);
        questionWrapper.select(Question::getId);
        List<Long> existQuestionId = questionService.listObjs(questionWrapper, obj -> (Long)obj);
        ThrowUtil.throwIf(existQuestionId.isEmpty(), () -> new DataBaseAbsentException("不存在当前题目"));
        // 查询是否存在这个题库
        LambdaQueryWrapper<QuestionBank> bankWrapper = new LambdaQueryWrapper<>();
        bankWrapper.eq(QuestionBank::getId, questionBankId);
        QuestionBank questionBank = questionBankService.getOne(bankWrapper);
        ThrowUtil.throwIf(questionBank == null, () -> new DataBaseAbsentException("不存在当前题库"));
        // 过滤不存在的题目
        List<Long> validIdList = questionIds.stream().filter(existQuestionId::contains).collect(Collectors.toList());
        // 去掉题库中已存在的题目
        LambdaQueryWrapper<QuestionBankQuestion> questionAndBankWrapper = new LambdaQueryWrapper<>();
        questionAndBankWrapper.eq(QuestionBankQuestion::getQuestionBandId, questionBankId);
        List<QuestionBankQuestion> questionAndBankList = this.list(questionAndBankWrapper);
        Set<Long> relationQuestionIds = questionAndBankList.stream().map(QuestionBankQuestion::getQuestionId).collect(Collectors.toSet());
        List<Long> notExistRelationQuestionIds = validIdList.stream().filter(id -> !relationQuestionIds.contains(id)).collect(Collectors.toList());
        // 将题目添加到题库中
        int batchSize = 500;
        int totalQuestionSize = notExistRelationQuestionIds.size();
        // 异步批量添加
        // 自定义线程池
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                20,                         // 核心线程数
                50,                        // 最大线程数
                60L,                       // 线程空闲存活时间
                TimeUnit.SECONDS,           // 存活时间单位
                new LinkedBlockingQueue<>(10000),  // 阻塞队列容量
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：由调用线程处理任务
        );
        QuestionBankQuestionService questionBankQuestionService = (QuestionBankQuestionService) AopContext.currentProxy();
        List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();
        for(int i = 0; i < totalQuestionSize; i += batchSize) {
            List<Long> batchQuestionIdList = notExistRelationQuestionIds.subList(i, Math.min(i + batchSize, totalQuestionSize));
            List<QuestionBankQuestion> questionBankQuestionList = batchQuestionIdList.stream().map(questionId -> {
                QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                questionBankQuestion.setQuestionBandId(questionBankId);
                questionBankQuestion.setUserId(userId);
                questionBankQuestion.setQuestionId(questionId);
                return questionBankQuestion;
            }).collect(Collectors.toList());

            CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> questionBankQuestionService.batchAddQuestion(questionBankQuestionList), customExecutor);
            completableFutureList.add(completableFuture);
        }
        // 阻塞等待全部任务完成
        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0])).join();


    }

    /**
     * 批量将题目添加到题库 (避免长事务)
     * @param questionBankQuestionList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestion(List<QuestionBankQuestion> questionBankQuestionList) {
        try {
            boolean add = this.saveBatch(questionBankQuestionList);
            ThrowUtil.throwIf(!add, () -> new DataOperationException("添加题库失败"));
        }catch (DataIntegrityViolationException e) {
            log.error("数据库唯一键冲突或违反其他完整性约束, 错误信息: {}", e.getMessage());
            throw new DataOperationException("题目已存在于该题库，无法重复添加");
        } catch (DataAccessException e) {
            log.error("数据库连接问题、事务问题等导致操作失败, 错误信息: {}", e.getMessage());
            throw new DataOperationException("数据库操作失败");
        } catch (Exception e) {
            // 捕获其他异常，做通用处理
            log.error("添加题目到题库时发生未知错误，错误信息: {}", e.getMessage());
            throw new DataOperationException("向题库添加题目失败");
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




