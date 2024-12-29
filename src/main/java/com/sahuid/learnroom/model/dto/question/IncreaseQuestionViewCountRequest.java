package com.sahuid.learnroom.model.dto.question;

import lombok.Data;

import java.io.Serializable;

@Data
public class IncreaseQuestionViewCountRequest implements Serializable {


    private static final long serialVersionUID = 1;

    private Long questionId;

    private Long userId;
}
