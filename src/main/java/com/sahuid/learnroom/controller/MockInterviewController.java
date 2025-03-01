package com.sahuid.learnroom.controller;

import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.entity.MockInterview;
import com.sahuid.learnroom.model.req.mockInterview.CreateMockInterviewRequest;
import com.sahuid.learnroom.model.req.mockInterview.HandleMockInterviewChatEventRequest;
import com.sahuid.learnroom.model.req.mockInterview.QueryMockInterviewByPageRequest;
import com.sahuid.learnroom.service.MockInterviewService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: mcj
 * @Description: 模拟面试控制层
 * @DateTime: 2025/3/1 15:41
 **/
@RestController
@RequestMapping("/mock_interview")
public class MockInterviewController {

    @Resource
    private MockInterviewService mockInterviewService;

    @PostMapping("/create")
    public R<MockInterview> createMockInterview(@RequestBody CreateMockInterviewRequest createMockInterviewRequest) {
        MockInterview mockInterview = mockInterviewService.createMockInterview(createMockInterviewRequest);
        return R.ok(mockInterview, "创建成功");
    }

    @GetMapping("/query/page")
    public R<PageResult<MockInterview>> queryMockInterviewByPage(QueryMockInterviewByPageRequest queryMockInterviewByPageRequest) {
        PageResult<MockInterview> pageResult = mockInterviewService.queryMockInterviewByPage(queryMockInterviewByPageRequest);
        return R.ok(pageResult, "查询成功");
    }

    @PostMapping("/handle/event")
    public R<String> handleMockInterviewEvent(@RequestBody HandleMockInterviewChatEventRequest handleMockInterviewChatEventRequest) {
        String ans = mockInterviewService.handleMockInterviewEvent(handleMockInterviewChatEventRequest);
        return R.ok(ans, "回复成功！");
    }


    @GetMapping("/queryOne")
    public R<MockInterview> queryMockInterviewById(Long id) {
        MockInterview mockInterview = mockInterviewService.queryMockInterviewById(id);
        return R.ok(mockInterview, "查询成功");
    }

}
