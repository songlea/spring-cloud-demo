package com.songlea.demo.cloud.security.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser implements Serializable {

    private static final long serialVersionUID = -6056240253374276066L;

    public static final int USER_STATUS_ENABLE = 1;
    public static final int USER_STATUS_LOCK = 2;
    public static final int USER_STATUS_DELETE = 3;

    private Integer id;
    private String avatar;
    private String account;
    private String password;
    private String salt;
    private String name;
    private String birthday;
    private Integer sex;
    private String email;
    private String phone;
    private Integer deptId;
    private Integer status;
    private Date createTime;

}