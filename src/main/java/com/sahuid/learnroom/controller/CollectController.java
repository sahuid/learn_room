package com.sahuid.learnroom.controller;


import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.req.collect.CollectQuestionRequest;
import com.sahuid.learnroom.model.req.collect.GetCollectQuestionRequest;
import com.sahuid.learnroom.model.req.collect.HasCollectQuestionRequest;
import com.sahuid.learnroom.model.req.collect.UnCollectQuestionRequest;
import com.sahuid.learnroom.model.vo.QuestionViewHistoryVo;
import com.sahuid.learnroom.service.QuestionCollectService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/collect")
public class CollectController {

    @Resource
    private QuestionCollectService questionCollectService;

    @PostMapping
    public R<Void> questionCollect(@RequestBody CollectQuestionRequest collectQuestionRequest) {
        questionCollectService.questionCollect(collectQuestionRequest);
        return R.ok("收藏成功");
    }

    @GetMapping("/get/count")
    public R<Integer> questionCollect(Long questionId) {
        int count = questionCollectService.getCollectCount(questionId);
        return R.ok(count);
    }

    @GetMapping("/hasCollect")
    public R<Boolean> questionHasCollect(HasCollectQuestionRequest hasCollectQuestionRequest) {
        Boolean res = questionCollectService.hasCollectQuestion(hasCollectQuestionRequest);
        return R.ok(res);
    }

    @PostMapping("/unCollect")
    public R<Void> unCollectQuestion(@RequestBody UnCollectQuestionRequest unCollectQuestionRequest) {
        questionCollectService.unCollectQuestion(unCollectQuestionRequest);
        return R.ok("取消成功");
    }


    @GetMapping("/getCollectList")
    public R<PageResult<QuestionViewHistoryVo>> getCollectQuestionList(GetCollectQuestionRequest getCollectQuestionRequest) {
        PageResult<QuestionViewHistoryVo> pageResult = questionCollectService.getCollectQuestionList(getCollectQuestionRequest);
        return R.ok(pageResult);
    }
}
