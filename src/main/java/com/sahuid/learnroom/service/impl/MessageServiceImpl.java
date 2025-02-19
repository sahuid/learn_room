package com.sahuid.learnroom.service.impl;

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

}




