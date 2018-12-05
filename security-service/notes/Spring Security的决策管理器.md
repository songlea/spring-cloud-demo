## AccessDecisionManager
* Spring的决策管理器，抽象类为AbstractAccessDecisionManager，自定义决策管理器一般是继承抽象类而不是实现接口。
* Spring提供了3个决策管理器
    * AffirmativeBased 一票通过，只要有一个投票器通过才允许访问资源
        * 只要有AccessDecisionVoter的投票为ACCESS_GRANTED则同意用户进行访问；
        * 如果全部弃权抛出AccessDeniedException(见源码org.springframework.security.access.vote.AffirmativeBased)；
        * 如果没有一个人投赞成票，但是有人投反对票，则将抛出AccessDeniedException。
    * ConsensusBased 有一半以上投票器通过才允许访问资源
        * 如果赞成票多于反对票则表示通过；
        * 反过来，如果反对票多于赞成票则将抛出AccessDeniedException；
        * 如果赞成票与反对票相同且不等于0，并且属性allowIfEqualGrantedDeniedDecisions的值为true，则表示通过，否则将抛出异常AccessDeniedException，参数allowIfEqualGrantedDeniedDecisions的值默认为true；
        * 如果所有的AccessDecisionVoter都弃权了，则将视参数allowIfAllAbstainDecisions的值而定，如果该值为true则表示通过，否则将抛出异常AccessDeniedException，参数allowIfAllAbstainDecisions的值默认为false。
    * UnanimousBased 所有投票器都通过才允许访问资源
        * 如果受保护对象配置的某一个ConfigAttribute被任意的AccessDecisionVoter反对了，则将抛出AccessDeniedException；
        * 如果没有反对票，但是有赞成票，则表示通过；
        * 如果全部弃权了，则将视参数allowIfAllAbstainDecisions的值而定，true则通过，默认false则抛出AccessDeniedException。

#### AccessDecisionVoter
* 投票器的概念，有无权限访问的最终决策权是由投票器来决定的，最常见的投票器为RoleVoter，在RoleVoter中定义了权限的前缀。
```
// Authentication中用户及用户权限信息;
// attributes是访问资源需要的权限，然后循环判断用户是否有访问资源需要的权限，如果有就返回ACCESS_GRANTED;
public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {  
    int result = ACCESS_ABSTAIN;  
    Collection<? extends GrantedAuthority> authorities = extractAuthorities(authentication);  
    for (ConfigAttribute attribute : attributes) {  
        if (this.supports(attribute)) {  
            result = ACCESS_DENIED;  
            // Attempt to find a matching granted authority  
            for (GrantedAuthority authority : authorities) {  
                if (attribute.getAttribute().equals(authority.getAuthority())) {  
                    return ACCESS_GRANTED;  
                }  
            }  
        }  
    }  
    return result;  
}  
Collection<? extends GrantedAuthority> extractAuthorities(Authentication authentication) {  
    return authentication.getAuthorities();  
} 
```
Spring 提供的投票器
* AuthenticatedVoter：支持IS_AUTHENTICATED认证
    * IS_AUTHENTICATED_ANONYMOUSLY 不管用户是匿名的还是已经认证的都将投赞成票；
    * IS_AUTHENTICATED_FULLY 则仅当用户是通过登录入口进行登录的才会投赞成票，否则将投反对票；
    * IS_AUTHENTICATED_REMEMBERED 则仅当用户是由Remember-Me自动登录，或者是通过登录入口进行登录认证时才会投赞成票，否则将投反对票；
* RoleVoter：其会将ConfigAttribute简单的看作是一个角色名称，在投票的时如果拥有该角色即投赞成票；
    * 如果ConfigAttribute是以“ROLE_”开头的，则将使用RoleVoter进行投票
    * 当用户拥有的权限中有一个或多个能匹配受保护对象配置的以“ROLE_”开头的ConfigAttribute时其将投赞成票；
    * 如果用户拥有的权限中没有一个能匹配受保护对象配置的以“ROLE_”开头的ConfigAttribute，则RoleVoter将投反对票；
    * 如果受保护对象配置的ConfigAttribute中没有以“ROLE_”开头的，则RoleVoter将弃权；
* WebExpressionVoter：access属性用来定义访问配置属性，设置http元素的use-expressions=”true”
可以启用intercept-url元素的access属性对Spring EL表达式的支持，use-expressions的值默认为false。
    * hasIpAddress(ipAddress) 用于匹配一个请求的ip地址或一个地址的网络掩码；
    * hasRole(role) 用于匹配一个使用GrantedAuthority的角色(类似于RoleVoter)；
    * hasAnyRole(role) 用于匹配一个使用GrantedAuthority的角色列表，用于匹配其中的任何一个均可放行；
    * 还有一系列的方法可以作为属性，它们不需要圆括号或方法参数
        * permitAll 任何用户均可访问，access="permitAll"
        * denyAll 任何用户均不可访问
        * anonymous 匿名用户可访问
        * authenticated 检查用户是否认证过
        * rememberMe 检查用户是否通过remember me功能认证的
        * fullyAuthenticated 检查用户是否通过提供完整的凭证信息来认证的


