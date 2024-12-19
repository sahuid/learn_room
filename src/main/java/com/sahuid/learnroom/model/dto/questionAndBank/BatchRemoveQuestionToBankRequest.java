package com.sahuid.learnroom.model.dto.questionAndBank;

import lombok.Data;

import java.util.List;

@Data
public class BatchRemoveQuestionToBankRequest {

    private List<Long> questionIds;

    private Long questionBankId;
}
