package com.songlea.demo.cloud.security.auth.ajax;

import com.songlea.demo.cloud.security.auth.userdetails.CustomUserDetails;
import com.songlea.demo.cloud.security.auth.userdetails.ExtendUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * ajax请求认证的具体实现类
 */
@Component
public class AjaxAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final ExtendUserDetailsService extendUserDetailsService;
    private final UserDetailsChecker userDetailsChecker;

    @Autowired
    public AjaxAuthenticationProvider(ExtendUserDetailsService extendUserDetailsService,
                                      PasswordEncoder passwordEncoder, UserDetailsChecker userDetailsChecker) {
        Assert.notNull(extendUserDetailsService, "extendUserDetailsService must be not null");
        Assert.notNull(passwordEncoder, "passwordEncoder must be not null");
        Assert.notNull(userDetailsChecker, "userDetailsChecker must be not null");
        this.extendUserDetailsService = extendUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsChecker = userDetailsChecker;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");

        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        UserDetails userDetails = extendUserDetailsService.loadUserByUsername(username);
        // 先验证用户状态
        userDetailsChecker.check(userDetails);
        // 由于用户是否存在与权限列表是否为空已经在获取UserDetails时验证过,此处只需要验证密码
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }
        // 默认实现的CustomUserDetailsService里返回的是CustomUserDetails
        CustomUserDetails.UserContext userContext = ((CustomUserDetails) userDetails).builderUserContext();
        return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
