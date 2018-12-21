package com.songlea.demo.cloud.security.auth.userdetails.checker;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

/**
 * 用户状态校验
 */
@Component
public class CustomUserDetailsChecker implements UserDetailsChecker {

    @Override
    public void check(UserDetails userDetails) {
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("User account is locked");
        }

        if (!userDetails.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("User account has expired");
        }

        if (!userDetails.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("User credentials have expired");
        }
    }
}
