package com.sahuid.learnroom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.model.entity.LikesCount;
import com.sahuid.learnroom.service.LikesCountService;
import com.sahuid.learnroom.mapper.LikesCountMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【likes_count】的数据库操作Service实现
* @createDate 2024-12-27 12:12:50
*/
@Service
public class LikesCountServiceImpl extends ServiceImpl<LikesCountMapper, LikesCount>
    implements LikesCountService{

}




