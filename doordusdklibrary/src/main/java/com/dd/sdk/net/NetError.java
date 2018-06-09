package com.dd.sdk.net;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.net
 * @class describe
 * @time 2018/6/5 10:14
 * @change
 * @class describe 网络失败
 */

@SuppressWarnings("serial")
public class NetError extends Exception {

    public final static int ERROR_CONNECTION = 1;
    public final static int ERROR_WRITE = 2;
    public final static int ERROR_READ = 3;
    public final static int ERROR_END = 4;
    /**
     *
     */
    public final static int ERROR_MAY_NET_DISCOUNNECT = 5;
    public final int mWhat;
    public NetError(int what,String message)
    {
        super(message);
        mWhat = what;
    }

    public static boolean isDisconnection(NetError e)
    {
        return e.mWhat == ERROR_END || e.mWhat == ERROR_MAY_NET_DISCOUNNECT;
    }

    public NetError(int what,Throwable able)
    {
        super(able);
        mWhat = what;
    }

    public int getWhat(){
        return mWhat;
    }

    public NetError(String message)
    {
        super(message);
        mWhat = 0;
    }

    public static NetError connectionError(Throwable able)
    {
        return new NetError(ERROR_CONNECTION, able);
    }

    public static NetError readError(Throwable able)
    {
        return new NetError(ERROR_READ, able);
    }

    public static NetError writeError(Request r,Throwable able)
    {
        return new NetError(ERROR_WRITE, able);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NetError{");
        sb.append("mWhat=").append(mWhat);
        sb.append('}');
        return sb.toString();
    }
}
