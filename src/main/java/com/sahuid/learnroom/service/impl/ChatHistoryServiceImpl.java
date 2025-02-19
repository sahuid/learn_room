package com.sahuid.learnroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.model.entity.ChatHistory;
import com.sahuid.learnroom.model.enums.MessageRoleEnums;
import com.sahuid.learnroom.service.ChatHistoryService;
import com.sahuid.learnroom.mapper.ChatHistoryMapper;
import com.sahuid.learnroom.utils.ThrowUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author mcj
* @description 针对表【message_history】的数据库操作Service实现
* @createDate 2025-02-08 14:36:06
*/
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>
    implements ChatHistoryService {

    @Override
    public void addMessageHistory(Long userId, String message, MessageRoleEnums messageRoleEnums) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setContent(message);
        chatHistory.setRole(messageRoleEnums.getRole());
        chatHistory.setUserId(userId);
        boolean save = this.save(chatHistory);
        ThrowUtil.throwIf(!save, () -> new DataOperationException("聊天记录保存失败"));
    }

    @Override
    public void removeMessageByUserId(Long userId) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getUserId, userId);
        boolean remove = this.remove(wrapper);
        ThrowUtil.throwIf(!remove, () -> new DataOperationException("清空失败"));
    }

    @Override
    public List<ChatHistory> getMessageHistory(Long userId, String cursor, Integer size) {
        LambdaQueryWrapper<ChatHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatHistory::getUserId, userId);
        wrapper.orderByDesc(ChatHistory::getCreateTime);
        if (cursor != null) {
            Date date = new Date(Long.parseLong(cursor));
            wrapper.lt(ChatHistory::getCreateTime, date);
        }
        wrapper.last("limit " + size);
        List<ChatHistory> list = this.list(wrapper);
        return list;
    }
}




