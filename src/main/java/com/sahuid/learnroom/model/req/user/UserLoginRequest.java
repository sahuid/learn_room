package com.sahuid.learnroom.model.req.user;

import lombok.Data;

@Data
public class UserLoginRequest {
    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;
}
