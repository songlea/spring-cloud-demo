package com.songlea.demo.cloud.security.service;

import com.songlea.demo.cloud.security.model.db.SysMenu;
import com.songlea.demo.cloud.security.model.db.SysRole;
import com.songlea.demo.cloud.security.model.db.SysUser;

import java.util.List;

/**
 * 权限相关的服务层接口
 *
 * @author Song Lea
 * @since 2018-10-09 新建
 */
public interface PermissionService {

    List<SysRole> listSysRoleByUserId(Integer userId);

    SysUser selectSysUserByAccount(String username);

    List<SysMenu> selectAllSysMenu();

    List<SysRole> selectSysRoleByMenuId(Integer id);
}
