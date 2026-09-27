package com.cineverse.dto;

import java.util.Map;

public class ApiResponse<T> {

    private boolean success;
    private T data;
    private Integer count;
    private String message;
    private String error;

    public ApiResponse() {}

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.data = data;
        return r;
    }

    public static <T> ApiResponse<T> ok(T data, int count) {
        ApiResponse<T> r = ok(data);
        r.count = count;
        return r;
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        ApiResponse<T> r = ok(data);
        r.message = message;
        return r;
    }

    public static <T> ApiResponse<T> message(String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.message = message;
        return r;
    }

    public static ApiResponse<Map<String, Object>> fail(String error) {
        ApiResponse<Map<String, Object>> r = new ApiResponse<>();
        r.success = false;
        r.error = error;
        return r;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
