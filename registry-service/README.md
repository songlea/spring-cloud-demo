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

### 2、Eureka REST operations
| Operation	| HTTP action | Description |
| ------ | ------ | ------ |
| Register new application instance	| POST /eureka/v2/apps/appID | Input: JSON/XML payload HTTP Code: 204 on success | 
| De-register application instance	| DELETE /eureka/v2/apps/appID/instanceID | HTTP Code: 200 on success | 
| Send application instance heartbeat| PUT /eureka/v2/apps/appID/instanceID	| HTTP Code: <br> * 200 on success <br> * 404 if instanceID doesn’t exist| 
| Query for all instances | GET /eureka/v2/apps	| HTTP Code: 200 on success Output: JSON/XML | 
| Query for all appID instances | GET /eureka/v2/apps/appID	| HTTP Code: 200 on success Output: JSON/XML | 
| Query for a specific appID/instanceID | GET /eureka/v2/apps/appID/instanceID	| HTTP Code: 200 on success Output: JSON/XML | 
| Query for a specific instanceID | GET /eureka/v2/instances/instanceID	| HTTP Code: 200 on success Output: JSON/XML | 
| Take instance out of service | PUT /eureka/v2/apps/appID/instanceID/status?value=OUT_OF_SERVICE | HTTP Code:<br> * 200 on success <br> * 500 on failure | 
| Move instance back into service (remove override)	| DELETE /eureka/v2/apps/appID/instanceID/status?value=UP (The value=UP is optional, it is used as a suggestion for the fallback status due to removal of the override) | HTTP Code:<br> * 200 on success <br> * 500 on failure | 
| Update metadata | PUT /eureka/v2/apps/appID/instanceID/metadata?key=value	| HTTP Code:<br> * 200 on success <br> * 500 on failure | 
| Query for all instances under a particular vip address | GET /eureka/v2/vips/vipAddress | * HTTP Code:<br> 200 on success Output: JSON/XML <br> * 404 if the vipAddress does not exist. | 
| Query for all instances under a particular secure vip address | GET /eureka/v2/svips/svipAddress | * HTTP Code:<br> 200 on success Output: JSON/XML <br> * 404 if the svipAddress does not exist.| 
