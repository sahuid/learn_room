package com.sahuid.learnroom.service;

import com.sahuid.learnroom.model.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author mcj
* @description 针对表【message】的数据库操作Service
* @createDate 2025-02-19 18:53:10
*/
public interface MessageService extends IService<Message> {


    Message queryMessageByIdAndStatus(Long messageId, String status);

    void updateMessageSuccess(Long messageId);
}
