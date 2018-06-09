package com.dd.sdk.net;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.net
 * @class describe
 * @time 2018/6/5 10:19
 * @change
 * @class describe
 */

public interface NetCallback {

      void onResponse(Response response);

      void onError(NetError error);

      void onResponse(String str);
}
