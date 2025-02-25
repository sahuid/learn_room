package com.sahuid.learnroom.model.req.question;

import com.sahuid.learnroom.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QueryQuestionByPageRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 搜索关键词
     */
    private String searchText;

    /**
     * 题库 id
     */

    private Long questionBankId;
}
