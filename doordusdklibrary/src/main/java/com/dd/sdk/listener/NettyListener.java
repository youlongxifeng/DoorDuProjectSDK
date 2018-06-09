package com.dd.sdk.listener;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.listener
 * @class describe
 * @time 2018/6/6 17:56
 * @change
 * @class describe
 */

public interface NettyListener {
    byte STATUS_CONNECT_SUCCESS = 1;

    byte STATUS_CONNECT_CLOSED = 0;

    byte STATUS_CONNECT_ERROR = 0;


    /**
     * 当接收到系统消息
     */
    void onMessageResponse(Object msg);

    /**
     * 当服务状态发生变化时触发
     */
    void onServiceStatusConnectChanged(int statusCode);
}
