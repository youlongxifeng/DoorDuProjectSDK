package com.dd.sdk.netbean;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.netbean
 * @class describe
 * @time 2018/6/5 11:14
 * @change
 * @class describe
 */

public interface Encoder {
    byte[] encoder(String content);

     byte[] encode(byte[] content);
}
