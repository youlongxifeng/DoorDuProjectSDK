package com.dd.sdk.net;

import android.util.Base64;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.net
 * @class describe
 * @time 2018/6/5 10:17
 * @change
 * @class describe
 */

public class Decoder {
    public byte[] decoder(byte[] data,int start,int lenght)
    {   return Base64.decode(data,start,lenght, Base64.DEFAULT);
    }

    public byte[] decoder(byte[] data)
    {
        return decoder(data, 0, data.length);
    }
}
