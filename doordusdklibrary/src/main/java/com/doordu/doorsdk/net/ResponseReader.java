package com.doordu.doorsdk.net;

import java.nio.ByteBuffer;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.net
 * @class describe
 * @time 2018/6/5 10:23
 * @change
 * @class describe
 */

public class ResponseReader {
    private ByteBuffer tmp;
    private final byte mDiviler;
    public ResponseReader(){
        mDiviler = INet.DIVIL_VALUE;
        tmp = ByteBuffer.allocate(128*1024); //FIXME zxj
    }
    public Response response(ByteBuffer bufer)
    {
        bufer.flip();
        int limit = bufer.limit();
        byte b = 0;
        while(bufer.position() < limit)
        {
            b = bufer.get();
            if(b != mDiviler)
            {
                tmp.put(b);
            }else{
                byte[] d = new byte[tmp.position()];
                System.arraycopy(tmp.array(), 0, d, 0, tmp.position());
                tmp.clear();
                bufer.compact();
                return Response.obtain(0, d);
            }
        }
        bufer.clear();
        return null;

    }
}
