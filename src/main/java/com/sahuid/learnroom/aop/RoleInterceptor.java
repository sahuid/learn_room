package com.sahuid.learnroom.aop;

import cn.hutool.core.util.StrUtil;
import com.sahuid.learnroom.annotation.RoleCheck;
import com.sahuid.learnroom.common.R;
import com.sahuid.learnroom.exception.NoAuthException;
import com.sahuid.learnroom.model.enums.UserRoleEnums;
import com.sahuid.learnroom.model.vo.UserVo;
import com.sahuid.learnroom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleInterceptor {

    private final UserService userService;

    @Around("@annotation(roleCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, RoleCheck roleCheck) throws Throwable {
        String mustRole = roleCheck.mustRole();

        UserVo currentUser = userService.getCurrentUser();
        Integer userRole = currentUser.getUserRole();
        // 只有权限才能进入
        if (StrUtil.isNotBlank(mustRole)) {
            UserRoleEnums mustRoleEnum = UserRoleEnums.getCurrentUserRoleEnum(mustRole);
            if (mustRoleEnum == null) {
                throw new NoAuthException("没有权限访问");
            }
            String currentRoleName = UserRoleEnums.getCurrentRoleName(userRole);

            // 必须拥有管理员才能进入
            if (UserRoleEnums.ADMIN.equals(mustRoleEnum)) {
                if (!mustRole.equals(currentRoleName)) {
                    // 报错
                    throw new NoAuthException("没有权限访问");
                }
            }
        }
        return joinPoint.proceed();
    }
}
