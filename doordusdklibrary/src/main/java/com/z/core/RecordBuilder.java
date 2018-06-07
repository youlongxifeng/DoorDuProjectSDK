package com.z.core;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.android.volley.VolleyLog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.z.core
 * @class describe
 * @time 2018/6/5 16:26
 * @change
 * @class describe
 */


public class RecordBuilder implements Runnable {


    private final SimpleDateFormat mFormat;
    private final StringBuffer mBufer;
    private final byte[] mLock;
//    private final String mPath;

    public final static int LV_DEVICE = 1;
    public final static int LV_BASE = LV_DEVICE << 1;
    public final static int LV_HTTP = LV_BASE << 1;
    public final static int LV_TCP = LV_HTTP << 1;
    public final static int LV_SIP = LV_TCP << 1;
    public final static int LV_VOIP = LV_SIP << 1;
    public final static int LV_SERIAL = LV_VOIP << 1;
    public final static int LV_TOTP = LV_SERIAL << 1;
    public final static int LV_AD = LV_TOTP << 1;

    private JSwitch mLv;

    private RecordBuilder() {
        mFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        mBufer = new StringBuffer();
        mLock = new byte[]{};
//        mPath = path;
        mLv = new JSwitch();
    }

    public RecordBuilder enable(int lv) {
        mLv.add(lv);
        return this;
    }

    public RecordBuilder close(int lv) {
        mLv.add(lv);
        return this;
    }

    public void closeAll() {
        mLv.reset(0);
    }

    public void openAll() {
        for (int i = 1; i <= LV_AD; i <<= 1) {
            mLv.add(i);
        }
    }

    public static File getLast() {
        return getFile(true);
    }

    /**
     *
     * @param pre true 当前天的前一天,数据提交时候使用
     * @return
     */
    private static File getFile(boolean pre) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_YEAR) - (pre ? 1 : 0);
        return new File(Environment.getExternalStorageDirectory(), String.format("record_%d", day % 3));
    }

    public static void device(int type, String des, Object... obj) {
        addRecord(LV_DEVICE, "dev", type, des, obj);
    }

    public static void normal(int type, String des, Object... obj) {
        addRecord(LV_BASE, "info", type, des, obj);
    }

    public static void http(int type, String des, Object... obj) {
        addRecord(LV_HTTP, "http", type, des, obj);
    }

    public static void tcp(int type, String des, Object... obj) {
        addRecord(LV_TCP, "tcp", type, des, obj);
    }

    public static void sip(int type, String des, Object... obj) {
        addRecord(LV_SIP, "sip", type, des, obj);
    }

    public static void voip(int type, String des, Object... obj) {
        addRecord(LV_VOIP, "voip", type, des, obj);
    }

    public static void serial(int type, String des, Object... obj) {
        addRecord(LV_SERIAL, "seri", type, des, obj);
    }

    public static void totp(int type, String des, Object... obj) {
        addRecord(LV_TOTP, "totp", type, des, obj);
    }

    public static void ad(int type, String des, Object... obj) {
        addRecord(LV_AD, "ad", type, des, obj);
    }

    private static void addRecord(int lv, String event, int type, String format, Object... args) {
        try {
            if (null != mBuilder) {
                mBuilder.record(lv, event, type, format, args);
            }
            if (VolleyLog.DEBUG) {
                Log.i("RecordBuilder", String.format(" %4s %02x %s\n", event, type, args == null ? format : String.format(Locale.CHINA, format, args)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void record(int lv, String event, int type, String format, Object... args) {
        if (mLv.has(lv)) {
            synchronized (mLock) {
                try {
                    if(mBufer.length() < 1024 * 512)
                    {
                        mBufer.append(String.format("%s %4s %02x %s\n", mFormat.format(new Date()), event, type, args == null ? format : String.format(Locale.CHINA, format, args)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void flush() {
        mTime = SystemClock.uptimeMillis();
        String str = "";
        synchronized (mLock) {
            str = mBufer.substring(0);
            mBufer.delete(0, mBufer.length());
        }

        File f = getFile(false);
        if (f.length() < 1024 * 1024 * 2) {
            Util.saveString(f, str, true);
        }

    }

    public static void save() {
        if (null != mBuilder) {
            mBuilder.flush();
        }
    }

    public static void updateFile() {
        if (null != mBuilder) {
            mBuilder.flush();
            mBuilder = null;
            instance();
        }
    }

    public static RecordBuilder instance() {
        if (null == mBuilder) {
            mBuilder = new RecordBuilder();
            mBuilder.mBufer.append("\n\n");
        }
        return mBuilder;
    }

    private static RecordBuilder mBuilder;
    private long mTime;

    @Override
    public void run() {
        if (mBufer.length() > 0 && SystemClock.uptimeMillis() - mTime > 150000) {
            flush();
        }
    }
}
