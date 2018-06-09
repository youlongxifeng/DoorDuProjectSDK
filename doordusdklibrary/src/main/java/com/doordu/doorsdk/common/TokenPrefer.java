package com.doordu.doorsdk.common;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.doordu.doorsdk.bean.AccessToken;

/** Token信息
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.common
 * @class describe
 * @time 2018/6/7 18:05
 * @change
 * @class describe
 *
 */
public class TokenPrefer {

    /**
     * 读取配置
     * @param context
     * @param info
     */
    public static void loadConfig(Context context, AccessToken info) {
        SharedPreferences share = context.getSharedPreferences("token_prefer", 0);
        info.token   = share.getString("access_token", "");
        info.expires_in   = share.getString("access_token", "");
    }

    /**
     * 保存配置
     * @param context
     * @param info
     */
    public static void saveConfig(Context context, final AccessToken info) {
        SharedPreferences share = context.getSharedPreferences("token_prefer", 0);
        Editor editor = share.edit();
        editor.putString("access_token", info.getToken());
        editor.putString("expires_in", info.getExpires_in());
        editor.commit();
    }

}