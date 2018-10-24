package com.songlea.demo.cloud.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultData<T> {

    public static final String LOGIN_SUCCESS = "login_success";
    public static final String LOGIN_FAILURE = "login_failure";

    // 200表示成功,其他失败
    private int code;

    // 提示信息
    private String message;

    // 接口返回的数据
    private T data;

}
