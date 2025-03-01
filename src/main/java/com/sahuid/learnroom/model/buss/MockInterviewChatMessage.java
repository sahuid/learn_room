package com.sahuid.learnroom.model.buss;

import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mcj
 * @Description: 模拟面试数据库聊天记录
 * @DateTime: 2025/3/1 17:23
 **/
@Data
public class MockInterviewChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String role;

    private String message;


    public static MockInterviewChatMessage aiMessage2DB(ChatMessage chatMessage) {
        return assembleMessage(chatMessage.getContent().toString(), chatMessage.getRole().value());
    }

    public static ChatMessage dbMessage2Ai(MockInterviewChatMessage message) {
        return ChatMessage.builder().
                role(ChatMessageRole.valueOf(message.getRole().toUpperCase())).
                content(message.getMessage()).build();
    }

    public static MockInterviewChatMessage assembleMessage(String content, String role) {
        MockInterviewChatMessage mockInterviewChatMessage = new MockInterviewChatMessage();
        mockInterviewChatMessage.setMessage(content);
        mockInterviewChatMessage.setRole(role);
        return mockInterviewChatMessage;
    }
}
