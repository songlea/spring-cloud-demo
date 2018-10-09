package com.songlea.demo.cloud.security.mapper;

import com.songlea.demo.cloud.security.model.db.SysRoleMenuRelation;

public interface SysRoleMenuRelationMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleMenuRelation record);

    int insertSelective(SysRoleMenuRelation record);

    SysRoleMenuRelation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleMenuRelation record);

    int updateByPrimaryKey(SysRoleMenuRelation record);
}