package com.sahuid.learnroom.model.enums;

import lombok.Getter;

@Getter
public enum LikeTargetTypeEnums {
    QUESTION_TYPE(0, "题目"),
    COMMENT_TYPE(1, "评论")
    ;


    private Integer type;

    private String des;

    LikeTargetTypeEnums(Integer type, String des) {
        this.type = type;
        this.des = des;
    }

    public static LikeTargetTypeEnums getLikeTargetType(Integer type) {
        for (LikeTargetTypeEnums value : values()) {
            if (value.getType() == type) {
                return value;
            }
        }
        return null;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
