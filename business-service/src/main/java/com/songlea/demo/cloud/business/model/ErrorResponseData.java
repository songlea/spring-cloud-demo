package com.songlea.demo.cloud.business.model;

import java.sql.Timestamp;

/**
 * 错误内容
 *
 * @author Song Lea
 */
public class ErrorResponseData {

    private String timestamp = new Timestamp(System.currentTimeMillis()).toString();
    private int status = 500;
    private String error = "Internal Server Error";
    private String message;
    private String path;


    public ErrorResponseData(String path, String message) {
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