package com.sahuid.learnroom.model.req.like;

import lombok.Data;

import java.io.Serializable;

@Data
public class LikeRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long targetId;

    private Integer targetType;

    private Long userId;
}
