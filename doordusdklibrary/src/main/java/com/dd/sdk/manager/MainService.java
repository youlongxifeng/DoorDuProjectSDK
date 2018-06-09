package com.dd.sdk.manager;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dd.sdk.common.DeviceInformation;
import com.dd.sdk.netbean.NetConfig;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.manager
 * @class describe
 * @time 2018/6/5 11:49
 * @change
 * @class describe
 */

public class MainService extends Service implements Callback {
    private final static String TAG=MainService.class.getSimpleName();
    protected final static int CMD_TCP_ERROR            = -1;
    protected final static int CMD_RECEIVER_SERIAL_DATA = 0;
    protected final static int CMD_RECEIVER_TCP         = 1;
    protected final static int CMD_TCP_SUCCESS          = -2;
    private final static int   CMD_START_TCP            = -3;

    private Handler mMainHandler;
    private TCPManager         mTCPManager; 
    private boolean      mCreate,mRegister,mDestroy;
    private int     mCount;
    private boolean mTcpStatus;
    private long mDisConnTime;
    public MainService() {
        mMainHandler = new Handler(this);
        mCreate = true;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initTcpManager();
        Log.i(TAG,TAG+"   ====onCreate===");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (null != intent) {
            boolean datab=intent.getBooleanExtra("data", false);
            if (datab) {
                realseTcp();
                initTcpManager();
            }
            int what = intent.getIntExtra("what", 0);
            Log.i(TAG,TAG+"   ====onStartCommand===datab="+datab+"   what="+what);

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,TAG+"   ====onDestroy===" );
        if (null != mTCPManager) {
            mTCPManager.destory();
            mTCPManager = null;
        }

    }



    private void initTcpManager() {
        if (null == mTCPManager) {
            mTcpStatus = false;
            final Application app =  getApplication();
            mTCPManager = new TCPManager( app, mMainHandler, CMD_TCP_SUCCESS, CMD_TCP_ERROR);
            mTCPManager.start(NetConfig.getAddress());//默认正式地址
        }
    }

    /**
     * 重新释放
     */
    private void realseTcp() {
        if (null != mTCPManager) {
            mTCPManager.destory();
            mTCPManager = null;
        }
    }

    private void stopBind() {

    }

    private void bind() {
        stopBind();

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CMD_TCP_ERROR: {
                if (mTcpStatus) {
                    mTcpStatus = false;
                    DeviceInformation.getInstance().setTcpConn(false);
                  /*  NoticeEvent.post(onTCPNotice.NOTICE_TYPE_TCP_CONNECTION,null);
                    app.setSipNet(false); */
                }
                mCount++;
                realseTcp();
                if(mMainHandler.hasMessages(CMD_START_TCP))
                    mMainHandler.removeMessages(CMD_START_TCP);
                mMainHandler.sendEmptyMessageDelayed(CMD_START_TCP, mCount * 2000);
                if(mDisConnTime == 0)
                    mDisConnTime = SystemClock.elapsedRealtime();
                else if(SystemClock.elapsedRealtime() - mDisConnTime  > 1800000)
                {
                    mDisConnTime = 0;
                 /*   app.mPoxy.onReboot(true, "tcp disconnect more 30 min");*/
                }
                long interval = SystemClock.elapsedRealtime() - mDisConnTime;

            }
            break;
            case CMD_TCP_SUCCESS:
                {
                mDisConnTime = 0;
                if (!mTcpStatus) {
                    mTcpStatus = true;
                    mCount = 0;

                    DeviceInformation.getInstance().setTcpConn(true);
                  /*  app.setSipNet(true);
                    mMainHandler.sendEmptyMessageDelayed(CMD_DELAY_NOTICE_CONNECT,3000);
                    app.connectChange();
                    NoticeEvent.post(onTCPNotice.NOTICE_TYPE_TCP_CONNECTION,null);*/
                }
                Log.i(TAG,"数据连接成功  CMD_TCP_SUCCESS");
            }
            break;
            case CMD_START_TCP:
                if(mMainHandler.hasMessages(CMD_START_TCP))
                    mMainHandler.removeMessages(CMD_START_TCP);
                if (null == mTCPManager)
                {
                    initTcpManager();
                }
                break;
            default:
                //NoticeEvent.post(msg.what, msg.obj);
                break;
        }
        return true;
    }


}
