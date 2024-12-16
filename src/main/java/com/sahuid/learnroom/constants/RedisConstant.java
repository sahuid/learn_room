package com.sahuid.learnroom.constants;

public class RedisConstant {

    public static final String USER_SIGN = "user:sign";


    public static String getUserSignKey(int year, Long userId) {
        return String.format("%s:%s:%s", USER_SIGN, year, userId);
    }
}
