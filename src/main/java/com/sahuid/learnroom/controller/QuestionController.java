package com.sahuid.learnroom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.annotation.RoleCheck;
import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.constants.UserConstant;
import com.sahuid.learnroom.model.req.question.*;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.model.vo.QuestionViewHistoryVo;
import com.sahuid.learnroom.model.vo.QuestionVo;
import com.sahuid.learnroom.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RequestMapping("/question")
@RestController
public class QuestionController {

    @Resource
    private  QuestionService questionService;

    /**
     * 添加题目
     * @param addQuestionRequest
     * @return
     */
    @PostMapping("/add")
    public R<Void> addQuestion(@RequestBody AddQuestionRequest addQuestionRequest) {
        questionService.addQuestion(addQuestionRequest);
        return R.ok("添加成功");
    }


    /**
     * 修改题目
     * @param updateQuestionRequest
     * @return
     */
    @PostMapping("/update")
    public R<Void> updateQuestion(@RequestBody UpdateQuestionRequest updateQuestionRequest) {
        questionService.updateQuestion(updateQuestionRequest);
        return R.ok("修改成功");
    }

    /**
     * 查询单个题目
     * @param id
     * @return
     */
    @GetMapping("/queryOne")
    public R<QuestionVo> queryQuestion(@RequestParam("id") Long id){
        QuestionVo questionVo = questionService.queryQuestionById(id);
        return R.ok(questionVo, "查询成功");
    }

    /**
     * 分页查询题目列表
     * @param queryQuestionByPageRequest
     * @return
     */
    @GetMapping("/queryPage")
    @RoleCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R<Page<Question>> queryQuestionByPage(QueryQuestionByPageRequest queryQuestionByPageRequest) {
        Page<Question> page = questionService.queryQuestionByPage(queryQuestionByPageRequest);
        return R.ok(page, "查询成功");
    }


    /**
     * 题目浏览量增加
     * @param increaseQuestionViewCountRequest
     * @return
     */
    @GetMapping("/view/increase")
    public R<Void> increaseQuestionViewCount(IncreaseQuestionViewCountRequest increaseQuestionViewCountRequest) {
        questionService.increaseQuestionViewCount(increaseQuestionViewCountRequest);
        return R.ok();
    }


    /**
     * 查询题目查看历史记录
     * @param queryQuestionViewHistoryRequest
     * @return
     */
    @GetMapping("/view/history")
    public R<PageResult<QuestionViewHistoryVo>> getQuestionViewHistory(QueryQuestionViewHistoryRequest queryQuestionViewHistoryRequest) {
        PageResult<QuestionViewHistoryVo> pageResult = questionService.getQuestionViewHistory(queryQuestionViewHistoryRequest);
        return R.ok(pageResult, "查询成功");
    }

    /**
     * 通过 ES 搜索题目
     * @param queryQuestionByPageRequest
     * @return
     */
    @PostMapping("/search")
    public R<PageResult<Question>> searchQuestionByPage(@RequestBody QueryQuestionByPageRequest queryQuestionByPageRequest) {
        PageResult<Question> pageResult = questionService.queryFromEs(queryQuestionByPageRequest);
        return R.ok(pageResult, "查询成功");
    }

    @PostMapping("/ai/generate/question")
    public R<Boolean> aiGenerateQuestions(@RequestBody AIGenerateQuestionRequest aiGenerateQuestionRequest) {
        questionService.aiGenerateQuestions(aiGenerateQuestionRequest);
        return R.ok(true);
    }
}
