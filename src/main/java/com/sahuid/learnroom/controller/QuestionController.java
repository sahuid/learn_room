package com.sahuid.learnroom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.annotation.RoleCheck;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.constants.UserConstant;
import com.sahuid.learnroom.model.dto.question.AddQuestionRequest;
import com.sahuid.learnroom.model.dto.question.QueryQuestionByPageRequest;
import com.sahuid.learnroom.model.dto.question.UpdateQuestionRequest;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.service.QuestionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RequestMapping("/question")
@RestController
public class QuestionController {

    @Resource
    private  QuestionService questionService;

    @PostMapping("/add")
    public R<Void> addQuestion(@RequestBody AddQuestionRequest addQuestionRequest, HttpServletRequest request) {
        questionService.addQuestion(addQuestionRequest, request);
        return R.ok("添加成功");
    }


    @PostMapping("/update")
    public R<Void> updateQuestion(@RequestBody UpdateQuestionRequest updateQuestionRequest) {
        questionService.updateQuestion(updateQuestionRequest);
        return R.ok("修改成功");
    }

    @GetMapping("/queryOne")
    public R<Question> queryQuestion(@RequestParam("id") Long id){
        Question question = questionService.queryQuestionById(id);
        return R.ok(question, "查询成功");
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
}
