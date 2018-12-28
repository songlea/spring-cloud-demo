package com.songlea.demo.cloud.security.auth.userdetails;

import com.songlea.demo.cloud.security.model.db.SysMenu;
import com.songlea.demo.cloud.security.model.db.SysRole;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * 扩展UserDetailsService接口,添加其他的方法
 *
 * @author Song Lea
 */
public interface ExtendUserDetailsService extends UserDetailsService {

    List<SysMenu> selectAllSysMenu();

    List<SysRole> selectSysRoleByMenuId(Integer menuId);
}
