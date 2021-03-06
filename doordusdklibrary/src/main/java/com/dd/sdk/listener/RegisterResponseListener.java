package com.dd.sdk.listener;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.listener
 * @class describe
 * @time 2018/6/4 16:47
 * @change
 * @class describe
 * 注册返回
 */

public interface RegisterResponseListener {
    /**
     * 注册返回成功
     */
    void responseSuccess();

    /**
     * 注册返回失败
     */
    void responseFail();
}
