package com.songlea.demo.cloud.security.endpoint;

import com.songlea.demo.cloud.security.auth.jwt.extractor.TokenExtractor;
import com.songlea.demo.cloud.security.auth.jwt.verifier.TokenVerifier;
import com.songlea.demo.cloud.security.config.CustomSecurityConfig;
import com.songlea.demo.cloud.security.config.JwtSettings;
import com.songlea.demo.cloud.security.exceptions.InvalidJwtTokenException;
import com.songlea.demo.cloud.security.model.UserContext;
import com.songlea.demo.cloud.security.model.db.SysRole;
import com.songlea.demo.cloud.security.model.db.SysUser;
import com.songlea.demo.cloud.security.model.token.JwtToken;
import com.songlea.demo.cloud.security.model.token.JwtTokenFactory;
import com.songlea.demo.cloud.security.model.token.RawAccessJwtToken;
import com.songlea.demo.cloud.security.model.token.RefreshToken;
import com.songlea.demo.cloud.security.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RefreshTokenEndpoint
 */
@RestController
public class RefreshTokenEndpoint {

    @Autowired
    private JwtTokenFactory tokenFactory;

    @Autowired
    private JwtSettings jwtSettings;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private TokenVerifier tokenVerifier;

    @Autowired
    @Qualifier("jwtHeaderTokenExtractor")
    private TokenExtractor tokenExtractor;

    @RequestMapping(value = "/api/auth/token", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public JwtToken refreshToken(HttpServletRequest request) {
        String tokenPayload = tokenExtractor.extract(request.getHeader(CustomSecurityConfig.AUTHENTICATION_HEADER_NAME));

        RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
        RefreshToken refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey())
                .orElseThrow(InvalidJwtTokenException::new);

        String jti = refreshToken.getJti();
        if (!tokenVerifier.verify(jti)) {
            throw new InvalidJwtTokenException();
        }
        String subject = refreshToken.getSubject();
        SysUser sysUser = permissionService.selectSysUserByAccount(subject)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + subject));

        List<SysRole> sysRoles = permissionService.listSysRoleByUserId(sysUser.getId());
        if (sysRoles == null || sysRoles.isEmpty()) {
            throw new InsufficientAuthenticationException("User has no roles assigned");
        }
        List<GrantedAuthority> authorities = sysRoles.stream()
                .filter(t -> StringUtils.isNotBlank(t.getCode()))
                .map(role -> new SimpleGrantedAuthority(role.getCode()))
                .collect(Collectors.toList());

        UserContext userContext = UserContext.create(sysUser.getAccount(), authorities);
        return tokenFactory.createAccessJwtToken(userContext);
    }

}
