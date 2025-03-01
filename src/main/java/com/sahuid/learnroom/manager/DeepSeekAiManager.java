package com.sahuid.learnroom.manager;

import cn.hutool.core.collection.CollUtil;
import com.sahuid.learnroom.exception.DataOperationException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChoice;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: mcj
 * @Description: deepSeek Ai 调用类
 * @DateTime: 2025/2/28 17:29
 **/

@Service
@Slf4j
public class DeepSeekAiManager implements AiService {

    private static final String DEFAULT_MODEL = "deepseek-v3-241226";

    @Resource
    private ArkService arkService;
    @Override
    public String onceChat(String systemPrompt, String userPrompt) {
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(systemPrompt).build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(userPrompt).build();
        messages.add(systemMessage);
        messages.add(userMessage);

        return chatAi(messages);
    }

    @Override
    public String contextChat(List<ChatMessage> messageList) {
        return chatAi(messageList);
    }

    private String chatAi(List<ChatMessage> messageList) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(DEFAULT_MODEL)
                .messages(messageList)
                .temperature(1.0)
                .build();

        List<ChatCompletionChoice> choices = arkService.createChatCompletion(chatCompletionRequest).getChoices();
        if (CollUtil.isNotEmpty(choices)) {
            String content = (String) choices.get(0).getMessage().getContent();
            log.info("ai 接受到的消息：{}", content);
            return content;
        }
        log.info("deepSeek AI 调用失败");
        throw new DataOperationException("deepSeek AI 调用失败");
    }
}
