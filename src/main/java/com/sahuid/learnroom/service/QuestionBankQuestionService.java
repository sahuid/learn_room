package com.sahuid.learnroom.service;

import com.sahuid.learnroom.model.dto.questionAndBank.BatchAddQuestionToBankRequest;
import com.sahuid.learnroom.model.dto.questionAndBank.BatchRemoveQuestionToBankRequest;
import com.sahuid.learnroom.model.entity.QuestionBankQuestion;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

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
     * 批量从题库中删除题目
     * @param batchRemoveQuestionToBankRequest
     * @param request
     */
    void batchRemoveQuestionToBank(BatchRemoveQuestionToBankRequest batchRemoveQuestionToBankRequest, HttpServletRequest request);
}
