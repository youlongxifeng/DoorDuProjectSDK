package com.doordu.doorsdk.common;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

import com.doordu.doorsdk.manager.MainService;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.common
 * @class describe
 * @time 2018/6/5 14:41
 * @change
 * @class describe
 */

public class NetworkState implements Runnable{
    public final static int NETWORK_UNKNOW = 0;
    public final static int NETWORK_ETH0 = 1;
    public final static int NETWORK_4G = 2;
    private int mNetState;
    private int mLevel;
    private long time;
    private static long WriteTime;
    private static long HertReceiver;
    /**
     * 最后一次心跳更新的时间
     */
    public static long HeatJump;
    /**
     * 没有ip的时长
     */
    private int mDiscount;
    /**
     * sip 连接成功
     */
    public static long SipConnTime = -1;

    private boolean mConntect=true;
    private final Application mContext;

    public NetworkState(Application context) {
        mContext = context;
        mNetState = NETWORK_UNKNOW;
        SipConnTime = SystemClock.elapsedRealtime();
        mDiscount = 1;
    }

    @Override
    public void run() {
        time = SystemClock.elapsedRealtime();
        checkTcp();
    }




    public static boolean connection(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return false;
    }



    /**
     * 检测tcp状态
     */
    private void checkTcp() {
        // tcp 写入或者收入实际超过2分半钟,则重启tcp
        if ((time - WriteTime) > 150000 || (time - HertReceiver) > 150000) {
            mConntect=connection(mContext);
            if (mConntect) {
                Intent intent = new Intent(mContext, MainService.class);
                intent.putExtra("data", true);
                mContext.startService(intent);
            }
            WriteTime = time;
            HeatJump = time;
            Log.i("TAG","time=="+time);
        }
    }


    /**
     * 释放资源
     */
    public void release() {
        Intent intent = new Intent(mContext, MainService.class);
        intent.putExtra("data", true);
        mContext.stopService(intent);
    }
}
