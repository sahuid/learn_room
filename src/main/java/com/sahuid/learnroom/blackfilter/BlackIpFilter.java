package com.sahuid.learnroom.blackfilter;

import cn.hutool.core.net.NetUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author: mcj
 * @Description: 黑名单过滤器
 * @DateTime: 2025/2/27 17:43
 **/
@WebFilter(urlPatterns = "/*", filterName = "blackIpFilter")
public class BlackIpFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String clientIP = getClientIP((HttpServletRequest) servletRequest);
        if (BlackIpUtils.isBlackIp(clientIP)) {
            servletResponse.setContentType("text/json;charset=UTF-8");
            servletResponse.getWriter().write("{\"errorCode\":\"-1\",\"errorMsg\":\"黑名单IP，禁止访问\"}");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getClientIP(HttpServletRequest request) {
        // 优先级：X-Forwarded-For -> Proxy-Client-IP -> WL-Proxy-Client-IP -> RemoteAddr
        String ip = request.getHeader("X-Forwarded-For");
        if (cn.hutool.core.util.StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (cn.hutool.core.util.StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (cn.hutool.core.util.StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多级代理的 X-Forwarded-For 场景（取第一个非 unknown 的 IP）
        if (cn.hutool.core.util.StrUtil.isNotBlank(ip)) {
            String[] ips = ip.split(",");
            for (String tmp : ips) {
                if (cn.hutool.core.util.StrUtil.isNotBlank(tmp) && !"unknown".equalsIgnoreCase(tmp)) {
                    return tmp.trim();
                }
            }
        }
        return ip;
    }
}
