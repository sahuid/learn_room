package com.sahuid.learnroom.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.model.dto.questionbank.AddQuestionBankRequest;
import com.sahuid.learnroom.model.dto.questionbank.QueryQuestionBankRequest;
import com.sahuid.learnroom.model.dto.questionbank.UpdateQuestionBankRequest;
import com.sahuid.learnroom.model.entity.QuestionBank;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Lenovo
* @description 针对表【question_band】的数据库操作Service
* @createDate 2024-12-12 12:08:39
*/
public interface QuestionBankService extends IService<QuestionBank> {

    void addQuestionBank(AddQuestionBankRequest addQuestionBankRequest, HttpServletRequest request);

    void updateQuestionBank(UpdateQuestionBankRequest updateQuestionBankRequest, HttpServletRequest request);

    QuestionBank queryBankById(Long id);

    Page<QuestionBank> queryQuestionBankByPage(QueryQuestionBankRequest queryQuestionBankRequest);

    void deleteQuestionBanks(List<Long> ids);
}
