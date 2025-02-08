package com.sahuid.learnroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.exception.DataOperationException;
import com.sahuid.learnroom.model.entity.MessageHistory;
import com.sahuid.learnroom.model.enums.MessageRoleEnums;
import com.sahuid.learnroom.service.MessageHistoryService;
import com.sahuid.learnroom.mapper.MessageHistoryMapper;
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
public class MessageHistoryServiceImpl extends ServiceImpl<MessageHistoryMapper, MessageHistory>
    implements MessageHistoryService{

    @Override
    public void addMessageHistory(Long userId, String message, MessageRoleEnums messageRoleEnums) {
        MessageHistory messageHistory = new MessageHistory();
        messageHistory.setContent(message);
        messageHistory.setRole(messageRoleEnums.getRole());
        messageHistory.setUserId(userId);
        boolean save = this.save(messageHistory);
        ThrowUtil.throwIf(!save, () -> new DataOperationException("聊天记录保存失败"));
    }

    @Override
    public void removeMessageByUserId(Long userId) {
        LambdaQueryWrapper<MessageHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageHistory::getUserId, userId);
        boolean remove = this.remove(wrapper);
        ThrowUtil.throwIf(!remove, () -> new DataOperationException("清空失败"));
    }

    @Override
    public List<MessageHistory> getMessageHistory(Long userId, String cursor, Integer size) {
        LambdaQueryWrapper<MessageHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageHistory::getUserId, userId);
        wrapper.orderByDesc(MessageHistory::getCreateTime);
        if (cursor != null) {
            Date date = new Date(Long.parseLong(cursor));
            wrapper.lt(MessageHistory::getCreateTime, date);
        }
        wrapper.last("limit " + size);
        List<MessageHistory> list = this.list(wrapper);
        return list;
    }
}




