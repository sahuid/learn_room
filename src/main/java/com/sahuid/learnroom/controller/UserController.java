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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/user")
@RestController
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public R<UserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        UserVo userVo = userService.userLogin(userLoginRequest, request);
        return R.ok(userVo, "登录成功");
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
    public R<UserVo> getCurrentUser(HttpServletRequest request){
        UserVo currentUser = userService.getCurrentUser(request);
        return R.ok(currentUser);
    }


    @GetMapping("/queryPage")
    public R<Page<User>> queryUserByPage(UserQueryRequest userQueryRequest){
        Page<User> userPage = userService.queryUserByPage(userQueryRequest);
        return R.ok(userPage, "获取成功");
    }

    @GetMapping("/sign")
    public R<Void> userSign(HttpServletRequest request){
        userService.userSign(request);
        return R.ok("签到成功");
    }

    @GetMapping("/getSignData")
    public R<List<Integer>> getUserSignData(Integer year, HttpServletRequest request) {
        List<Integer> list = userService.getUserSignData(year, request);
        return R.ok(list);
    }
}
