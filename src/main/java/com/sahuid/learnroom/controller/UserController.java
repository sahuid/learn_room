package com.sahuid.learnroom.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.model.req.user.UserLoginRequest;
import com.sahuid.learnroom.model.req.user.UserQueryRequest;
import com.sahuid.learnroom.model.req.user.UserRegisterRequest;
import com.sahuid.learnroom.model.req.user.UserUpdateRequest;
import com.sahuid.learnroom.model.entity.User;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public R<SaTokenInfo> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        SaTokenInfo tokenInfo = userService.userLogin(userLoginRequest);
        return R.ok(tokenInfo, "登录成功");
    }

    @PostMapping("/register")
    public R<Void> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        userService.userRegister(userRegisterRequest);
        return R.ok("注册成功");
    }

    @PostMapping("/update")
    public R<Void> userUpdate(@RequestBody UserUpdateRequest userUpdateRequest) {
        userService.userUpdate(userUpdateRequest);
        return R.ok("修改成功");
    }

    @GetMapping("/me")
    public R<UserVo> getCurrentUser(){
        UserVo currentUser = userService.getCurrentUser();
        return R.ok(currentUser);
    }


    @GetMapping("/queryPage")
    public R<Page<User>> queryUserByPage(UserQueryRequest userQueryRequest){
        Page<User> userPage = userService.queryUserByPage(userQueryRequest);
        return R.ok(userPage, "获取成功");
    }

    @GetMapping("/sign")
    public R<Void> userSign(){
        userService.userSign();
        return R.ok("签到成功");
    }

    @GetMapping("/getSignData")
    public R<List<Integer>> getUserSignData(Integer year) {
        List<Integer> list = userService.getUserSignData(year);
        return R.ok(list);
    }
}
