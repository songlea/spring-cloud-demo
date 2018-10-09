package com.songlea.demo.cloud.security.service.impl;

import com.songlea.demo.cloud.security.mapper.SysMenuMapper;
import com.songlea.demo.cloud.security.mapper.SysRoleMapper;
import com.songlea.demo.cloud.security.mapper.SysUserMapper;
import com.songlea.demo.cloud.security.model.db.SysMenu;
import com.songlea.demo.cloud.security.model.db.SysRole;
import com.songlea.demo.cloud.security.model.db.SysUser;
import com.songlea.demo.cloud.security.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 权限相关的服务层接口实现
 *
 * @author Song Lea
 * @since 2018-10-09 新建
 */
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    private SysUserMapper sysUserMapper;
    private SysRoleMapper sysRoleMapper;
    private SysMenuMapper sysMenuMapper;

    public PermissionServiceImpl() {
    }

    @Autowired
    public PermissionServiceImpl(SysRoleMapper sysRoleMapper, SysUserMapper sysUserMapper, SysMenuMapper sysMenuMapper) {
        Assert.notNull(sysRoleMapper, "sysRoleMapper cannot be null");
        Assert.notNull(sysUserMapper, "sysUserMapper cannot be null");
        Assert.notNull(sysMenuMapper, "sysMenuMapper cannot be null");
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public List<SysRole> listSysRoleByUserId(Integer userId) {
        return sysRoleMapper.listSysRoleByUserId(userId);
    }

    @Override
    public SysUser selectSysUserByAccount(String account) {
        List<SysUser> sysUsers = sysUserMapper.selectSysUserByAccount(account);
        if (sysUsers != null && !sysUsers.isEmpty()) {
            if (sysUsers.size() > 1) {
                // 用户名重复
                LOGGER.warn("There are {} identical user account name '{}'", sysUsers.size(), account);
            }
            return sysUsers.get(0);
        }
        return null;
    }

    @Override
    public List<SysMenu> selectAllSysMenu() {
        return sysMenuMapper.selectAllSysMenu();
    }

    @Override
    public List<SysRole> selectSysRoleByMenuId(Integer menuId) {
        return sysRoleMapper.selectSysRoleByMenuId(menuId);
    }

}
