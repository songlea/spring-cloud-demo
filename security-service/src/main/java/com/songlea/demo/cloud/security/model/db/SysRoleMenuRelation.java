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
public class SysRoleMenuRelation implements Serializable {

    private static final long serialVersionUID = 234381101860231444L;

    private Integer id;
    private Long menuid;
    private Integer roleid;

}