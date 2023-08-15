package com.ktg.common.result;

/**
 * 返回信息接口
 *
 * @author 丁国钊
 * @date 2022-12-6
 */
public interface ResultInterface {

    /**
     * 返回成功、失败码
     *
     * @return code
     */
    Integer getCode();

    /**
     * 返回信息
     *
     * @return message
     */
    String getMessage();

}
