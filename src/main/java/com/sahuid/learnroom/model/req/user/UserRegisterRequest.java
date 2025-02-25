package com.sahuid.learnroom.model.req.user;

import lombok.Data;

@Data
public class UserRegisterRequest {

    private String userAccount;

    private String userPassword;
}
