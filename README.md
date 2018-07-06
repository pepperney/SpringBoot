### SpringBootProejct

项目为SpringBoot项目,使用Gradle构建，使用到如下技术
 
 - NoSQL数据库Redis
 - 消息中间件RabbitMQ
 - Spring Retry重试机制
 - NIO通信框架Netty
 - Spring Scheule定时任务调度
 
包结构说明：
 
 - `com.pepper.common`:公共常量，枚举，统一异常，自定义注解，常用工具类
 
 - `com.pepper.learn`:学习的新东西，包括netty，java8语法，websocket协议
 
 - `com.pepper.web`:App后台系统架构，
 
web模块说明：
 
 - `com.pepper.web.config`为各种配置信息
 - `com.pepper.web.helper`为各种第三方中间件辅助处理类
 - `com.pepper.web.security`为系统的安全控制类，由filter实现
 
类说明：
 - `com.pepper.web.securityAuthFilter`：用于控制接口安全的类，通过application.yml文件的api.security选项控制所需进行的检验：
  
   ***timestamp*** 控制是否验证时间戳
  
   ***securityChains*** 控制是否验证接口权限，此处需要配置接口路径和对应的验证项(sign，token)，为none不做控制 
 - `com.pepper.web.security.MessyCodeFilter`：拦截请求中的非法字符
   
启动说明：项目启动时需预先在MQ后台系统建立如下对应关系的exchange和queue，其中exchange_test类型为topic
 - 将队列queue_test_1与exchange_test绑定，binding_key为key.topic.test.1
 - 将队列queue_test_2与exchange_test绑定，binding_key为key.topic.test.*
 
 
   
 