package com.ap.context;

/**
 * 类名：TenantContextHolder.java
 * 描述：
 *
 * @author AP
 * @version 1.0
 * @date 2023/7/12 16:43
 */

/**
 * 多租户上下文
 */
public class TenantContext {

    /**
     * 当前租户编号
     * # 采用 InheritableThreadLocal 防止多线程处理时子线程无法获取到租户id
     */
    private static final ThreadLocal<String> TENANT_ID = new InheritableThreadLocal<>();

    /**
     * 是否忽略租户
     * # 采用 InheritableThreadLocal 防止多线程处理时子线程无法获取到租户id
     */
    private static final ThreadLocal<Boolean> IGNORE = new InheritableThreadLocal<>();


    /**
     * 获得租户编号。
     *
     * @return 租户编号
     */
    public static String getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * 获得租户编号。如果不存在，则抛出 NullPointerException 异常
     *
     * @return 租户编号
     */
    public static String getRequiredTenantId() {
        String tenantId = getTenantId();
        if (tenantId == null) {
            throw new NullPointerException("TenantContext 不存在租户编号");
        }
        return tenantId;
    }

    public static void setTenantId(String tenantId) {

        TENANT_ID.set(tenantId);
    }

    public static void setIgnore(Boolean ignore) {
        IGNORE.set(ignore);
    }

    /**
     * 当前是否忽略租户
     *
     * @return 是否忽略
     */
    public static boolean isIgnore() {
        return Boolean.TRUE.equals(IGNORE.get());
    }

    public static void clear() {
        TENANT_ID.remove();
        IGNORE.remove();
    }

}
