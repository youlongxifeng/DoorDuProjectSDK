package com.dd.sdk.net;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.net
 * @class describe
 * @time 2018/6/5 10:20
 * @change
 * @class describe
 */

public interface Encoder {
    byte[] encoder(String content);
    byte[] encode(byte[] content);
}
