package com.sahuid.learnroom.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.model.dto.QuestionDto;
import com.sahuid.learnroom.model.req.question.*;
import com.sahuid.learnroom.model.entity.Question;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sahuid.learnroom.model.vo.QuestionViewHistoryVo;
import com.sahuid.learnroom.model.vo.QuestionVo;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【question】的数据库操作Service
* @createDate 2024-12-12 12:08:39
*/
public interface QuestionService extends IService<Question> {

    void addQuestion(AddQuestionRequest addQuestionRequest);

    void updateQuestion(UpdateQuestionRequest updateQuestionRequest);

    QuestionVo queryQuestionById(Long id);

    /**
     * 分页查询题目列表
     * @param queryQuestionByPageRequest
     * @return
     */
    Page<Question> queryQuestionByPage(QueryQuestionByPageRequest queryQuestionByPageRequest);

    /**
     * 增加浏览量和浏览记录
     * @param increaseQuestionViewCountRequest
     */
    void increaseQuestionViewCount(IncreaseQuestionViewCountRequest increaseQuestionViewCountRequest);

    /**
     * 查询题目的浏览记录
     * @param queryQuestionViewHistoryRequest
     * @return
     */
    PageResult<QuestionViewHistoryVo> getQuestionViewHistory(QueryQuestionViewHistoryRequest queryQuestionViewHistoryRequest);

    /**
     * 从 ES 中查询数据
     * @param queryQuestionByPageRequest
     * @return
     */
    PageResult<Question> queryFromEs(QueryQuestionByPageRequest queryQuestionByPageRequest);
}
