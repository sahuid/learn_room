package com.sahuid.learnroom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.annotation.RoleCheck;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.constants.UserConstant;
import com.sahuid.learnroom.model.dto.questionbank.*;
import com.sahuid.learnroom.model.entity.QuestionBank;
import com.sahuid.learnroom.model.vo.QuestionBankVo;
import com.sahuid.learnroom.service.QuestionBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/questionBank")
@RestController
public class QuestionBankController {

    @Resource
    private QuestionBankService questionBankService;

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


    /**
     * 查询题库 并且可以选择是否显示题目
     * @param queryQuestionBankOneRequest
     * @return
     */
    @GetMapping("/queryOne")
    public R<QuestionBankVo> queryQuestionBankById(QueryQuestionBankOneRequest queryQuestionBankOneRequest) {
        QuestionBankVo questionBank = questionBankService.queryBankById(queryQuestionBankOneRequest);
        return R.ok(questionBank, "查询成功");
    }

    @GetMapping("/queryPage")
    public R<Page<QuestionBank>> queryQuestionBankByPage(QueryQuestionBankByPageRequest queryQuestionBankByPageRequest){
        Page<QuestionBank> page = questionBankService.queryQuestionBankByPage(queryQuestionBankByPageRequest);
        return R.ok(page, "查询成功");
    }

    @GetMapping("/delete")
    public R<Void> deleteQuestionBank(@RequestParam("ids")List<Long>ids) {
        questionBankService.deleteQuestionBanks(ids);
        return R.ok("删除成功");
    }



    /**
     * 不分页查询所有的图库信息
     * @return
     */
    @GetMapping("/queryBankList")
    @RoleCheck(mustRole = UserConstant.ADMIN_ROLE)
    public R<List<QuestionBank>> queryBankList() {
        List<QuestionBank> list = questionBankService.queryBankList();
        return R.ok(list, "查询成功");
    }
}
