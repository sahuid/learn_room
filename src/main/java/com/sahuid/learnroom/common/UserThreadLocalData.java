package com.sahuid.learnroom.common;

/**
 * @Author: mcj
 * @Description: ThreadLocal 保存数据
 * @DateTime: 2025/2/10 17:44
 **/
public class UserThreadLocalData {

    private static final ThreadLocal<Long> USER_ID_THREADLOCAL = new ThreadLocal<>();

    public static void setUserData(Long userId) {
        USER_ID_THREADLOCAL.set(userId);
    }

    public static Long getUserData() {
        return USER_ID_THREADLOCAL.get();
    }

    public static void removeUserData() {
        USER_ID_THREADLOCAL.remove();
    }
}
