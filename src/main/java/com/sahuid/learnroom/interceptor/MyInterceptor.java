package com.sahuid.learnroom.interceptor;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.sahuid.learnroom.common.UserThreadLocalData;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: mcj
 * @Description: 项目拦截器
 * @DateTime: 2025/2/10 17:51
 **/
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isLogin = StpUtil.isLogin();
        if (isLogin) {
            String loginId = (String) StpUtil.getLoginId();
            Long userId = Long.valueOf(loginId);
            UserThreadLocalData.setUserData(userId);
            SaSession session = StpUtil.getSession();
            session.setId(loginId);
            return true;
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
       UserThreadLocalData.removeUserData();
    }
}
