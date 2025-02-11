package com.sahuid.learnroom.model.dto.user;

import lombok.Data;

@Data
public class UserUpdateRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名称
     */
    private String userName;


    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 用户头像
     */
    private String picture;

}
