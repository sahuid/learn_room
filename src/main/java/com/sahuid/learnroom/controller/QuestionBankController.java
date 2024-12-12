package com.sahuid.learnroom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.dto.questionbank.AddQuestionBankRequest;
import com.sahuid.learnroom.model.dto.questionbank.QueryQuestionBankRequest;
import com.sahuid.learnroom.model.dto.questionbank.UpdateQuestionBankRequest;
import com.sahuid.learnroom.model.entity.QuestionBank;
import com.sahuid.learnroom.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/questionBank")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    @PostMapping("/add")
    public R<Void> addQuestionBank(@RequestBody AddQuestionBankRequest addQuestionBankRequest, HttpServletRequest request) {
        questionBankService.addQuestionBank(addQuestionBankRequest, request);
        return R.ok("添加成功");
    }

    @PostMapping("/update")
    public R<Void> updateQuestionBank(@RequestBody UpdateQuestionBankRequest updateQuestionBankRequest, HttpServletRequest request) {
        questionBankService.updateQuestionBank(updateQuestionBankRequest, request);
        return R.ok("修改成功");
    }


    @GetMapping("/queryOne")
    public R<QuestionBank> queryQuestionBankById(@RequestParam("id") Long id) {
        QuestionBank questionBank = questionBankService.queryBankById(id);
        return R.ok(questionBank, "查询成功");
    }

    @GetMapping("/queryPage")
    public R<Page<QuestionBank>> queryQuestionBankByPage(QueryQuestionBankRequest queryQuestionBankRequest){
        Page<QuestionBank> page = questionBankService.queryQuestionBankByPage(queryQuestionBankRequest);
        return R.ok(page, "查询成功");
    }

    @GetMapping("/delete")
    public R<Void> deleteQuestionBank(@RequestParam("ids")List<Long>ids) {
        questionBankService.deleteQuestionBanks(ids);
        return R.ok("删除成功");
    }
}
