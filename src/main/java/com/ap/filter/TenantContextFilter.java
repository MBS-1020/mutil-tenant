package com.ap.filter;

import com.ap.context.TenantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 类名：TenantContextWebFilter.java
 * 描述： 多租户 Context Web 过滤器
 *
 * @author AP
 * @version 1.0
 * @date 2023/7/12 16:45
 */

public class TenantContextFilter extends OncePerRequestFilter {

    @Value("${multi-tenant.header-tenant-id}")
    private String headerTenantId;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 设置
        String tenantId = request.getHeader(headerTenantId);
        if (StringUtils.hasText(tenantId)) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            // 清理
            TenantContext.clear();
        }
    }

}

