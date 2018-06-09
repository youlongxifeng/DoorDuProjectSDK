package com.dd.sdk.net;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;

import com.dd.sdk.NettyClient;
import com.dd.sdk.common.DeviceInformation;
import com.dd.sdk.common.NetworkState;
import com.dd.sdk.listener.NettyListener;
import com.z.core.RecordBuilder;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.net
 * @class describe
 * @time 2018/6/5 10:19
 * @change
 * @class describe
 */


public class NetManager implements Callback, NetCallback, Encoder {
    private final static String TAG = NetManager.class.getSimpleName();
    private INet mNet;
    private final Handler mWorker;
    private final static int WHAT_START = 1;
    private final static int WHAT_HEAR_JUMP = 2;
    /**
     * 扩展86盒信跳信息
     */
    private final static int WHAT_UPDATE_HEAT_INFO = 3;
    private final PriorityBlockingQueue<Request> mNetworkQueue;
    private final ExecutorDelivery mDelivery;
    private final ResponseReader mReader;
    private final ResponseParser mParser;
    private String mAddress;
    private int mPort;
    private StringRequest mHearJump;
    private NettyClient mNettyClient;
    /**
     * 正常行为为 发一个收一个 但当网线被拔掉后，还是可以发 收不到 所以计数
     */
    private int mCount;

    public NetManager(ResponseParser parser) {
        mParser = parser;
        mNetworkQueue = new PriorityBlockingQueue<Request>();
        HandlerThread ht = new HandlerThread("tcpWorker");
        ht.start();
        mReader = new ResponseReader();
        mWorker = new Handler(ht.getLooper(), this);
        mDelivery = new ExecutorDelivery(mWorker, this);
        /*mNettyClient = new NettyClient("test.swoole.doordu.com", 9501);
        connect();*/

    }

    private void connect() {
        Log.i(TAG, "connect");
        if (!mNettyClient.getConnectStatus()) {
            mNettyClient.setListener(new NettyListener() {
                @Override
                public void onMessageResponse(Object msg) {
                    Log.i(TAG, "onMessageResponse:msg=" + msg);
                }

                @Override
                public void onServiceStatusConnectChanged(int statusCode) {
                    Log.i(TAG, "onMessageResponse:  statusCode=" + statusCode);
                }
            });
            mNettyClient.connect();//连接服务器
        } else {
            mNettyClient.disconnect();
        }
    }

    public void setHearInfo(String content) {
        mWorker.obtainMessage(WHAT_UPDATE_HEAT_INFO, content).sendToTarget();
    }

    public void start(String address, int port) {
        mWorker.removeCallbacksAndMessages(null);
        mAddress = address;
        mPort = port;
        mWorker.sendEmptyMessage(WHAT_START);
    }

    public void addRequest(Request request) {
        request.setEncoder(this);
        mNetworkQueue.add(request);
    }

    public void restart() {
        if (null != mNet) {
            mNet.quit();
            mNet = null;
        }
        mWorker.removeCallbacksAndMessages(null);
        mWorker.sendEmptyMessageDelayed(WHAT_START, 1000);
    }

    public void quit() {
        mDelivery.onDestory();
        if (null != mNet) {
            mNet.quit();
            mNet = null;
        }
        mNetworkQueue.clear();
        mWorker.removeCallbacksAndMessages(null);
        mWorker.getLooper().quit();
    }

    @Override
    public void onResponse(Response response) {
        mCount = 0;
        mParser.parser(response);

    }

    @Override
    public void onError(NetError error) {
        // restart();//FIXME 目前出错了 直接重启
        mParser.parserError(error);
    }
    @Override
    public boolean handleMessage(Message msg) {

        long time = DeviceInformation.getInstance().getTcpInterval();
        Log.i("TAG", "sdk   handleMessage==" + msg);
        switch (msg.what) {
            case WHAT_START:
                mNet = new INet(mAddress, mPort, mNetworkQueue, mDelivery, mReader);
                 mNet.start();
                RecordBuilder.tcp(0, "TCP start");
                mWorker.sendEmptyMessageDelayed(WHAT_HEAR_JUMP, time / 3);
                break;
            case WHAT_HEAR_JUMP://
                if (null == mHearJump) {
                    mHearJump = new StringRequest(String.format(
                            "{\"guid\":\"%s\",\"cmd\":\"heart_beat\",\"version\":\"%s\"}", DeviceInformation.getInstance().getGuid(), DeviceInformation.getInstance()
                                                                                                                                                       .getVersion()));
                    mHearJump.setEncoder(this);
                }
                mCount++;
                Log.i(TAG, "sdk   handleMessage==" + (mCount > 4 && null != mParser) + " mCount=" + mCount+"  mHearJump="+mHearJump);
                if (mCount > 4 && null != mParser) {
                    // 说明已经连续掉了3个包了
                    mParser.parserError(new NetError(NetError.ERROR_MAY_NET_DISCOUNNECT, "服务器没有回包,所以尝试重连"));

                }
                NetworkState.HeatJump = SystemClock.elapsedRealtime();
                mNetworkQueue.add(mHearJump);
                mWorker.sendEmptyMessageDelayed(WHAT_HEAR_JUMP, mCount == 1 ? time : 1500);
                break;
            case WHAT_UPDATE_HEAT_INFO:
                mHearJump = null;
                StringRequest request = new StringRequest((String) msg.obj);
                request.setEncoder(this);
                mHearJump = request;
                break;
        }

        return true;
    }

    @Override
    public byte[] encoder(String content) {
        Log.i(TAG, content);
        return Base64.encode(content.getBytes(), Base64.DEFAULT);
    }

    @Override
    public byte[] encode(byte[] content) {
        return Base64.encode(content, Base64.DEFAULT);
    }

    @Override
    public void onResponse(String str) {
        mCount = 0;
        mParser.parser(str);
    }
}
