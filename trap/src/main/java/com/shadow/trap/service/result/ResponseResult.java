package com.shadow.trap.service.result;

import lombok.ToString;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author guoda
 * @date 2020/2/28 03:00
 */
@ToString
public class ResponseResult<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;
    private String message;
    private T data;

    public ResponseResult() {
    }

    public ResponseResult(String code, String message) {
        this(code, message, null);
    }

    public ResponseResult(String code, T data) {
        this(code, "", data);
    }

    public ResponseResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public ResponseResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 操作是否成功
     *
     * @return 操作结果
     */
    public boolean isSuccess() {
        return Optional.ofNullable(this.code).map("200"::equals).orElse(false);
    }
}
