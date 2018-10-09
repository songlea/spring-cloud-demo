package com.songlea.demo.cloud.security.mapper;

import com.songlea.demo.cloud.security.model.db.SysUser;

import java.util.List;

public interface SysUserMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    List<SysUser> selectSysUserByAccount(String account);
}