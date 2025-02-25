package com.sahuid.learnroom.controller;

import com.sahuid.learnroom.annotation.RoleCheck;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.constants.UserConstant;
import com.sahuid.learnroom.model.req.questionAndBank.BatchAddQuestionToBankRequest;
import com.sahuid.learnroom.model.req.questionAndBank.BatchRemoveQuestionToBankRequest;
import com.sahuid.learnroom.model.req.questionbank.QuestionAndBankRequest;
import com.sahuid.learnroom.service.QuestionBankQuestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/questionAndBank")
public class QuestionAndBankController {

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @PostMapping("/add/batch")
    @RoleCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R<Void> batchAddQuestionToBank(@RequestBody BatchAddQuestionToBankRequest batchAddQuestionToBankRequest) {
        questionBankQuestionService.batchAddQuestionToBank(batchAddQuestionToBankRequest);
        return R.ok("添加成功");
    }

    @PostMapping("/remove/batch")
    @RoleCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R<Void> batchRemoveQuestionToBank(@RequestBody BatchRemoveQuestionToBankRequest batchRemoveQuestionToBankRequest) {
        questionBankQuestionService.batchRemoveQuestionToBank(batchRemoveQuestionToBankRequest);
        return R.ok("删除成功");
    }

    @PostMapping("/addQuestion")
    @RoleCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R<Void> addQuestionToBank(@RequestBody QuestionAndBankRequest questionAndBankRequest) {
        questionBankQuestionService.addQuestionToBank(questionAndBankRequest);
        return R.ok("添加成功");
    }

    @PostMapping("/deleteQuestion")
    @RoleCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R<Void> deleteQuestionFromBank(@RequestBody QuestionAndBankRequest questionAndBankRequest) {
        questionBankQuestionService.deleteQuestionFromBank(questionAndBankRequest);
        return R.ok("删除成功");
    }
}
