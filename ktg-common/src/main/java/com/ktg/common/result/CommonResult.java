package com.ktg.common.result;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 统一返回信息对象
 *
 * @author 丁国钊
 * @date 2022-12-5
 */
@Setter
@Getter
@NoArgsConstructor
public class CommonResult<T> {

    /** 状态码 */
    private Integer code;
    /** 信息 */
    private String message;
    /** 数据 */
    private T data;

    public CommonResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResult<T> failed() {
        return new CommonResult<>(ResultEnum.FAILED.getCode(), ResultEnum.FAILED.getMessage(), null);
    }

}
