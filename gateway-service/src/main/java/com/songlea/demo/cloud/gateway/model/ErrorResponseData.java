package com.songlea.demo.cloud.gateway.model;

import java.sql.Timestamp;

/**
 * 错误内容
 *
 * @author Song Lea
 */
public class ErrorResponseData {

    public enum ExceptionEnum {

        NO_OPEN_API_TOKEN_HEAD("gateway service verification", "请求参数中没有token", 403);

        private String message;
        private String error;
        private int status;

        ExceptionEnum(String error, String message, int status) {
            this.error = error;
            this.message = message;
            this.status = status;
        }

        public ErrorResponseData getResult(String path) {
            return new ErrorResponseData(this.status, this.error, this.message, path);
        }
    }

    private String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    private int status;
    private String error;
    private String message;
    private String path;


    private ErrorResponseData(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
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