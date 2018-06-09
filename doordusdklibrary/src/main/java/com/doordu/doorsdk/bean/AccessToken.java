package com.doordu.doorsdk.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.bean
 * @class describe
 * @time 2018/6/7 17:42
 * @change
 * @class describe
 */

public class AccessToken implements Serializable{
    /**access_token**/
    public String token;
    /**有效期**/
    public String expires_in;

    public String getToken() {
        if(TextUtils.isEmpty(token)){
            return "0";
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AccessToken{");
        sb.append("token='").append(token).append('\'');
        sb.append(", expires_in='").append(expires_in).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
