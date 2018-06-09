package com.dd.sdk.bean;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.bean
 * @class describe
 * @time 2018/6/7 17:53
 * @change
 * @class describe
 */

public class BaseResponse<T> {
    public int code;
    public T data;

    public boolean isSuccess() {
        return  code==200;
    }
}
