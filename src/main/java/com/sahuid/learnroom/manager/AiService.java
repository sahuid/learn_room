package com.sahuid.learnroom.manager;

import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;

import java.util.List;

/**
 * @Author: mcj
 * @Description: ai对话接口
 * @DateTime: 2025/2/28 17:20
 **/
public interface AiService {

    /**
     * 不含上下文的单次对话
     * @param systemPrompt
     * @param userPrompt
     * @return
     */
    String onceChat(String systemPrompt, String userPrompt);

    String contextChat(List<ChatMessage> messageList);
}
