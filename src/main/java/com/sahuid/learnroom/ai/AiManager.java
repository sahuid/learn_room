package com.sahuid.learnroom.ai;

import com.alibaba.dashscope.aigc.generation.*;
import com.alibaba.dashscope.common.History;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
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

    private Message sendQuestion(String question) {
        return Message.builder()
                .role(Role.USER.getValue())
                .content(question)
                .build();
    }

    private GenerationParam assembleGenerationParam(String question, List<Message> msgManager) {
        Message userMessage = sendQuestion(question);
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
     * 一次性回复问题
     *
     * @param
     * @return
     */
//    public String directAiTalk(String question) {
//        GenerationParam generationParam = assembleGenerationParam(question);
//        GenerationResult result = null;
//        try {
//            result = generation.call(generationParam);
//        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
//            // 使用日志框架记录异常信息
//            log.error("An error occurred while calling the generation service: {}", e.getMessage());
//            throw new RuntimeException("ai 功能出错");
//        }
//        return resolveResult(result);
//    }

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

    public Flowable<GenerationResult> streamAiTalk(String question, List<Message> msgManager) throws NoApiKeyException, InputRequiredException {
        GenerationParam generationParam = assembleGenerationParam(question, msgManager);
        return generation.streamCall(generationParam);

    }
}
