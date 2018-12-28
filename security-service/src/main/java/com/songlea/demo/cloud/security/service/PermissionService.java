package com.songlea.demo.cloud.security.service;

import com.songlea.demo.cloud.security.model.db.SysMenu;
import com.songlea.demo.cloud.security.model.db.SysRole;
import com.songlea.demo.cloud.security.model.db.SysUser;

import java.util.List;
import java.util.Optional;

/**
 * 权限相关的服务层接口
 *
 * @author Song Lea
 * @since 2018-10-09 新建
 */
public interface PermissionService {

    List<SysRole> listSysRoleByUserId(Integer userId);

    Optional<SysUser> selectSysUserByAccount(String username);

    List<SysMenu> selectAllSysMenu();

    List<SysRole> selectSysRoleByMenuId(Integer menuId);
}
