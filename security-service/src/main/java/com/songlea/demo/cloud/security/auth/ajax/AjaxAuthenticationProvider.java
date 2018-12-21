package com.songlea.demo.cloud.security.auth.ajax;

import com.songlea.demo.cloud.security.auth.userdetails.CustomUserDetailsService;
import com.songlea.demo.cloud.security.model.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ajax请求认证的具体实现类
 */
@Component
public class AjaxAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserDetailsChecker userDetailsChecker;

    @Autowired
    public AjaxAuthenticationProvider(CustomUserDetailsService customUserDetailsService,
                                      PasswordEncoder passwordEncoder, UserDetailsChecker userDetailsChecker) {
        Assert.notNull(customUserDetailsService, "customUserDetailsService must be not null");
        Assert.notNull(passwordEncoder, "passwordEncoder must be not null");
        Assert.notNull(userDetailsChecker, "userDetailsChecker must be not null");
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsChecker = userDetailsChecker;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");

        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        // 先验证用户状态
        userDetailsChecker.check(userDetails);

        // 由于用户是否存在与权限列表是否为空已经在获取UserDetails时验证过,此处只需要验证密码
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        UserContext userContext = UserContext.create(username, new ArrayList<>(authorities));
        return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
