package com.sahuid.learnroom.service;

import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.model.dto.collect.CollectQuestionRequest;
import com.sahuid.learnroom.model.dto.collect.GetCollectQuestionRequest;
import com.sahuid.learnroom.model.dto.collect.HasCollectQuestionRequest;
import com.sahuid.learnroom.model.dto.collect.UnCollectQuestionRequest;
import com.sahuid.learnroom.model.entity.QuestionCollect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sahuid.learnroom.model.vo.QuestionViewHistoryVo;

/**
* @author Lenovo
* @description 针对表【question_collect】的数据库操作Service
* @createDate 2024-12-30 11:12:31
*/
public interface QuestionCollectService extends IService<QuestionCollect> {

    /**
     * 收藏题目
     * @param collectQuestionRequest
     */
    void questionCollect(CollectQuestionRequest collectQuestionRequest);

    /**
     * 查询题目收藏数量
     * @param questionId
     * @return
     */
    int getCollectCount(Long questionId);

    /**
     * 判断是否收藏了题目
     * @param hasCollectQuestionRequest
     * @return
     */
    Boolean hasCollectQuestion(HasCollectQuestionRequest hasCollectQuestionRequest);

    /**
     * 取消收藏
     * @param unCollectQuestionRequest
     */
    void unCollectQuestion(UnCollectQuestionRequest unCollectQuestionRequest);

    /**
     * 查询收藏题目列表
     * @param getCollectQuestionRequest
     * @return
     */
    PageResult<QuestionViewHistoryVo> getCollectQuestionList(GetCollectQuestionRequest getCollectQuestionRequest);
}
