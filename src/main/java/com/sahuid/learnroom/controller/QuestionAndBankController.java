package com.sahuid.learnroom.controller;

import com.sahuid.learnroom.annotation.RoleCheck;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.constants.UserConstant;
import com.sahuid.learnroom.model.dto.questionAndBank.BatchAddQuestionToBankRequest;
import com.sahuid.learnroom.model.dto.questionAndBank.BatchRemoveQuestionToBankRequest;
import com.sahuid.learnroom.service.QuestionBankQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/questionAndBank")
public class QuestionAndBankController {

    private final QuestionBankQuestionService questionBankQuestionService;

    @PostMapping("/add/batch")
    @RoleCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R<Void> batchAddQuestionToBank(@RequestBody BatchAddQuestionToBankRequest batchAddQuestionToBankRequest, HttpServletRequest request) {
        questionBankQuestionService.batchAddQuestionToBank(batchAddQuestionToBankRequest, request);
        return R.ok("添加成功");
    }

    @PostMapping("/remove/batch")
    @RoleCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R<Void> batchRemoveQuestionToBank(@RequestBody BatchRemoveQuestionToBankRequest batchRemoveQuestionToBankRequest, HttpServletRequest request) {
        questionBankQuestionService.batchRemoveQuestionToBank(batchRemoveQuestionToBankRequest, request);
        return R.ok("删除成功");
    }
}
