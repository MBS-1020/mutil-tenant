package com.ap.config;

/**
 * 类名：MultiTenantAutoConfiguration.java
 * 描述： 多租户自动配置处理类
 *
 * @author AP
 * @version 1.0
 * @date 2023/7/12 10:12
 */


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.ap.context.TenantContext;
import com.ap.properties.MultiTenantProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Configuration
@EnableConfigurationProperties(MultiTenantProperties.class)
@ConditionalOnProperty(prefix = MultiTenantProperties.PREFIX, value = "enabled", havingValue = "true")
public class MultiTenantAutoConfiguration {

    @Resource
    MybatisPlusInterceptor mybatisPlusInterceptor;

    @Resource(type=MultiTenantProperties.class)
    MultiTenantProperties properties;

    @PostConstruct
    public void tenantLineInnerInterceptor(){
        // 多租户~字段拦截器
        if (properties.getIsolationRow()) {
            mybatisPlusInterceptor.addInnerInterceptor(
                    new TenantLineInnerInterceptor(
                            new TenantLineHandler() {
                                @Override
                                public Expression getTenantId() {
                                    return new StringValue(TenantContext.getRequiredTenantId());
                                }
                                // 租户id 列名值
                                @Override
                                public String getTenantIdColumn(){
                                    return properties.getTenantIdColumn();
                                }

                                // 返回 false 表示所有表都需要拼多租户条件
                                @Override
                                public boolean ignoreTable(String tableName) {
                                    List<String> ignoreTables = properties.getIgnoreTables();
                                    return !CollectionUtils.isEmpty(ignoreTables) && ignoreTables.contains(tableName);
                                }
                            }
                    )
            );
        }
        // 多租户~动态名拦截器
        if (properties.getIsolationTable()) {
            DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
            dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
                // 获取参数方法
                String tenantId = TenantContext.getRequiredTenantId();
                List<String> ignoreTables = properties.getIgnoreTables();
                // 忽略表处理
                if(!CollectionUtils.isEmpty(ignoreTables) && ignoreTables.contains(tableName)){
                    return tableName;
                }
                return tenantId.concat(".").concat(tableName);

            });
            mybatisPlusInterceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        }
    }
}
