package com.songlea.demo.cloud.security.mapper;

import com.songlea.demo.cloud.security.model.db.SysRole;

import java.util.List;

public interface SysRoleMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    List<SysRole> listSysRoleByUserId(Integer userId);

    List<SysRole> selectSysRoleByMenuId(Integer menuId);
}