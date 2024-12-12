package com.sahuid.learnroom.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.dto.user.UserLoginDto;
import com.sahuid.learnroom.model.dto.user.UserQueryDto;
import com.sahuid.learnroom.model.dto.user.UserRegisterDto;
import com.sahuid.learnroom.model.dto.user.UserUpdateDto;
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

    R<UserVo> userLogin(UserLoginDto userLoginDto, HttpServletRequest request);

    R<Void> userRegister(UserRegisterDto userRegisterDto);

    R<Void> userUpdate(UserUpdateDto userUpdateDto);

    R<UserVo> getCurrentUser(HttpServletRequest request);

    R<Page<User>> queryUserByPage(UserQueryDto userQueryDto);
}
