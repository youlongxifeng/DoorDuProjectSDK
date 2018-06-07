package com.doordu.doorsdk.net;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.net
 * @class describe
 * @time 2018/6/5 10:14
 * @change
 * @class describe
 */

public interface ResponseParser {
    void parser(Response response);

      void parserError(NetError error);

      void parser(String string);
}
