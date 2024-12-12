package com.sahuid.learnroom.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.exception.DataBaseAbsentException;
import com.sahuid.learnroom.exception.DataPresentException;
import com.sahuid.learnroom.exception.RequestParamException;
import com.sahuid.learnroom.model.dto.user.UserLoginDto;
import com.sahuid.learnroom.model.dto.user.UserQueryDto;
import com.sahuid.learnroom.model.dto.user.UserRegisterDto;
import com.sahuid.learnroom.model.dto.user.UserUpdateDto;
import com.sahuid.learnroom.model.entity.User;

import com.sahuid.learnroom.mapper.UserMapper;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
* @author Lenovo
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-12-11 11:35:19
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public R<UserVo> userLogin(UserLoginDto userLoginDto, HttpServletRequest request) {
        if (userLoginDto == null) {
            throw new RequestParamException("请求参数错误");
        }
        String userAccount = userLoginDto.getUserAccount();
        String userPassword = userLoginDto.getUserPassword();

        if (StrUtil.isBlank(userAccount) || StrUtil.isBlank(userPassword)) {
            throw new RequestParamException("请求参数错误");
        }
        String md5Password = DigestUtils.md5DigestAsHex(userPassword.getBytes());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserAccount, userAccount);
        wrapper.eq(User::getUserPassword, md5Password);
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new DataBaseAbsentException("数据不存在");
        }
        UserVo userVo = UserVo.userToVo(user);
        request.getSession().setAttribute("user", userVo);
        return R.ok(userVo, "登录成功");

    }

    @Override
    public R<Void> userRegister(UserRegisterDto userRegisterDto) {
        if (userRegisterDto == null) {
            throw new RequestParamException("请求参数错误");
        }
        String userAccount = userRegisterDto.getUserAccount();
        String userPassword = userRegisterDto.getUserPassword();
        if (StrUtil.isBlank(userAccount) || StrUtil.isBlank(userPassword)) {
            throw new RequestParamException("请求参数错误");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserAccount, userAccount);
        User user = this.getOne(wrapper);
        if (user != null) {
            throw new DataPresentException("数据已经存在");
        }

        String md5Password = DigestUtils.md5DigestAsHex(userPassword.getBytes());
        UUID uuid = UUID.randomUUID(false);
        String userName = "用户" + uuid.toString();
        User currentUser = new User();
        currentUser.setUserName(userName);
        currentUser.setUserAccount(userAccount);
        currentUser.setUserPassword(md5Password);
        this.save(currentUser);
        return R.ok();
    }

    @Override
    public R<Void> userUpdate(UserUpdateDto userUpdateDto) {
        if (userUpdateDto == null) {
            throw new RequestParamException("请求参数错误");
        }
        Long userId = userUpdateDto.getId();
        if (userId == null || userId <= 0) {
            throw new RequestParamException("请求参数错误");
        }
        User user = this.getById(userId);
        if (user == null) {
            throw new DataBaseAbsentException("数据不存在");
        }

        String userName = userUpdateDto.getUserName();
        if (StrUtil.isNotBlank(userName)) {
            user.setUserName(userName);
        }
        boolean updateById = this.updateById(user);
        return R.ok("修改成功");
    }

    @Override
    public R<UserVo> getCurrentUser(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        if(user == null) {
            throw new RequestParamException("当前用户未登录");
        }
        UserVo userVo = (UserVo) user;
        return R.ok(userVo);
    }

    @Override
    public R<Page<User>> queryUserByPage(UserQueryDto userQueryDto) {
        if (userQueryDto == null) {
            throw new RequestParamException("请求参数错误");
        }
        int currentPage = userQueryDto.getPage();
        int pageSize = userQueryDto.getPageSize();
        Page<User> page = new Page<>(currentPage, pageSize);
        this.page(page);
        return R.ok(page);
    }
}



