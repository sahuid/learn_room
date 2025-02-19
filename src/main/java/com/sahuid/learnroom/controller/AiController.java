package com.sahuid.learnroom.controller;

import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.entity.ChatHistory;
import com.sahuid.learnroom.service.ChatHistoryService;
import com.sahuid.learnroom.ws.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: mcj
 * @Description: ai 接口
 * @DateTime: 2025/2/8 15:27
 **/

@RestController
@RequestMapping("/chat")
public class AiController {

    @Resource
    private ChatHistoryService chatHistoryService;

    @GetMapping("/clearHistory")
    public R<Void> clearChatHistory(@RequestParam Long userId) {
        ChatService.clearChatMessage(userId);
        chatHistoryService.removeMessageByUserId(userId);
        return R.ok("清除成功");
    }

    @GetMapping("/getHistory")
    public R<List<ChatHistory>> getMessageHistory(
            @RequestParam Long userId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ChatHistory> result =
                chatHistoryService.getMessageHistory(userId, cursor, size);
        return R.ok(result);
    }
}
