# Spring Cloud Demo

## 模块及其依赖说明
* registry-service 服务注册中心
    * spring-cloud-starter-netflix-eureka-server：Eureka服务端
    * spring-boot-starter-security：用于Web界面访问Basic验证
* config-service 配置中心
    * spring-cloud-starter-netflix-eureka-client：Eureka客户端
    * spring-cloud-config-server：Spring Cloud Config服务端
    * spring-cloud-starter-bus-amqp：用于配置的动态刷新(依赖RabbitMQ)
* gateway-service 服务网关
    * spring-cloud-starter-netflix-eureka-client：Eureka客户端
    * spring-cloud-starter-netflix-zuul：服务网关/智能路由(Zuul组件包含有Hystrix和Ribbon)
        * Ribbon：服务调用软负载均衡器
        * Hystrix：服务熔断机制 
    * spring-retry：用于服务调用重试机制
    * spring-cloud-zuul-ratelimit：用于服务调用限流(默认保存内存,集群下保存于redis需要依赖spring-boot-starter-data-redis)
* monitor-service 流量监控服务
    * spring-cloud-starter-netflix-hystrix-dashboard：提供Web可视化监控界面
    * spring-cloud-starter-netflix-turbine：汇聚hystrix的监控数据(包含Eureka客户端)
* business-service 业务层服务
    * spring-cloud-starter-netflix-eureka-client：Eureka客户端
    * spring-cloud-starter-config：Spring Cloud Config客户端
    * spring-cloud-starter-bus-amqp：用于@Value成员变量的动态更新
* config-repository 存放Spring Cloud Config的配置文件,HTTP服务以下形式获取资源：
    ~~~
    /{application}/{profile}[/{label}]
    /{application}-{profile}.yml
    /{label}/{application}-{profile}.yml
    /{application}-{profile}.properties
    /{label}/{application}-{profile}.properties
    ~~~