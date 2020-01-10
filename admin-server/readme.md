### spring-admin
 - spring-actuator 是spring-boot 自带的对应用程序的监控服务.但是spring-actuator 接口返回的数据是json格式的,所以spring 开发团队就开发出了基于UI 的spring-admin 监控工具.
 - spring-admin 是一个监控和管理spirngboot 应用程序的开源软件. 每个应用都可以认为是一个客户端.通过HTTP 或者 服务注册发现的方式注册到spring-admin中展示.Spring Boot Admin UI部分使用AngularJs将数据展示在前端.
 - spring-admin 针对spring-actuator 监控接口提供UI可视化的美化和封装的工具.
 - 具备功能:
    - 在列表中浏览所有被监控spring-boot项目的基本信息，
    - 详细的Health信息、内存信息、JVM信息、垃圾回收信息、
    - 各种配置信息（比如数据源、缓存列表和命中率）等，
    - 还可以直接修改logger的level
 #### 示例
 - Spring Boot Admin包含admin-server与admin-client两个组件: admin-server通过采集actuator端点数据，显示在spring-boot-admin-ui上。
 
 
 #### server 编写
 引入 maven 依赖
 ```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
</dependency>
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-server-ui</artifactId>
</dependency>
```
- 添加maven依赖
```yaml
server:
  port: 8080
  servlet:
    context-path: /admin-server

```
- 启动:http://localhost:8080/admin-server：
### client

- 引入MAVEN
```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
</dependency>
```
- 添加配置
> 在 Spring Boot 2.x 中为了安全期间，Actuator 只开放了两个端点 /actuator/health 和 /actuator/info。可以在配置文件中设置 `web.exposure.include: '*' ` 打开所有配置

```yaml
management:
  endpoint:
    health:
      show-details: always
  #  security:
#    enabled: false  # 2.0 已经废弃
  endpoints:
    jmx:
      exposure:
        include: '*'
    web:
      exposure:
        include: '*' #在 Spring Boot 2.x 中为了安全期间，Actuator 只开放了两个端点 /actuator/health 和 /actuator/info。可以在配置文件中设置打开
    
server:
  port: 8081
  
spring:
  boot:
    admin:
      url: http://localhost:8080/admin-server
```

- spring.boot.admin.url配置的是SBA服务端的地址，management.security.enabled: false这里先关闭安全验证。启动客户端后查看http://localhost:8080/admin-server地址
 
#### 客户端额外信息
- 在客户端的yml 文件中添加
```yaml
info: 
  app:  
    name: "@project.name@"
    description: "@project.description@"  
    version: "@project.version@"  
    spring-boot-version: "@project.parent.version@"
```

#### 服务端添加右键警告
- 依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

- 邮件配置
```yaml
spring:
  mail:
    host: smtp.163.com
    username: xxx@163.com
    password: xxx
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true

  boot:
    admin:
      notify:
        mail:
          from: xxx@163.com
          to: xxx@qq.com
```

### security 配置
> 因为客户端需要暴露一些端点（SpringBoot Actuator），若是服务部署在外网上，可能会造成信息泄露，故需要使用Spring Security进行安全认证
> 需要注意的是：若是加入了security的maven依赖后，会自动的对所有路径使用httpBasic安全认证。

1. 若不需要配置，则在项目中添加配置文件 `com.zbcn.adminclient.config.SecurityPermitAllConfig` 配置忽略所有权限

#### 添加权限功能
1. 客户端 
    1. 添加依赖
    ```xml
       <!--整合安全机制-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
    ```
    2. 添加安全配置config类 `com.zbcn.adminclient.config.SecurityConfiguration` 
    3. yml 文件中添加 配置
    ```yaml
    spring.boot.admin.client:
       username: user
       password: 123456
       metadata:
           user.name: ${spring.security.user.name} #客户端元数据访问用户
           user.password: ${spring.security.user.password} #客户端元数据访问密码
    ```
2. 服务端
    1. 添加依赖
        ```xml
           <!--整合安全机制-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-security</artifactId>
            </dependency>
        ```
   2. 添加权限校验 `com.zbcn.adminserver.config.SecurityConfig` 和 `com.zbcn.adminserver.config.monitorConfig`
   3. 修改配置文件   
      ```yml
       spring:
         security:
           user:
             name: admin
             password: 123456
    
        ```
### client 无法注册到 server中的踩坑记录
- 不能使用spring admin 官方教程中的 三个配置类，需要使用spring 自带的 `@AdminServerApplication`
### 自定义邮件

- 加入模板引擎依赖
```xml
<!-- 加入模版引擎 freemarker: 支持mail服务 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-freemarker</artifactId>
</dependency>
```
- `server` 添加 自定义邮件组件 `com.zbcn.adminserver.mail.CustomMailNotifier`
- `server` 自定义邮件发送功能关闭
```yaml
#关闭自带的邮件服务
boot:
    admin:
      notify:
        mail:
          enabled: false 
# 添加自定义邮件功能
spring:
  mail:
    host: smtp.qq.com
    port: -1 # SMTP服务器端口号 默认-1
    username: 1363653611@qq.com
    password: xxxx # 发送方密码（授权码）
    to:  zbcn810@163.com
    cc: zbcn810@163.com
    properties:
      mail:
        smtp:
          auth: true
```
- templates 中添加邮件模板

### 拓展
- yml 配置详解（string，对象，数组）
```yaml
config-attributes:
  value: 345                          #对应单个值
  valueArray: 1,2,3,4,5,6,7,8,9      #对应数组
  valueList:                         #对应list
    -13579
    -246810
  valueMap:                          #对应map
    name: lili
    age: 20
    sex: female
  valueMapList:                      #对应list<map>
    - name: bob
      age: 21
    - name: caven
      age: 31

```
### 参考：
- [Spring Boot Admin Tutorial] https://www.vojtechruzicka.com/spring-boot-admin/ 

- [SpringBoot2.x整合监控（2-SpringBoot Admin] https://www.jianshu.com/p/21c32276b2d3
 
 
