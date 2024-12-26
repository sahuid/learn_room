package com.sahuid.learnroom.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.model.dto.questionbank.*;
import com.sahuid.learnroom.model.entity.QuestionBank;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sahuid.learnroom.model.vo.QuestionBankVo;

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

    QuestionBankVo queryBankById(QueryQuestionBankOneRequest queryQuestionBankOneRequest);

    Page<QuestionBank> queryQuestionBankByPage(QueryQuestionBankByPageRequest queryQuestionBankByPageRequest);

    void deleteQuestionBanks(List<Long> ids);
    /**
     * 不分页查询所有的题库列表
     * @return
     */
    List<QuestionBank> queryBankList();
}
