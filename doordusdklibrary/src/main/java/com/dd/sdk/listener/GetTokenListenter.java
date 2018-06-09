package com.dd.sdk.listener;

import com.dd.sdk.bean.AccessToken;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.listener
 * @class describe
 * @time 2018/6/7 17:41
 * @change
 * @class describe
 */

public interface GetTokenListenter {
    void onSucces(AccessToken accessToken);

    void onError();
}
