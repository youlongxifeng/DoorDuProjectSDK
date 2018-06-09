package com.doordu.doorsdk.listener;

import com.doordu.doorsdk.bean.AccessToken;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.listener
 * @class describe
 * @time 2018/6/7 17:41
 * @change
 * @class describe
 */

public interface GetTokenListenter {
    void onSucces(AccessToken accessToken);

    void onError();
}
