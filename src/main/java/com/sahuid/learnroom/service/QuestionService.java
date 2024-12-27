package com.sahuid.learnroom.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.model.dto.question.AddQuestionRequest;
import com.sahuid.learnroom.model.dto.question.QueryQuestionByPageRequest;
import com.sahuid.learnroom.model.dto.question.UpdateQuestionRequest;
import com.sahuid.learnroom.model.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sahuid.learnroom.model.vo.QuestionVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author Lenovo
* @description 针对表【question】的数据库操作Service
* @createDate 2024-12-12 12:08:39
*/
public interface QuestionService extends IService<Question> {

    void addQuestion(AddQuestionRequest addQuestionRequest, HttpServletRequest request);

    void updateQuestion(UpdateQuestionRequest updateQuestionRequest);

    QuestionVo queryQuestionById(Long id, HttpServletRequest request);

    /**
     * 分页查询题目列表
     * @param queryQuestionByPageRequest
     * @return
     */
    Page<Question> queryQuestionByPage(QueryQuestionByPageRequest queryQuestionByPageRequest);
}
