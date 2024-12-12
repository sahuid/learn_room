package com.sahuid.learnroom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.dto.user.UserLoginRequest;
import com.sahuid.learnroom.model.dto.user.UserQueryRequest;
import com.sahuid.learnroom.model.dto.user.UserRegisterRequest;
import com.sahuid.learnroom.model.dto.user.UserUpdateRequest;
import com.sahuid.learnroom.model.entity.User;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public R<UserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        return userService.userLogin(userLoginRequest, request);
    }

    @PostMapping("/register")
    public R<Void> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.userRegister(userRegisterRequest);
    }

    @PostMapping("/update")
    public R<Void> userUpdate(@RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.userUpdate(userUpdateRequest);
    }

    @GetMapping("/me")
    public R<UserVo> getCurrentUser(HttpServletRequest request){
        return userService.getCurrentUser(request);
    }


    @GetMapping("/queryPage")
    public R<Page<User>> queryUserByPage(UserQueryRequest userQueryRequest){
        return userService.queryUserByPage(userQueryRequest);
    }
}
