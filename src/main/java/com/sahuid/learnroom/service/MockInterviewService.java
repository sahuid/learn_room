package com.sahuid.learnroom.service;

import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.model.entity.MockInterview;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sahuid.learnroom.model.req.mockInterview.CreateMockInterviewRequest;
import com.sahuid.learnroom.model.req.mockInterview.HandleMockInterviewChatEventRequest;
import com.sahuid.learnroom.model.req.mockInterview.QueryMockInterviewByPageRequest;

/**
* @author mcj
* @description 针对表【mock_interview】的数据库操作Service
* @createDate 2025-03-01 15:38:18
*/
public interface MockInterviewService extends IService<MockInterview> {

    /**
     * 创建模拟面试
     * @param createMockInterviewRequest
     */
    MockInterview createMockInterview(CreateMockInterviewRequest createMockInterviewRequest);

    /**
     * 分页查询模拟面试列表
     * @param queryMockInterviewByPageRequest
     * @return
     */
    PageResult<MockInterview> queryMockInterviewByPage(QueryMockInterviewByPageRequest queryMockInterviewByPageRequest);

    /**
     * 处理模拟面试事件
     * @param handleMockInterviewChatEventRequest
     * @return
     */
    String handleMockInterviewEvent(HandleMockInterviewChatEventRequest handleMockInterviewChatEventRequest);

    /**
     * 根据 id 查询模拟面试信息
     * @param id
     * @return
     */
    MockInterview queryMockInterviewById(Long id);
}
