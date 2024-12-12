package com.sahuid.learnroom.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.dto.user.UserLoginRequest;
import com.sahuid.learnroom.model.dto.user.UserQueryRequest;
import com.sahuid.learnroom.model.dto.user.UserRegisterRequest;
import com.sahuid.learnroom.model.dto.user.UserUpdateRequest;
import com.sahuid.learnroom.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sahuid.learnroom.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author Lenovo
* @description 针对表【user】的数据库操作Service
* @createDate 2024-12-11 11:35:19
*/
public interface UserService extends IService<User> {

    R<UserVo> userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    R<Void> userRegister(UserRegisterRequest userRegisterRequest);

    R<Void> userUpdate(UserUpdateRequest userUpdateRequest);

    R<UserVo> getCurrentUser(HttpServletRequest request);

    R<Page<User>> queryUserByPage(UserQueryRequest userQueryRequest);
}
