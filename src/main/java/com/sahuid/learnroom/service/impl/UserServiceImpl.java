package com.sahuid.learnroom.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.constants.RedisConstant;
import com.sahuid.learnroom.exception.*;
import com.sahuid.learnroom.model.dto.user.UserLoginRequest;
import com.sahuid.learnroom.model.dto.user.UserQueryRequest;
import com.sahuid.learnroom.model.dto.user.UserRegisterRequest;
import com.sahuid.learnroom.model.dto.user.UserUpdateRequest;
import com.sahuid.learnroom.model.entity.User;

import com.sahuid.learnroom.mapper.UserMapper;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.UserService;
import com.sahuid.learnroom.utils.ThrowUtil;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
* @author Lenovo
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-12-11 11:35:19
*/
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RedissonClient redissonClient;

    @Override
    public UserVo userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new RequestParamException("请求参数错误");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

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
        return userVo;

    }

    @Override
    public void userRegister(UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new RequestParamException("请求参数错误");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
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
    }

    @Override
    public void userUpdate(UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null) {
            throw new RequestParamException("请求参数错误");
        }
        Long userId = userUpdateRequest.getId();
        if (userId == null || userId <= 0) {
            throw new RequestParamException("请求参数错误");
        }
        User user = this.getById(userId);
        if (user == null) {
            throw new DataBaseAbsentException("数据不存在");
        }

        String userName = userUpdateRequest.getUserName();
        if (StrUtil.isNotBlank(userName)) {
            user.setUserName(userName);
        }
        boolean updateById = this.updateById(user);
        ThrowUtil.throwIf(!updateById, () -> new DataOperationException("修改失败"));
    }

    @Override
    public UserVo getCurrentUser(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        if(user == null) {
            throw new NoLoginException("当前用户未登录");
        }
        return (UserVo) user;
    }

    @Override
    public Page<User> queryUserByPage(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new RequestParamException("请求参数错误");
        }
        int currentPage = userQueryRequest.getPage();
        int pageSize = userQueryRequest.getPageSize();
        Page<User> page = new Page<>(currentPage, pageSize);
        this.page(page);
        return page;
    }

    @Override
    public void userSign(HttpServletRequest request) {
        UserVo currentUser = this.getCurrentUser(request);
        Long userId = currentUser.getId();
        LocalDate date = LocalDate.now();
        int year = date.getYear();

        String key = RedisConstant.getUserSignKey(year, userId);
        RBitSet bitSet = redissonClient.getBitSet(key);

        int dayOfYear = date.getDayOfYear();
        if (!bitSet.get(dayOfYear)) {
            boolean sign = bitSet.set(dayOfYear);
            ThrowUtil.throwIf(sign, () -> new DataOperationException("签到失败"));
        }
    }

    @Override
    public List<Integer> getUserSignData(Integer year, HttpServletRequest request) {
        if (year == null) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
        }
        UserVo currentUser = this.getCurrentUser(request);
        Long userId = currentUser.getId();

        String key = RedisConstant.getUserSignKey(year, userId);

        RBitSet signBitSet = redissonClient.getBitSet(key);
        BitSet bitSet = signBitSet.asBitSet();
        List<Integer> result = new ArrayList<>();
        int signDay = bitSet.nextSetBit(0);
        while(signDay != -1) {
            result.add(signDay);
            signDay = bitSet.nextSetBit(signDay + 1);
        }
        return result;
    }
}




