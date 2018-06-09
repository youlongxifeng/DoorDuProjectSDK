package com.dd.sdk.net;

import java.nio.ByteBuffer;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.net
 * @class describe
 * @time 2018/6/4 16:10
 * @change
 * @class describe
 */

public abstract class Request implements Comparable<Request> {
    protected  Encoder mEncoder;
    /**
     *
     * @param bufer
     * @return true 数据全部写入 bufer中，false 当前bufer 不够写完
     */
    public abstract boolean write(ByteBuffer bufer);

    public boolean hasResponse()
    {
        return true;
    }

    @Override
    public int compareTo( Request other) {

        return 0;
    }

    public void setEncoder( Encoder encoder)
    {
        mEncoder = encoder;

    }
}
