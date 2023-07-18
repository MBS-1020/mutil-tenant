package com.ap.interceptor;

import com.ap.context.TenantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 类名：TenantContextInterceptor.java
 * 描述：多租户 mvc 拦截器
 *
 * @author AP
 * @version 1.0
 * @date 2023/7/12 17:32
 */
public class TenantContextInterceptor implements HandlerInterceptor {

    @Value("${multi-tenant.header-tenant-id}")
    private String headerTenantId;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantId = request.getHeader(headerTenantId);
        if (StringUtils.hasText(tenantId)) {
            TenantContext.setTenantId(tenantId);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex){
        TenantContext.clear();
    }

}
