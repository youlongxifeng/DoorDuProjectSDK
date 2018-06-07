package com.doordu.doorsdk.common;

import com.z.core.JSwitch;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.common
 * @class describe
 * @time 2018/6/5 14:28
 * @change
 * @class describe
 */

public class DeviceInformation {
    private final static int SUPPORT_BLUETOOTH = 1;
    private final static int BLUETOOTH_OPEN = SUPPORT_BLUETOOTH <<1;
    /**
     * true 表示连接上tcp服务器
     */
    private final static int TCP_SUPPORT = BLUETOOTH_OPEN << 1;
    private final static int AP_STATUS = TCP_SUPPORT << 1;
    private String mGuid;//设备id
    private String mVersion;//版本号
    private long mSipInterval, mTcpInterval;
    /**
     * 标记某些状态变更时是否需要上报服务器
     */
    private final JSwitch mSwitch,mSupport;
    private final static DeviceInformation mInstance = new DeviceInformation();

    public static DeviceInformation getInstance() {
        return mInstance;
    }
    private DeviceInformation() {

        mSwitch = new JSwitch();
        mSupport = new JSwitch();
    }


    public String getGuid() {
        return mGuid;
    }
    public String getDeviceID(){
        return mGuid;
    }


    public void setGuid(String guid) {
        this.mGuid = guid;
    }

    public String getVersion() {
        /**
         * PackageManager pm = context.getPackageManager();//context为当前Activity上下文
         PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
         version = pi.versionName;
         */
        if(mVersion==null){
            return "5.8.500.0";
        }
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public long getTcpInterval() {
        return getIntervale(true);
    }

    private long getIntervale(boolean tcp) {
        long r = tcp ? mTcpInterval : mSipInterval;
        if (r == 0) {
            r = 30;
        }
        if (r < 3) {
            r = 3;
        }
        return r * 1000;
    }
    public void setSipInterval(long interval) {
        mSipInterval = interval;
    }

    public void setTcpInterval(long interval) {
        mTcpInterval = interval;
    }

    public void setTcpConn(boolean conn)
    {
        changeSupport(conn,TCP_SUPPORT);
    }

    private void changeSupport(boolean support,int what){
        if(support){
            mSupport.add(what);
        }else{
            mSupport.remove(what);
        }
    }
}
