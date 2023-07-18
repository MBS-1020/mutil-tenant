# MyBatis-Plus 多租户 【表、行级别数据隔离】

基于多租户插件和动态表名插件实现

**要求：**   

集成项目使用MyBatis-Plus 3.5.0以上版本 仅测试 **mysql库（版本8.0.26）** 

**表隔离：** 

同一个Mysql实例下不同库(Schema)相同表结构、通过动态库名.表名进行隔离查询   
要求请求参数tenant-id的值与库名一致 

**行隔离：**  

yaml配置的tenant-id-column、header-tenant-id值  
实现查询sql动态增加 tenant_id = "请求头tenant-id的值"达到行数据隔离
## 1、yml配置

```yaml
# 多租户
multi-tenant:
  # 是否开启多租户处理 true 开启 false 关闭
  enabled: true
  # 表对应的租户字段名称
  tenant-id-column: "tenant_id"
  # 请求头中租户key值
  header-tenant-id: "tenant-id"
  # Schema table隔离 true 开启 false 关闭
  isolation-table: true
  # Row 隔离 true 开启 false 关闭
  isolation-row: true
  # 忽略表
  ignore-tables:
    - message
    - user
```

## 2、请求参数获取 【拦截器、过滤器二选一】

```java
// 基于 filter 过滤器获取
@Import(TenantContextFilter.class)

// 基于 mvc 拦截器获取
@Configuration
@Import(TenantContextInterceptor.class)
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    TenantContextInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器
        InterceptorRegistration registration = registry.addInterceptor(interceptor);
        registration.addPathPatterns("/**");//所有路径都被拦截
        registration.excludePathPatterns(//添加不拦截路径
                "你的登陆路径",//登录
                "/**/*.html",//html静态资源
                "/**/*.js",//js静态资源
                "/**/*.css",//css静态资源
                "/**/*.woff",
                "/**/*.ttf"
        );
    }
}
```

## 3、注解方式忽略多租户配置处理
```java
/**
 * 内置插件的一些过滤规则
 * 支持注解在 Mapper 上以及 Mapper.Method 上
 * 同时存在则 Mapper.method 比 Mapper 优先级高
 * 支持:
 * true 和 false , 1 和 0 , on 和 off
 * 各属性返回 true 表示不走插件(在配置了插件的情况下,不填则默认表示 false)
 *
 */
@InterceptorIgnore(tenantLine = "1", dynamicTableName = "1")

```
    
#### 注 
mybatisPlus 低版本多租户处理有缺陷：
如：3.1.1版本 针对如下场景无效: 3.2/3.3/3.4版本未验证，因多表关联bug是在3.5.0版本后修复的

**场景①、** from后的表为无需租户处理时，处理失效
user_addr 无需租户处理  、sys_user 需要租户处理的表

```sql
# 3.1.1
SELECT a.name AS addr_name, u.id, u.name
FROM user_addr a
LEFT JOIN sys_user u ON a.user_id = u.id  
```
**场景②、** insert 时，插入语句中不可包含【tenant_id】字段，否则插入异常

```sql
INSERT INTO sys_user (id, tenant_id, name) VALUES
(1, 1, 'xxxx')

# 处理后 造成异常
INSERT INTO sys_user (id, tenant_id, name, tenant_id) VALUES
(1, 1, 'xxxx', 'xx')
```


**场景③、** 多表关联Bug
```sql
# 当多租户处理表为关联表时,添加租户过滤条件的位置是在 ON 关联条件上 造成租户筛选失效
SELECT a.name AS addr_name, u.id, u.name,u.tenant_id
FROM user_addr a
RIGHT JOIN sys_user u ON u.id = a.user_id and u.tenant_id = 1;

# 3.5.0版本后修复此bug
SELECT a.name AS addr_name, u.id, u.name,u.tenant_id
FROM user_addr a
RIGHT JOIN sys_user u ON u.id = a.user_id
where u.tenant_id = 1;
```



