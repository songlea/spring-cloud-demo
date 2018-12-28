package com.songlea.demo.cloud.security.auth.userdetails;

import com.songlea.demo.cloud.security.model.db.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

/**
 * 自定义的UserDetails
 *
 * @author Song Lea
 */
public class CustomUserDetails implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = 5369953176452532224L;
    private static final String ROLE_PREFIX = "ROLE_";

    // 用户表主键id
    private Integer id;
    // 用户名
    private String account;
    // 用户表密码
    private String password;
    // 用户表用户状态
    private Integer status;
    // 权限列表
    private Set<GrantedAuthority> authorities;

    public CustomUserDetails(Integer id, String account, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this(id, account, password, SysUser.USER_STATUS_ENABLE, authorities);
    }

    public CustomUserDetails(Integer id, String account, String password, Integer status,
                             Collection<? extends GrantedAuthority> authorities) {
        if (id == null || account == null || "".equals(account) || password == null) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }
        this.id = id;
        this.account = account;
        this.password = password;
        this.status = status;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
    }

    // 获取当前用户的id
    public Integer getCurrentUserId() {
        return this.id;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return account;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 用户非已删除状态
        return status != null && status != SysUser.USER_STATUS_DELETE;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 用户非锁定状态
        return status != null && status != SysUser.USER_STATUS_LOCK;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 密码永不过期(用户表没有密码过期字段,故这里不验证)
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 用户非禁用状态
        return status != null && status == SysUser.USER_STATUS_ENABLE;
    }

    @Override
    public void eraseCredentials() {
        // 清除密码
        password = null;
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof CustomUserDetails) {
            return account.equals(((CustomUserDetails) rhs).account);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return account.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Id: ").append(this.id).append("; ");
        sb.append("Username: ").append(this.account).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("status: ").append(this.status).append("; ");
        if (!authorities.isEmpty()) {
            sb.append("Granted Authorities: ");
            boolean first = true;
            for (GrantedAuthority auth : authorities) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                sb.append(auth);
            }
        } else {
            sb.append("Not granted any authorities");
        }
        return sb.toString();
    }

    // 对权限集合中的标识进行排序
    private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new CustomUserDetails.AuthorityComparator());
        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }
        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {

        private static final long serialVersionUID = -3368044519455069402L;

        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            if (g2.getAuthority() == null) {
                return -1;
            }
            if (g1.getAuthority() == null) {
                return 1;
            }
            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }

    public static UserBuilder withUsername(String username) {
        return new UserBuilder().username(username);
    }

    public static class UserBuilder {
        private Integer id;
        private String username;
        private String password;
        private Integer status;
        private List<GrantedAuthority> authorities;

        private UserBuilder() {
        }

        private UserBuilder username(String username) {
            Assert.notNull(username, "username cannot be null");
            this.username = username;
            return this;
        }

        public UserBuilder id(Integer id) {
            Assert.notNull(id, "id cannot be null");
            this.id = id;
            return this;
        }

        public UserBuilder password(String password) {
            Assert.notNull(password, "password cannot be null");
            this.password = password;
            return this;
        }

        public UserBuilder roles(String... roles) {
            List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
            for (String role : roles) {
                Assert.isTrue(!role.startsWith(ROLE_PREFIX), role + " cannot start with ROLE_");
                authorities.add(new SimpleGrantedAuthority(role));
            }
            return authorities(authorities);
        }

        public UserBuilder authorities(GrantedAuthority... authorities) {
            return authorities(Arrays.asList(authorities));
        }

        public UserBuilder authorities(List<? extends GrantedAuthority> authorities) {
            this.authorities = new ArrayList<>(authorities);
            return this;
        }

        public UserBuilder authorities(String... authorities) {
            return authorities(AuthorityUtils.createAuthorityList(authorities));
        }

        public UserBuilder status(Integer status) {
            this.status = status;
            return this;
        }

        public CustomUserDetails build() {
            return new CustomUserDetails(id, username, password, status, authorities);
        }
    }

    // 创建用户与权限对象,以便保存于jwt中
    public UserContext builderUserContext() {
        return new UserContext(account, new ArrayList<>(authorities));
    }

    public static class UserContext {

        private final String username;
        private final List<GrantedAuthority> authorities;

        private UserContext(String username, List<GrantedAuthority> authorities) {
            this.username = username;
            this.authorities = authorities;
        }

        public static UserContext create(String username, List<GrantedAuthority> authorities) {
            if (StringUtils.isBlank(username)) {
                throw new IllegalArgumentException("Username is blank: " + username);
            }
            return new UserContext(username, authorities);
        }

        public String getUsername() {
            return username;
        }

        public List<GrantedAuthority> getAuthorities() {
            return authorities;
        }
    }
}
