package com.songlea.demo.cloud.security.endpoint;

import com.songlea.demo.cloud.security.auth.jwt.JwtAuthenticationToken;
import com.songlea.demo.cloud.security.auth.jwt.extractor.TokenExtractor;
import com.songlea.demo.cloud.security.auth.jwt.verifier.TokenVerifier;
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
public class TokenEndpoint {

    /**
     * token的请求头标识
     */
    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";

    /**
     * 请求获取token与refreshToken的URL
     */
    public static final String AUTHENTICATION_URL = "/api/auth/login";

    /**
     * 重新获取token的URL
     */
    public static final String REFRESH_TOKEN_URL = "/api/auth/token/refresh";

    /**
     * 获取token里用户标识的URL
     */
    public static final String GET_ME_URL = "/api/me";

    /**
     * 需要token验证的根URL
     */
    public static final String API_ROOT_URL = "/api/**";

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

    @RequestMapping(value = AUTHENTICATION_URL, method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public JwtToken refreshToken(HttpServletRequest request) {
        String tokenPayload = tokenExtractor.extract(request.getHeader(AUTHENTICATION_HEADER_NAME));

        RawAccessJwtToken rawToken = new RawAccessJwtToken(tokenPayload);
        RefreshToken refreshToken = RefreshToken.create(rawToken, jwtSettings.getTokenSigningKey())
                .orElseThrow(() -> new InvalidJwtTokenException("Invalid jwt token"));

        // 验证token的jti(唯一标识符)
        String jti = refreshToken.getJti();
        if (!tokenVerifier.verify(jti)) {
            throw new InvalidJwtTokenException("Invalid jwt token");
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

    @RequestMapping(value = GET_ME_URL, method = RequestMethod.GET)
    public UserContext get(JwtAuthenticationToken token) {
        return (UserContext) token.getPrincipal();
    }
}
