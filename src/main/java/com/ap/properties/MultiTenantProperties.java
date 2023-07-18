package com.ap.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 类名：MultiTenantProperties.java
 * 描述：多租户配置
 *
 * @author AP
 * @version 1.0
 * @date 2023/7/12 10:02
 */
@ConfigurationProperties(prefix = MultiTenantProperties.PREFIX)
public class MultiTenantProperties {


    public static final String PREFIX = "multi-tenant";

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 租户id列名称
     */
    private String tenantIdColumn;

    /**
     * 忽略多租户限制条件的库表名
     */
    private List<String> ignoreTables;

    /**
     * request請求header 中的多租户标识
     */
    private String headerTenantId;
    /**
     * 基于字段tenantIdColumn的行级别隔离
     */

    private boolean isolationRow;
    /**
     * 基于Schema.表名的表级别隔离
     */
    private boolean isolationTable;

    public boolean getIsolationRow() {
        return isolationRow;
    }

    public void setIsolationRow(boolean isolationRow) {
        this.isolationRow = isolationRow;
    }

    public boolean getIsolationTable() {
        return isolationTable;
    }

    public void setIsolationTable(boolean isolationTable) {
        this.isolationTable = isolationTable;
    }

    public String getHeaderTenantId() {
        return headerTenantId;
    }

    public void setHeaderTenantId(String headerTenantId) {
        this.headerTenantId = headerTenantId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    public void setTenantIdColumn(String tenantIdColumn) {
        this.tenantIdColumn = tenantIdColumn;
    }

    public List<String> getIgnoreTables() {
        return ignoreTables;
    }

    public void setIgnoreTables(List<String> ignoreTables) {
        this.ignoreTables = ignoreTables;
    }
}
