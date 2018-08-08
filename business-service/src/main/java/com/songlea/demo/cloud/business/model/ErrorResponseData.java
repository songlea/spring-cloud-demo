package com.songlea.demo.cloud.business.model;

import java.sql.Timestamp;

/**
 * 错误内容
 *
 * @author Song Lea
 */
public class ErrorResponseData {

    // 异常处理
    public enum ExceptionEnum {
        EXCEPTION_SYSTEM_BUSY("系统正忙，请稍后重试！"),
        EXCEPTION_METHOD_NOT_SUPPORTED("请求的方式不对(POST/GET/PUT/DELETE...)！"),
        EXCEPTION_LACK_PARAMETER("请求的参数不完整！"),
        EXCEPTION_ARGUMENT_TYPE_MISMATCH("请求的参数格式不匹配！"),
        EXCEPTION_MEDIA_TYPE_NOT_ACCEPTABLE("请求的MINE类型不接受！"),
        EXCEPTION_MEDIA_TYPE_NOT_SUPPORTED("请求的MIME类型不支持！");

        private String message;

        ExceptionEnum(String message) {
            this.message = message;
        }

        public ErrorResponseData getResult(String path) {
            return new ErrorResponseData(path, this.message);
        }
    }

    private String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    private int status = 500;
    private String error = "Internal Server Error";
    private String message;
    private String path;


    private ErrorResponseData(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ErrorResponseData{" +
                "timestamp='" + timestamp + '\'' +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}