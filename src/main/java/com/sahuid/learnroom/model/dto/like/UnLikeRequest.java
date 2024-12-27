package com.sahuid.learnroom.model.dto.like;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnLikeRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long targetId;

    private Integer targetType;

    private Long userId;
}
