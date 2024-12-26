package com.sahuid.learnroom.service;

import com.sahuid.learnroom.model.dto.questionAndBank.BatchAddQuestionToBankRequest;
import com.sahuid.learnroom.model.dto.questionAndBank.BatchRemoveQuestionToBankRequest;
import com.sahuid.learnroom.model.dto.questionbank.QuestionAndBankRequest;
import com.sahuid.learnroom.model.entity.QuestionBankQuestion;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Lenovo
* @description 针对表【question_bank_question】的数据库操作Service
* @createDate 2024-12-12 12:08:39
*/
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 批量添加题目到题库
     * @param batchAddQuestionToBankRequest
     */
    void batchAddQuestionToBank(BatchAddQuestionToBankRequest batchAddQuestionToBankRequest, HttpServletRequest request);

    /**
     * 批量将题目添加到题库 (避免大事务)
     * @param questionBankQuestionList
     */
    @Transactional(rollbackFor = Exception.class)
    void batchAddQuestion(List<QuestionBankQuestion> questionBankQuestionList);

    /**
     * 批量从题库中删除题目
     * @param batchRemoveQuestionToBankRequest
     * @param request
     */
    void batchRemoveQuestionToBank(BatchRemoveQuestionToBankRequest batchRemoveQuestionToBankRequest, HttpServletRequest request);

    /**
     * 从题库中添加题目
     * @param questionAndBankRequest
     * @param request
     */
    void addQuestionToBank(QuestionAndBankRequest questionAndBankRequest, HttpServletRequest request);

    /**
     * 从题库中删除题目
     * @param questionAndBankRequest
     */
    void deleteQuestionFromBank(QuestionAndBankRequest questionAndBankRequest);
}
