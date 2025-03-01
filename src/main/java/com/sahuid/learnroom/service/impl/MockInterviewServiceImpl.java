package com.sahuid.learnroom.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.common.PageResult;
import com.sahuid.learnroom.common.UserThreadLocalData;
import com.sahuid.learnroom.constants.AiConstant;
import com.sahuid.learnroom.exception.DataBaseAbsentException;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.exception.NoAuthException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.manager.AiManager;
import com.sahuid.learnroom.manager.AiService;
import com.sahuid.learnroom.model.buss.MockInterviewChatMessage;
import com.sahuid.learnroom.model.entity.MockInterview;
import com.sahuid.learnroom.model.enums.MockInterviewEventEnums;
import com.sahuid.learnroom.model.enums.MockInterviewStatusEnums;
import com.sahuid.learnroom.model.req.mockInterview.CreateMockInterviewRequest;
import com.sahuid.learnroom.model.req.mockInterview.HandleMockInterviewChatEventRequest;
import com.sahuid.learnroom.model.req.mockInterview.QueryMockInterviewByPageRequest;
import com.sahuid.learnroom.service.MockInterviewService;
import com.sahuid.learnroom.mapper.MockInterviewMapper;
import com.sahuid.learnroom.utils.ThrowUtil;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mcj
 * @description 针对表【mock_interview】的数据库操作Service实现
 * @createDate 2025-03-01 15:38:18
 */
@Service
public class MockInterviewServiceImpl extends ServiceImpl<MockInterviewMapper, MockInterview>
        implements MockInterviewService {

    @Resource
    private AiService aiService;

    @Override
    public MockInterview createMockInterview(CreateMockInterviewRequest createMockInterviewRequest) {
        ThrowUtil.throwIf(createMockInterviewRequest == null,
                () -> new RequestParamException("请求参数错误"));
        String difficulty = createMockInterviewRequest.getDifficulty();
        String workExperience = createMockInterviewRequest.getWorkExperience();
        String jobPosition = createMockInterviewRequest.getJobPosition();
        ThrowUtil.throwIf(StringUtils.isAnyBlank(difficulty, workExperience, jobPosition),
                () -> new RequestParamException("请求参数缺失"));

        MockInterview mockInterview = MockInterview.createInstance(difficulty, workExperience, jobPosition);
        boolean save = this.save(mockInterview);
        ThrowUtil.throwIf(!save,
                () -> new DataOperationException("创建模拟面试失败"));
        return mockInterview;
    }

    @Override
    public PageResult<MockInterview> queryMockInterviewByPage(QueryMockInterviewByPageRequest queryMockInterviewByPageRequest) {
        ThrowUtil.throwIf(queryMockInterviewByPageRequest == null,
                () -> new RequestParamException("请求参数错误"));
        Long userId = UserThreadLocalData.getUserData();

        int currPage = queryMockInterviewByPageRequest.getPage();
        int pageSize = queryMockInterviewByPageRequest.getPageSize();
        Page<MockInterview> page = new Page<>(currPage, pageSize);
        LambdaQueryWrapper<MockInterview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MockInterview::getUserId, userId);
        this.page(page, wrapper);
        return PageResult.pageToResult(page);
    }

    @Override
    public String handleMockInterviewEvent(HandleMockInterviewChatEventRequest handleMockInterviewChatEventRequest) {
        ThrowUtil.throwIf(handleMockInterviewChatEventRequest == null,
                () -> new RequestParamException("请求参数错误"));
        // 判断事件
        String event = handleMockInterviewChatEventRequest.getEvent();
        MockInterviewEventEnums eventEnum = MockInterviewEventEnums.getEventEnum(event);
        ThrowUtil.throwIf(eventEnum == null,
                () -> new RequestParamException("非法事件，无法进行处理"));
        // 查询模拟面试是否存在
        Long mockInterviewId = handleMockInterviewChatEventRequest.getId();
        MockInterview mockInterview = this.getById(mockInterviewId);
        ThrowUtil.throwIf(mockInterview == null,
                () -> new DataBaseAbsentException("模拟面试不存在"));
        // 判断是否是本人参与
        Long loginId = UserThreadLocalData.getUserData();
        Long userId = mockInterview.getUserId();
        ThrowUtil.throwIf(!loginId.equals(userId),
                () -> new NoAuthException("不是本人创建的模拟面试无法参与"));

        String userAnswer = handleMockInterviewChatEventRequest.getContent();
        // 通过类型进行相应处理
        switch (eventEnum) {
            case START:
                return handleStartEvent(mockInterview);
            case CHAT:
                return handleChatEvent(mockInterview, userAnswer);
            case END:
                return handleEndEvent(mockInterview);
            default:
                throw new RequestParamException("非法事件，无法进行处理");
        }
    }

    @Override
    public MockInterview queryMockInterviewById(Long id) {
        ThrowUtil.throwIf(id == null || id <= 0,
                () -> new RequestParamException("请求参数错误"));
        MockInterview mockInterview = this.getById(id);
        ThrowUtil.throwIf(mockInterview == null,
                () -> new DataBaseAbsentException("这场模拟面试不存在"));
        return mockInterview;
    }

    private String handleEndEvent(MockInterview mockInterview) {
        // 获取历史记录
        String messages = mockInterview.getMessages();
        List<MockInterviewChatMessage> historyList = JSONUtil.toList(messages, MockInterviewChatMessage.class);
        // 组装结束信息
        MockInterviewChatMessage mockInterviewChatMessage = MockInterviewChatMessage.assembleMessage("结束面试", ChatMessageRole.USER.value());
        historyList.add(mockInterviewChatMessage);
        // 转换对象
        List<ChatMessage> aiMessage = historyList.stream()
                .map(MockInterviewChatMessage::dbMessage2Ai)
                .collect(Collectors.toList());
        // 询问 ai
        String aiAnswer = aiService.contextChat(aiMessage);
        // 添加到历史记录中
        historyList.add(MockInterviewChatMessage.assembleMessage(aiAnswer, ChatMessageRole.ASSISTANT.value()));
        // 修改记录
        mockInterview.setMessages(JSONUtil.toJsonStr(historyList));
        mockInterview.setStatus(MockInterviewStatusEnums.ENDING.getStatus());
        this.updateById(mockInterview);
        return aiAnswer;
    }

    /**
     * 处理ai面试回答
     *
     * @param mockInterview
     * @param userAnswer
     * @return
     */
    private String handleChatEvent(MockInterview mockInterview, String userAnswer) {
        // 组装用户消息
        MockInterviewChatMessage userAnswerMessage = MockInterviewChatMessage.assembleMessage(userAnswer, ChatMessageRole.USER.value());
        // 获取历史记录
        String historyMessage = mockInterview.getMessages();
        List<MockInterviewChatMessage> historyList = JSONUtil.toList(historyMessage, MockInterviewChatMessage.class);
        // 添加用户回答到历史记录中
        historyList.add(userAnswerMessage);
        // 转换类型
        List<ChatMessage> aiMessage = historyList.stream()
                .map(MockInterviewChatMessage::dbMessage2Ai)
                .collect(Collectors.toList());
        // 回答ai
        String aiAnswer = aiService.contextChat(aiMessage);
        // 加入到历史记录中
        historyList.add(MockInterviewChatMessage.assembleMessage(aiAnswer, ChatMessageRole.ASSISTANT.value()));
        mockInterview.setMessages(JSONUtil.toJsonStr(historyList));
        // 判断是否提前结束面试
        if (aiAnswer.contains("面试结束")) {
            // 提前结束
            mockInterview.setStatus(MockInterviewStatusEnums.ENDING.getStatus());
        }
        this.updateById(mockInterview);
        return aiAnswer;
    }

    /**
     * 处理面试开始
     *
     * @param mockInterview
     */
    private String handleStartEvent(MockInterview mockInterview) {
        // 系统 prompt
        String systemPrompt = String.format(AiConstant.DEEPSEEK_MOCK_INTERVIEW_SYSTEM_PROMPT
                , mockInterview.getWorkExperience(), mockInterview.getJobPosition(), mockInterview.getDifficulty());
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(systemPrompt).build();
        ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(AiConstant.MOCK_INTERVIEW_START_CONTENT).build();
        messages.add(systemMessage);
        messages.add(userMessage);
        // 询问 ai
        String answer = aiService.contextChat(messages);
        // 转化为聊天记录保存到数据库中
        List<MockInterviewChatMessage> dbInterviewHistory = messages.stream()
                .map(MockInterviewChatMessage::aiMessage2DB)
                .collect(Collectors.toList());
        MockInterviewChatMessage answerMessage = MockInterviewChatMessage.assembleMessage(answer, ChatMessageRole.ASSISTANT.value());
        dbInterviewHistory.add(answerMessage);
        // 保存到数据库，并修改状态
        mockInterview.setMessages(JSONUtil.toJsonStr(dbInterviewHistory));
        mockInterview.setStatus(MockInterviewStatusEnums.BEING.getStatus());
        this.updateById(mockInterview);
        return answer;
    }
}




