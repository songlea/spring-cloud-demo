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
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 5101452305723609102L;

    private Integer id;
    private String code;
    private String pcode;
    private String pcodes;
    private String name;
    private String icon;
    private String url;
    private Integer num;
    private Integer levels;
    private Integer ismenu;
    private String tips;
    private Integer status;
    private Integer isopen;

}