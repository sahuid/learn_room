package com.sahuid.learnroom.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import com.sahuid.learnroom.annotation.RoleCheck;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.constants.UserConstant;
import com.sahuid.learnroom.model.req.questionbank.*;
import com.sahuid.learnroom.model.entity.QuestionBank;
import com.sahuid.learnroom.model.vo.QuestionBankVo;
import com.sahuid.learnroom.service.QuestionBankService;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/questionBank")
@RestController
public class QuestionBankController {

    @Resource
    private QuestionBankService questionBankService;

    @PostMapping("/add")
    public R<Void> addQuestionBank(@RequestBody AddQuestionBankRequest addQuestionBankRequest) {
        questionBankService.addQuestionBank(addQuestionBankRequest);
        return R.ok("添加成功");
    }

    @PostMapping("/update")
    public R<Void> updateQuestionBank(@RequestBody UpdateQuestionBankRequest updateQuestionBankRequest) {
        questionBankService.updateQuestionBank(updateQuestionBankRequest);
        return R.ok("修改成功");
    }


    /**
     * 查询题库 并且可以选择是否显示题目
     * @param queryQuestionBankOneRequest
     * @return
     */
    @GetMapping("/queryOne")
    public R<QuestionBankVo> queryQuestionBankById(QueryQuestionBankOneRequest queryQuestionBankOneRequest) {
        String key = "bank_detail_" + queryQuestionBankOneRequest.getId();
        if (JdHotKeyStore.isHotKey(key)){
            Object cacheObject = JdHotKeyStore.get(key);
            if (cacheObject != null) {
                return R.ok((QuestionBankVo) cacheObject, "查询成功");
            }
        }
        QuestionBankVo questionBank = questionBankService.queryBankById(queryQuestionBankOneRequest);
        JdHotKeyStore.smartSet(key, questionBank);
        return R.ok(questionBank, "查询成功");
    }

    @GetMapping("/queryPage")
    @SentinelResource(value = "queryQuestionBankByPage",
                blockHandler = "handleBlockException",
                fallback = "handleFallback")
    public R<Page<QuestionBank>> queryQuestionBankByPage(QueryQuestionBankByPageRequest queryQuestionBankByPageRequest){
        Page<QuestionBank> page = questionBankService.queryQuestionBankByPage(queryQuestionBankByPageRequest);
        return R.ok(page, "查询成功");
    }


    /**
     * 流控操作
     * 限流：提示 "系统压力过大，请耐心等待！"
     * 熔断：执行降级操作
     * @param queryQuestionBankByPageRequest
     * @param ex
     * @return
     */
    public R<Page<QuestionBank>> handleBlockException(QueryQuestionBankByPageRequest queryQuestionBankByPageRequest,
                                                      BlockException ex){

        // 降级操作
        if (ex instanceof DegradeException) {
            return handleFallback(queryQuestionBankByPageRequest, ex);
        }
        // 限流操作
        return R.fail(500, "系统压力过大，请耐心等待！");
    }

    /**
     * 降级操作：直接返回本地数据
     * @param queryQuestionBankByPageRequest
     * @param ex
     * @return
     */
    public R<Page<QuestionBank>> handleFallback(QueryQuestionBankByPageRequest queryQuestionBankByPageRequest,
                                                      Throwable ex){

        // 可以返回本地数据或空数据
        return R.ok(null, "暂无数据，请稍等！");
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
