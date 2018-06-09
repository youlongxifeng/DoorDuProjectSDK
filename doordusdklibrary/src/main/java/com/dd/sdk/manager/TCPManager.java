package com.dd.sdk.manager;

import android.app.Application;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.dd.sdk.common.DeviceInformation;
import com.dd.sdk.net.Decoder;
import com.dd.sdk.net.NetError;
import com.dd.sdk.net.NetManager;
import com.dd.sdk.net.Request;
import com.dd.sdk.net.Response;
import com.dd.sdk.net.ResponseParser;
import com.dd.sdk.net.StringRequest;
import com.dd.sdk.netbean.NetConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.net
 * @class describe
 * @time 2018/6/5 10:15
 * @change
 * @class describe 长连接管理器
 */

public class TCPManager implements ResponseParser {
    private final static String TAG = TCPManager.class.getCanonicalName();
    public final static int CONNECTION = -200;
    private Decoder mDecoder;
    private final Handler mMainHandler;
    private final int mSuc, mFail;
    private final byte[] mLock;
    private NetManager mNet;
    private String mUUID;
    private ServerCMD mServerCMD;
    private final Method[] mMethods;
    private int mCount;
    private int mDropCount;
    private long mInterval;//A时间间隔
    private boolean mDestory;

    public TCPManager(  Application app, Handler handler, int whatSuccess, int whatFail) {
        mLock = new byte[]{};
        mSuc = whatSuccess;
        mFail = whatFail;
        mNet = new NetManager(this);
        mDecoder = new Decoder();
        mServerCMD = new ServerCMD(mNet, app);
        mMethods = ServerCMD.class.getMethods();
        Arrays.sort(mMethods, new Comparator<Method>() {

            @Override
            public int compare(Method lhs, Method rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        mMainHandler = handler;
        mUUID = DeviceInformation.getInstance().getGuid();
    }
    public void start(NetConfig config) {
        if (null == config)
            Log.e("zxj", "TCPManager.start NetConfig= null ");
        else {
            if (null == mNet)
                mNet = new NetManager(this);
            mServerCMD.setNetManager(mNet);
            mNet.start(config.getDomain(), config.getdPort());

        }

    }

    /**
     * 心跳信息
     * @param string
     */
    public void setHeatInfo(String string) {
        if (null != mNet)
            mNet.setHearInfo(string);

    }
    @Override
    public void parser(Response response) {
        if (response.getCMD() == CONNECTION) {
            mMainHandler.obtainMessage(mSuc).sendToTarget();
        } else {
            try {
                final byte[] data = mDecoder.decoder(response.getData());
                String str = new String(data);

                parserJson(str);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.recycle();
    }

    private void parserJson(String str) throws JSONException {
        JSONObject json = new JSONObject(str);
        String methodName = json.getString("cmd");
        String uuid = json.getString("request_id");
        if (null == methodName || TextUtils.isEmpty(methodName.trim())) {
            Log.i(TAG, String.format("invalid method %s", methodName));
            return;
        }
        String sn = "";
        if (json.has("sn")) {
            sn = json.getString("sn");
        }
        Log.i(TAG,String.format("invalid method %s", methodName));
        if (mUUID.equals(uuid)) {
            if ("heart_beat".equals(methodName)) {
                mMainHandler.obtainMessage(mSuc).sendToTarget();
            } else {
                mCount++;
                long t = SystemClock.elapsedRealtime();
                mInterval = t;
                String content = json.getString("response_params");
                Method m = filterWithSort(mMethods, methodName);
                if (null != m) {
                    synchronized (mLock) {
                        mServerCMD.content = content;
                        mServerCMD.sn = sn;
                        try {
                            m.invoke(mServerCMD);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                }
            }
        } else {
            mDropCount++;
            addRequest(new StringRequest(ServerCMD.builder(methodName, "guid 不匹配", sn, false)));

        }
    }

    /**
     * 排序过滤
     *
     * @param mMethods
     * @param name
     * @return
     */
    protected static Method filterWithSort(final Method[] mMethods, String name) {

        int lo = 0;
        int hi = mMethods.length - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int midValCmp = mMethods[mid].getName().compareTo(name);
            if (midValCmp < 0) {
                lo = mid + 1;
            } else if (midValCmp > 0) {
                hi = mid - 1;
            } else {
                return mMethods[mid];
            }
        }

        return null;

    }

    /**
     * 添加请求
     *
     * @param request
     */
    public void addRequest(Request request) {
        if (null != mNet) {
            mNet.addRequest(request);
        }
    }


    @Override
    public void parserError(NetError error) {
        Log.i(TAG,"TCPManager response  error="+error);
    }

    @Override
    public void parser(String string) {
        Log.i(TAG,"TCPManager response  string="+string);
    }

    public void destory() {
        if (null != mNet) {
            mServerCMD.setNetManager(null);
            mNet.quit();
            mNet = null;
        }
        mDestory = true;
    }
}
