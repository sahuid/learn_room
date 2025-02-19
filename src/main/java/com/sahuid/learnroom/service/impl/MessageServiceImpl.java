package com.sahuid.learnroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.model.entity.Message;
import com.sahuid.learnroom.service.MessageService;
import com.sahuid.learnroom.mapper.MessageMapper;
import org.springframework.stereotype.Service;

/**
* @author mcj
* @description 针对表【message】的数据库操作Service实现
* @createDate 2025-02-19 18:53:10
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

    @Override
    public Message queryMessageByIdAndStatus(Long messageId, String status) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getMessageId, messageId);
        wrapper.eq(Message::getStatus, status);
        return this.getOne(wrapper);
    }

    @Override
    public void updateMessageSuccess(Long messageId) {
        LambdaUpdateWrapper<Message> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Message::getMessageId, messageId);
        updateWrapper.set(Message::getStatus, "success");
        this.update(updateWrapper);
    }
}




