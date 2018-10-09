package com.songlea.demo.cloud.security.mapper;

import com.songlea.demo.cloud.security.model.db.SysMenu;

import java.util.List;

public interface SysMenuMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysMenu record);

    int insertSelective(SysMenu record);

    SysMenu selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysMenu record);

    int updateByPrimaryKey(SysMenu record);

    List<SysMenu> selectAllSysMenu();
}