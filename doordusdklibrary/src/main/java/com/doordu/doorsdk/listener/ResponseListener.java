package com.doordu.doorsdk.listener;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.listener
 * @class describe
 * @time 2018/6/4 19:26
 * @change
 * @class describe
 */

public interface ResponseListener<T> {
    void onResponse(T var1, int var2);
}
