package com.z.core;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyLog;
import com.google.mgson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.z.core
 * @class describe
 * @time 2018/6/5 16:23
 * @change
 * @class describe
 */

public class Util {

    public static void json(String tag, byte[] data) {
        if (VolleyLog.DEBUG)
            json(tag, new String(data));
    }

    public static void json(String tag, String msg) {
        // if (LOG_SWITCH) {
        if (VolleyLog.DEBUG) {
            if (TextUtils.isEmpty(msg)) {
                Log.e(tag, "msg is null.");
                return;
            }
            try {
                if (msg.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(msg);
                    String message = jsonObject.toString(4);
                    String[] lines = message.split(System.getProperty("line.separator"));
                    Log.d(tag,
                            "┌───────────────────────────────────────────────────────────────────────────────────────");
                    for (String line : lines) {
                        Log.d(tag, "│" + " " + line);
                    }
                    Log.d(tag,
                            "└───────────────────────────────────────────────────────────────────────────────────────");
                    return;
                }
                if (msg.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(msg);
                    String message = jsonArray.toString(4);
                    String[] lines = message.split(System.getProperty("line.separator"));
                    Log.d(tag,
                            "┌───────────────────────────────────────────────────────────────────────────────────────");
                    for (String line : lines) {
                        Log.d(tag, "│" + " " + line);
                    }
                    Log.d(tag,
                            "└───────────────────────────────────────────────────────────────────────────────────────");
                }
            } catch (JSONException e) {
                Log.e(tag, e.getCause().getMessage() + "\n" + msg);
            }
        }
    }

    public static boolean saveString(File f, Object o) {
        return saveObject(f, o, false);
    }

    public static boolean saveObject(File f, Object o, boolean append) {
        String str = new Gson().toJson(o);
        return saveString(f, str, append);
    }
    @TargetApi(VERSION_CODES.KITKAT)
    public static boolean saveString(File f, String str, boolean append) {
        if (null != str)
            return saveByte(f, str.getBytes(), append);
        return Boolean.FALSE;
    }

    @RequiresApi(api = VERSION_CODES.KITKAT)
    public static boolean saveByte(File f, byte[] str, boolean append) {
        boolean r = Boolean.FALSE;
        try (FileOutputStream fos = new FileOutputStream(f, append)) {
            fos.write(str);
            fos.flush();
            fos.getFD().sync();
            r = Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
        }/*
          * finally { if (null != fos) try { fos.close(); fos = null; } catch (Exception e2) {} }
          */
        return r;
    }

    /**
     * 从列表中查找是否包含某个对象的数据
     *
     * @param list
     * @param o
     * @return
     */
    public static <E> E filter(final List<E> list, final Object o) {
        if (null == list || list.isEmpty() || null == o)
            return null;
        E r = null;
        for (E e : list)
            if (e.equals(o)) {
                r = e;
                break;
            }
        return r;
    }
    public static void resend(Handler h, int what, int delay) {
        if(null != h) {
            if(h.hasMessages(what)) {
                h.removeMessages(what);
            }

            h.sendEmptyMessageDelayed(what, (long)delay);
        }

    }
}
