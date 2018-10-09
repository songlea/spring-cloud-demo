## Spring Security 的过滤器机制
* servlet请求按照一定的顺序从一个过滤器到下一个穿过整个过滤器链，最终到达目标servlet。与之相对的是，
    当servlet处理完请求并返回一个response时，过滤器链按照相反的顺序再次穿过所有的过滤器。
    
* Spring Security的自动配置选项auto-config为你建立了Spring Security的过滤器。这些过滤器以及它们使用的顺序(FilterComparator)：
    * ChannelProcessingFilter：通常是用来过滤哪些请求必须用https协议, 哪些请求必须用http协议, 哪些请求随便用哪个协议都行。
    ```
    <bean id="springSecurityFilterChain" class="org.springframework.security.web.FilterChainProxy">
    	<constructor-arg>
    		<list>
    			<sec:filter-chain pattern="/login" filters="none"/>
    			<sec:filter-chain pattern="/register" filters="none"/>
    			<sec:filter-chain pattern="/accessDenied" filters="none"/>
    			<sec:filter-chain pattern="/**/*.js" filters="none"/>
    			<sec:filter-chain pattern="/**" filters="channelProcessingFilter, ..."/>
    		</list>
    	</constructor-arg>
    </bean>
    <bean id="channelProcessingFilter" class="org.springframework.security.web.access.channel.ChannelProcessingFilter">
    	<property name="channelDecisionManager" ref="channelDecisionManager"/>
    	<property name="securityMetadataSource">
    		<sec:filter-security-metadata-source request-matcher="ant">
    		   <!-- "/my_login"必须用https协议来发送 -->
    		   <sec:intercept-url pattern="/my_login" access="REQUIRES_SECURE_CHANNEL"/>
    		   <!-- 其他的可以用http或https协议来发送 -->
    		   <sec:intercept-url pattern="/**" access="ANY_CHANNEL"/>
    		 </sec:filter-security-metadata-source>
    	</property>
    </bean>
    <bean id="channelDecisionManager" class="org.springframework.security.web.access.channel.ChannelDecisionManagerImpl">
    	<property name="channelProcessors">
    		<list>
    			<ref local="secureChannelProcessor"/>
    			<ref local="insecureChannelProcessor"/>
    		</list>
    	</property>
    </bean>
    <bean id="secureChannelProcessor" class="org.springframework.security.web.access.channel.SecureChannelProcessor">
    	<property name="entryPoint" ref="httpsEntryPort"/>
    </bean>
    <bean id="insecureChannelProcessor" class="org.springframework.security.web.access.channel.InsecureChannelProcessor">
    	<property name="entryPoint" ref="httpEntryPort"/>
    </bean>
    <bean id="httpEntryPort" class="org.springframework.security.web.access.channel.RetryWithHttpEntryPoint">
    	<property name="portMapper" ref="securePortMapper"/>
    </bean>
    <bean id="httpsEntryPort" class="org.springframework.security.web.access.channel.RetryWithHttpsEntryPoint">
    	<property name="portMapper" ref="securePortMapper"/>
    </bean>
    <!-- 这里做http与https端口的映射配置. 如果不配置, 默认的端口映射是"80-443"和"8080-8443" -->
    <bean id="securePortMapper" class="org.springframework.security.web.PortMapperImpl">
    	<property name="portMappings">
    		<map>
    			<entry key="8020" value="8021"/>
    		</map>
    	</property>
    </bean>
    ```    
    * ConcurrentSessionFilter：主要是判断session是否过期以及更新最新访问时间。
    * WebAsyncManagerIntegrationFilter：提供了对securityContext和WebAsyncManager的集成，方式是通过SecurityContextCallableProcessingInterceptor的beforeConcurrentHandling(NativeWebRequest, Callable)方法来讲SecurityContext设置到Callable上。
    * SecurityContextPersistenceFilter：负责从SecurityContextRepository获取或存储SecurityContext, SecurityContext代表了用户安全和认证过的session。
    * HeaderWriterFilter：用来给http响应添加一些Header,比如X-Frame-Options，X-XSS-Protection*，X-Content-Type-Options等。
        * X-Frame-Options：用来给浏览器指示允许一个页面可否在frame,iframe或者object中展现的标记。
            * DENY：表示该页面不允许在 frame 中展示，即便是在相同域名的页面中嵌套也不允许；
            * SAMEORIGIN：表示该页面可以在相同域名页面的 frame 中展示；
            * ALLOW-FROM uri：表示该页面可以在指定来源的 frame 中展示。
        * X-XSS-Protection*：当检测到跨站脚本攻击 (XSS)时，浏览器将停止加载页面。
            * 0：禁止XSS过滤；
            * 1：启用XSS过滤（通常浏览器是默认的）。 如果检测到跨站脚本攻击，浏览器将清除页面（删除不安全的部分）；
            * 1;mode=block：启用XSS过滤，如果检测到攻击，浏览器将不会清除页面，而是阻止页面加载；
            * 1; report=<reporting-URI>  (Chromium only)：启用XSS过滤。 如果检测到跨站脚本攻击，浏览器将清除页面并使用CSP report-uri指令的功能发送违规报告。
        * X-Content-Type-Options：通过设置"X-Content-Type-Options: nosniff"响应标头，对 script 和 styleSheet 在执行是通过MIME 类型来过滤掉不安全的文件。
        * Content-Security-Policy：使用白名单的方式告诉客户端（浏览器）允许加载和不允许加载的资源；向服务器举报这种强贴牛皮鲜广告的行为，以便做出更加针对性的措施予以绝杀。
        * Referrer-Policy：首部用来监管哪些访问来源信息会在 Referer  中发送和应该被包含在生成的请求当中。
            * no-referrer：整个 Referer  首部会被移除。访问来源信息不随着请求一起发送；
            * no-referrer-when-downgrade （默认值）：在没有指定任何策略的情况下用户代理的默认行为。在同等安全级别的情况下，引用页面的地址会被发送(HTTPS->HTTPS)，但是在降级的情况下不会被发送 (HTTPS->HTTP)；
            * origin：在任何情况下，仅发送文件的源作为引用地址。例如 https://example.com/page.html 会将 https://example.com/ 作为引用地址；
            * origin-when-cross-origin：对于同源的请求，会发送完整的URL作为引用地址，但是对于非同源请求仅发送文件的源；
            * same-origin：对于同源的请求会发送引用地址，但是对于非同源请求则不发送引用地址信息；
            * strict-origin：在同等安全级别的情况下，发送文件的源作为引用地址(HTTPS->HTTPS)，但是在降级的情况下不会发送 (HTTPS->HTTP)；
            * strict-origin-when-cross-origin：对于同源的请求，会发送完整的URL作为引用地址；在同等安全级别的情况下，发送文件的源作为引用地址(HTTPS->HTTPS)；在降级的情况下不发送此首部 (HTTPS->HTTP)；
            * unsafe-url：无论是同源请求还是非同源请求，都发送完整的 URL（移除参数信息之后）作为引用地址。
    * CorsFilter：当一个资源从与该资源本身所在的服务器不同的域或端口不同的域或不同的端口请求一个资源时，资源会发起一个跨域 HTTP 请求，出于安全考虑，浏览器会限制从脚本内发起的跨域HTTP请求。
        该过滤器的工作原理是将所需的Access-Control- *标头添加到HttpServletResponse对象。 该过滤器还可以防止HTTP响应分裂。 如果请求无效或不被允许，则使用HTTP状态代码403（禁止）拒绝请求。
    * CsrfFilter：防止CSRF攻击过滤器。
    * LogoutFilter：监控一个实际为退出功能的URL（默认为/j_spring_security_logout），并且在匹配的时候完成用户的退出功能。
    * UsernamePasswordAuthenticationFilter：监控一个使用用户名和密码基于form认证的URL（默认为/j_spring_security_check），并在URL匹配的情况下尝试认证该用户。
    * DefaultLoginPageGeneratingFilter：监控一个要进行基于form或OpenID认证的URL（默认为/spring_security_login），并生成展现登录form的HTML。
    * BasicAuthenticationFilter：监控HTTP 基础认证的头信息并进行处理。
    * RequestCacheAwareFilter：用于用户登录成功后，重新恢复因为登录被打断的请求。
    * SecurityContextHolderAwareRequestFilter：用一个扩展了HttpServletRequestWrapper的子类（SecurityContextHolderAwareRequestWrapper）包装HttpServletRequest，它为请求处理器提供了额外的上下文信息。
    * AnonymousAuthenticationFilter：如果用户到这一步还没有经过认证，将会为这个请求关联一个认证的token，标识此用户是匿名的。
    * SessionManagementFilter：根据认证的安全实体信息跟踪session，保证所有关联一个安全实体的session都能被跟踪到。
    * ExceptionTranslationFilter：解决在处理一个请求时产生的指定异常。
    * FilterSecurityInterceptor：简化授权和访问控制决定，委托一个AccessDecisionManager完成授权的判断。
    
如UsernamePasswordAuthenticationFilter配置
 ```
 <bean id="loginAuthenticationFilter" class="org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter">
    <property name="filterProcessesUrl" value="/my_login" /><!-- 表单提交的url,默认是/j_spring_security_check -->
    <property name="usernameParameter" value="my_username"/><!-- 表单里用户名字段的name,默认是j_username -->
    <property name="passwordParameter" value="my_password"/><!-- 表单里密码字段的name,默认是j_password -->
    <property name="authenticationManager" ref="authenticationManager"/> <!-- 必须配置,这里使用上面定义的authenticationManager-->
    <property name="authenticationFailureHandler" ref="authenticationFailureHandler"/> <!-- 验证失败时的处理器 -->
    <property name="authenticationSuccessHandler" ref="authenticationSuccessHandler"/> <!-- 验证成果时的处理器 -->
</bean>
<bean id="authenticationSuccessHandler" class="org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler">	
    <property name="defaultTargetUrl" value="/index"/> <!-- 验证成功时跳到哪个请求 -->
    <property name="alwaysUseDefaultTargetUrl" value="true"/>
</bean> 
<bean id="authenticationFailureHandler" class="org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler">	
    <property name="defaultFailureUrl" value="/login"/> <!-- 验证失败时跳到哪个请求 -->
</bean>
```