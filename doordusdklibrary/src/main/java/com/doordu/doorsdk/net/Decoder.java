package com.doordu.doorsdk.net;

import android.util.Base64;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.net
 * @class describe
 * @time 2018/6/5 10:17
 * @change
 * @class describe
 */

public class Decoder {
    public byte[] decoder(byte[] data,int start,int lenght)
    {

        Decoder decoder = new Decoder(flags, new byte[len*3/4]);

        if (!decoder.process(input, offset, len, true)) {
            throw new IllegalArgumentException("bad base-64");
        }

        // Maybe we got lucky and allocated exactly enough output space.
        if (decoder.op == decoder.output.length) {
            return decoder.output;
        }

        // Need to shorten the array, so allocate a new one of the
        // right size and copy.
        byte[] temp = new byte[decoder.op];
        System.arraycopy(decoder.output, 0, temp, 0, decoder.op);

        return Base64.decode(data,start,lenght, Base64.DEFAULT);
    }

    public byte[] decoder(byte[] data)
    {
        return decoder(data, 0, data.length);
    }
}
