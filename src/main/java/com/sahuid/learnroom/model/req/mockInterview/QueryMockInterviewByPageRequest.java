package com.sahuid.learnroom.model.req.mockInterview;

import com.sahuid.learnroom.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mcj
 * @Description: 分页查询模拟面试请求类
 * @DateTime: 2025/3/1 15:45
 **/
@Data
public class QueryMockInterviewByPageRequest extends PageRequest implements Serializable {


    private static final long serialVersionUID = 1L;
}
