package com.sahuid.learnroom.model.dto.collect;

import com.sahuid.learnroom.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetCollectQuestionRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 用户 id
     */
    private Long userId;
}
