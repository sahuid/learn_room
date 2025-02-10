package com.sahuid.learnroom.service;

import cn.dev33.satoken.stp.SaTokenInfo;
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
import java.util.List;

/**
* @author Lenovo
* @description 针对表【user】的数据库操作Service
* @createDate 2024-12-11 11:35:19
*/
public interface UserService extends IService<User> {

    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    SaTokenInfo userLogin(UserLoginRequest userLoginRequest);

    void userRegister(UserRegisterRequest userRegisterRequest);

    void userUpdate(UserUpdateRequest userUpdateRequest);

    UserVo getCurrentUser();

    Page<User> queryUserByPage(UserQueryRequest userQueryRequest);

    void userSign();

    List<Integer> getUserSignData(Integer year);
}
