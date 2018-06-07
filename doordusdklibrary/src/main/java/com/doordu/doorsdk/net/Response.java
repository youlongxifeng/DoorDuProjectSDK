package com.doordu.doorsdk.net;

import java.util.Arrays;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.net
 * @class describe
 * @time 2018/6/5 10:14
 * @change
 * @class describe 解析内容
 */

public class Response {
    private static final Object sPoolSync = new Object();
    private static Response sPool;
    Response next;
    Response values;
    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 15;


    int cmd;
    byte[] data;
    Object attach;

    public static Response obtain(int cmd, byte[] data) {
        return obtain(cmd, data, data);
    }

    public static Response obtain(int cmd, byte[] data, Object attach)
    {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Response m = sPool;
                sPool = m.next;
                m.next = null;
                sPoolSize--;
                m.cmd = cmd;
                m.data = data;
                m.attach = attach;
                return m;
            }
        }
        Response msg = new Response();
        msg.cmd = cmd;
        msg.data = data;
        msg.attach = attach;
        return msg;
    }

    public void recycle() {
        data = null;
        attach = null;
        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    public int getCMD()
    {
        return cmd;
    }

    public byte[] getData()
    {
        return data;
    }

    public Object getAttach()
    {
        return attach;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Response{");
        sb.append("next=").append(next);
        sb.append(", values=").append(values);
        sb.append(", cmd=").append(cmd);
        sb.append(", data=").append(Arrays.toString(data));
        sb.append(", attach=").append(attach);
        sb.append('}');
        return sb.toString();
    }
}
