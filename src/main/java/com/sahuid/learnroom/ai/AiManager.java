package com.sahuid.learnroom.ai;

import com.alibaba.dashscope.aigc.generation.*;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: mcj
 * @Description: Ai 对话
 * @DateTime: 2025/2/5 14:42
 **/
@Component
@Slf4j
public class AiManager {

    @Value("${ai.api.key}")
    private String appKey;

    @Resource
    private Message systemMessage;

    @Resource
    private Generation generation;

    /**
     * 组装问题
     * @param question
     * @return
     */
    private Message assembleQuestion(String question) {
        return Message.builder()
                .role(Role.USER.getValue())
                .content(question)
                .build();
    }

    /**
     * 组装参数
     * @param question
     * @param msgManager
     * @return
     */
    private GenerationParam assembleGenerationParam(String question, List<Message> msgManager) {
        Message userMessage = assembleQuestion(question);
        // 保存历史记录
        if(msgManager.isEmpty()) {
            msgManager.add(systemMessage);
        }
        msgManager.add(userMessage);
        return GenerationParam.builder()
                .apiKey(appKey)
                .model(Generation.Models.QWEN_PLUS)
                .messages(msgManager)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .temperature(1F)
                .incrementalOutput(true)
                .topP(0.8)
                .build();
    }

    /**
     * 解析结果
     * @param result
     * @return
     */
    public String resolveResult(GenerationResult result) {
        // 其他数据
        String requestId = result.getRequestId();
        GenerationUsage usage = result.getUsage();
        Integer inputTokens = usage.getInputTokens();
        Integer outputTokens = usage.getOutputTokens();
        Integer totalTokens = usage.getTotalTokens();
        // 回复内容
        GenerationOutput output = result.getOutput();
        return output.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 流式回复
     * @param question
     * @param msgManager
     * @return
     * @throws NoApiKeyException
     * @throws InputRequiredException
     */
    public Flowable<GenerationResult> streamAiTalk(String question, List<Message> msgManager) throws NoApiKeyException, InputRequiredException {
        GenerationParam generationParam = assembleGenerationParam(question, msgManager);
        return generation.streamCall(generationParam);

    }
}
