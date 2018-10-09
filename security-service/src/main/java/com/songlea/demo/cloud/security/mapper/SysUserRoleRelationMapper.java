package com.songlea.demo.cloud.security.mapper;

import com.songlea.demo.cloud.security.model.db.SysUserRoleRelation;

public interface SysUserRoleRelationMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(SysUserRoleRelation record);

    int insertSelective(SysUserRoleRelation record);

    SysUserRoleRelation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUserRoleRelation record);

    int updateByPrimaryKey(SysUserRoleRelation record);
}