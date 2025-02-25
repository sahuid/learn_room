package com.sahuid.learnroom.model.req.comment;

import com.sahuid.learnroom.common.PageRequest;
import lombok.Data;

/**
 * @Author: mcj
 * @Description: 查询评论请求
 * @DateTime: 2025/1/12 1:39
 **/
@Data
public class QueryCommentByPageRequest extends PageRequest {

    /**
     * 目标id
     */
    private Long targetId;
}
