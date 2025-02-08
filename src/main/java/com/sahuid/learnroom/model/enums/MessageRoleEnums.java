package com.sahuid.learnroom.model.enums;

/**
 * @Author: mcj
 * @Description: TODO
 * @DateTime: 2025/2/8 15:05
 **/
public enum MessageRoleEnums {
    USER(1),
    AI(0)
    ;

    private int role;

    MessageRoleEnums(int role) {
        this.role = role;
    }

    public int getRole() {
        return role;
    }
}
