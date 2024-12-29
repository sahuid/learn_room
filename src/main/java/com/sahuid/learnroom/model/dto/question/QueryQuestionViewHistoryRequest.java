package com.sahuid.learnroom.model.dto.question;

import com.sahuid.learnroom.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class QueryQuestionViewHistoryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
}
