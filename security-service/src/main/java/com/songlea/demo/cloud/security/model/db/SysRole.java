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
public class SysRole implements Serializable {

    private static final long serialVersionUID = 8748393690039674506L;

    private Integer id;
    private String code;
    private String name;
    private String desc;
    private Date createTime;

}