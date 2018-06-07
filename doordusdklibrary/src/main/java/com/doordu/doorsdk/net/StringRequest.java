package com.doordu.doorsdk.net;

import java.nio.ByteBuffer;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.netbean
 * @class describe
 * @time 2018/6/5 11:14
 * @change
 * @class describe
 */

public class StringRequest extends Request {

    protected String mContent;
    private int mLength;
    private byte[] mencoded;

    public StringRequest(String content) {
        mContent = content;
    }

    @Override
    public boolean write(ByteBuffer bufer) {
        boolean result = true;
        if(null != mEncoder)
        {
            if(null ==mencoded)
                mencoded = mEncoder.encoder(mContent);
            int len = mencoded.length;
            int r = bufer.remaining();
            if (r >= (len - mLength + 1)) {
                bufer.put(mencoded, mLength, len - mLength);
                bufer.put(INet.DIVIL_VALUE);
                mencoded = null;
            } else {
                bufer.put(mencoded, mLength, r);
                mLength += r;
                result = false;
            }
        }else{
            final byte[] b = mContent.getBytes();
            int len = mContent.length();
            int r = bufer.remaining();
            if (r >= (len - mLength + 1)) {
                bufer.put(b, mLength, len - mLength);
                bufer.put(INet.DIVIL_VALUE);
            } else {
                bufer.put(b, mLength, r);
                mLength += r;
                result = false;
            }
        }
        return result;
    }


}
