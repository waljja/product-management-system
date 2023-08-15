package com.ktg.common.result;

/**
 * 返回信息枚举
 *
 * @author 丁国钊
 * @date 2022-12-6
 */
public enum ResultEnum implements ResultInterface {
    SUCCESS(100,"成功"),
    FAILED(500,"失败");

    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

