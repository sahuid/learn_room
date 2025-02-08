package com.sahuid.learnroom.service;

import com.sahuid.learnroom.model.entity.MessageHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sahuid.learnroom.model.enums.MessageRoleEnums;

import java.util.Date;
import java.util.List;

/**
* @author mcj
* @description 针对表【message_history】的数据库操作Service
* @createDate 2025-02-08 14:36:06
*/
public interface MessageHistoryService extends IService<MessageHistory> {


    void addMessageHistory(Long userId, String message, MessageRoleEnums messageRoleEnums);

    void removeMessageByUserId(Long userId);

    List<MessageHistory> getMessageHistory(Long userId, String cursor, Integer size);
}
