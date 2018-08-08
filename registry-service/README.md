# spring-cloud-demo registry-service

#### 模块：服务注册中心

### 1、actuator的Endpoints 
* auditevents：陈列当前应用程序的审计事件信息
* autoconfig：展示自动配置信息并且显示所有自动配置候选人以及他们“被不被”应用的原因
* beans：显示应用程序中所有Spring bean的完整列表
* configprops：显示所有配置信息
* dump：dump所有线程
* env：陈列所有的环境变量
* flyway：Shows any Flyway database migrations that have been applied.
* health：显示应用程序运行状况信息
* info：显示应用信息
* loggers：显示和修改应用程序中的loggers配置
* liquibase：显示已经应用的任何Liquibase数据库迁移
* metrics：显示当前应用程序的“指标”信息
* mappings：显示所有@RequestMapping的url整理列表
* shutdown：关闭应用（默认情况下不启用）
* trace：显示跟踪信息（默认最后100个HTTP请求）
