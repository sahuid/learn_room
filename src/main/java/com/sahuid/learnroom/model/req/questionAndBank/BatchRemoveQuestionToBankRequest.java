package com.sahuid.learnroom.model.req.questionAndBank;

import lombok.Data;

import java.util.List;

@Data
public class BatchRemoveQuestionToBankRequest {

    private List<Long> questionIds;

    private Long questionBankId;
}
