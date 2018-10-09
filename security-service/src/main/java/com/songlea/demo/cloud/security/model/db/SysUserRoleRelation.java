package com.songlea.demo.cloud.security.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserRoleRelation implements Serializable {

    private static final long serialVersionUID = 7039923338739810723L;

    private Integer id;
    private Integer userId;
    private Integer roleId;

}