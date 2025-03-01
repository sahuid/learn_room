package com.sahuid.learnroom.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 模拟面试状态枚举类
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MockInterviewStatusEnums {

    WAITING_BEING(0, "待开始"),
    BEING(1, "已开始"),
    ENDING(2, "已结束")
    ;

    private int status;

    private String type;


}
