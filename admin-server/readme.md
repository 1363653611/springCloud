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
```yaml
management:
  security:
    enabled: false
    
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
 
 
 
