package com.doordu.doorsdk.listener;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.listener
 * @class describe
 * @time 2018/6/4 16:36
 * @change
 * @class describe
 * 注册监听
 */

public interface RegisterListener {
    /**
     * 已经注册
     */
    void alreadyRegistered();

    /**
     * 尚未注册
     */
    void unregistered();
}
