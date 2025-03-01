package com.sahuid.learnroom.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 模拟面试事件枚举类
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MockInterviewEventEnums {

    START("start"),
    CHAT("chat"),
    END("end")
    ;

    private String event;

    /**
     * 通过事件类型获取枚举类
     * @param event
     * @return
     */
    public static MockInterviewEventEnums getEventEnum(String event) {
        for (MockInterviewEventEnums value : values()) {
            String enumEvent = value.getEvent();
            if (enumEvent.equals(event)) {
                return value;
            }
        }
        return null;
    }


}
