package com.doordu.doorsdk.bean;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.bean
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
