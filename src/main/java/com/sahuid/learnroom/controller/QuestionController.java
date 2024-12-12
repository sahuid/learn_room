package com.sahuid.learnroom.controller;

import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.dto.question.AddQuestionRequest;
import com.sahuid.learnroom.model.dto.question.UpdateQuestionRequest;
import com.sahuid.learnroom.model.entity.Question;
import com.sahuid.learnroom.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/question")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionController {

    private final QuestionService questionService;

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
}
