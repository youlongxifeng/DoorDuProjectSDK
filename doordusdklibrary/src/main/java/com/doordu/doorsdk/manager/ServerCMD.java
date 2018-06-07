package com.doordu.doorsdk.manager;

import android.app.Application;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import com.doordu.doorsdk.common.DeviceInformation;
import com.doordu.doorsdk.net.NetManager;
import com.doordu.doorsdk.net.StringRequest;
import com.doordu.doorsdk.netbean.OpenDoorPwd;
import com.doordu.doorsdk.netbean.RandomPwd;
import com.doordu.doorsdk.netbean.SubGsonClass;
import com.google.mgson.Gson;
import com.google.mgson.reflect.TypeToken;
import com.z.core.RecordBuilder;
import com.z.core.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.manager
 * @class describe
 * @time 2018/6/5 10:34
 * @change
 * @class describe
 */

/**
 * 因为服务器是以命令名称映射方法,所以客户端也遵循次规律反射方法 *
 * @author ww
 */
public class ServerCMD {
    final static transient int TASK_CARDNEW = 1;
    final static transient int TASK_AD_UPDATE = 2;
    final static transient int TASK_CONFIG = 3;
    protected String content, sn;
    private final Application app;
    private NetManager mManager;
    private Gson mGson;
    private long mLastCall;
    private Handler mHandler;


    private final Callback mBack;

    {
        mBack = new Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case TASK_CARDNEW:
                        //  HttpImpleHelper2.getCardInfo(app);
                        break;
                    case TASK_AD_UPDATE:
                        //  DoorDuAd.whenConfigChange();
                        break;
                    case TASK_CONFIG:
                        // app.reGetConfig();
                        break;
                }
                return true;
            }
        };
    }

    public ServerCMD(NetManager mamager, Application ap) {
        mManager = mamager;
        app = ap;
        mGson = new Gson();
        mHandler = new Handler(mBack);
    }

    protected void setNetManager(NetManager manager) {
        this.mManager = manager;
    }

    /**
     * 服务端发送命令，通知客户端进行配置获取
     */
    public void get_config() {
        Util.resend(mHandler, TASK_CONFIG, 5000);
        sendReponse("get_config", true);
    }


    /**
     * 服务端发送命令，通知客户端进行开门
     */
    private long mLastOpen;

    public void open_door() {
        mLastOpen = SystemClock.elapsedRealtime();
        try {
            JSONObject o = new JSONObject(content);
            JSONArray a = o.getJSONArray("data");
            int type = 2/**钥匙包开门*/, floor = 0;
            String roomId = "", attach = "";
            if (a.length() > 0) {
                JSONObject tmp = a.getJSONObject(0);
                roomId = tmp.getString("room_id");
                if (tmp.has("content")) {
                    attach = tmp.getString("content");
                } else if (tmp.has("device_guid")) {
                    attach = "-" + tmp.getString("device_guid");
                }
                if (tmp.has("operate_type")) {
                    type = tmp.getInt("operate_type");
                }
                if (tmp.has("floor")) {
                    floor = tmp.getInt("floor");
                }
                //RequestOpenDoorEvent.post(type, attach, roomId, sn, floor);关键代码发送到前端处理
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendReponse("open_door", String.format("client error %s ,%s", content, e.getLocalizedMessage()), false);
        }
    }

    /**
     * 重置数据
     */
    public void resetData() {
        boolean r = true;
        String message = "";
       /* try {//关键代码
            IUtil.clearData(app, true);
        } catch (Exception e) {
            e.printStackTrace();
            r = false;
            message = e.getMessage();
        }*/
        sendReponse("resetData", message, r);
    }


    /**
     * 发送重启指令
     */
    public void reboot() {
        sendReponse("reboot", true);
      /*  try {
            app.mPoxy.onReboot(false, "后台请求重启");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 固件升级暂时不需要
     */
    public void coreUpdate() {
        /*boolean download = app.getInfo().isDownloading();
        if (download || !app.getPlatform().supportCoreUpdate()) {
            sendReponse("coreUpdate", download ? "正在下载当中" : "当前版本不支持固件升级", false);
            return;
        }
        boolean r = false;
        Type t = new TypeToken<SubGsonClass<AdsBean>>() {
        }.getType();
        SubGsonClass<AdsBean> s = mGson.fromJson(content, t);
        if (s.isSuccess()) {
            final AdsBean bean = s.getData().get(0);
            if (null != bean && !TextUtils.isEmpty(bean.getUrl())) {
                UpdateVersion.startDownloadCore(app, bean.getUrl(), bean.getCheck());
                r = true;
            }
        }
        sendReponse("coreUpdate", r);*/
    }

    /**
     * 发送响应
     *
     * @param cmd
     * @param success
     */
    private void sendReponse(String cmd, boolean success) {
        sendReponse(cmd, null, success);
    }

    /**
     * 发送响应
     *
     * @param cmd
     * @param content
     * @param success
     */
    protected void sendReponse(String cmd, String content, boolean success) {
        if (null != mManager) {
            mManager.addRequest(new StringRequest(builder(cmd, content, sn, success)));
        } else {
            RecordBuilder.tcp(0, "ServerCMD error mManager null");
        }
    }


    /**
     * 删除黑名单
     */
    public void removeBlackName() {
        try {
            JSONObject o = new JSONObject(content);
            JSONArray a = o.getJSONArray("data");
            String str = a.getString(0);
           /* if (!TextUtils.isEmpty(str)) {//关键代码
                SerialCMD cmd = new SerialCMD(SerialCmdConstant.CMD_RM_BLACK_NAME, Base64.encodeToString(SerialManager
                        .fix(str), Base64.DEFAULT));
                app.mPoxy.sendSerialCMD(cmd);
            }*/
        } catch (Exception e) {
            // TODO: handle exception
        }
        sendReponse("removeBlackName", true);
    }

    /**
     * 添加黑名单
     */
    public void addBlackName() {
        try {
            JSONObject o = new JSONObject(content);
            JSONArray a = o.getJSONArray("data");
            String str = a.getString(0);
          /*  if (!TextUtils.isEmpty(str)) {关键代码
                SerialCMD cmd = new SerialCMD(SerialCmdConstant.CMD_BLACK_NAME, Base64.encodeToString(SerialManager
                        .fix(str), Base64.DEFAULT));
                app.mPoxy.sendSerialCMD(cmd);
            }*/
        } catch (Exception e) {
            // TODO: handle exception
        }
        sendReponse("addBlackName", true);
    }

    /**
     * 服务端主动下发生成的随机密码。 服务端发送命令，通知客户端进行配置获取
     */
    public void randomPwd() {
        boolean result = false;
        String message = "";
        try {
            Type t = new TypeToken<SubGsonClass<OpenDoorPwd>>() {
            }.getType();
            SubGsonClass<OpenDoorPwd> d = mGson.fromJson(content, t);
            if (d.isSuccess() && d.hasData()) {
                OpenDoorPwd pwd = d.getData().get(0);
                RandomPwd r = RandomPwd.getInstance(app);
                result = r.add(pwd);
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getLocalizedMessage();
        }

        sendReponse("randomPwd", message, result);
    }

    public void cardNew() {
        Util.resend(mHandler, TASK_CARDNEW, 5000);
        sendReponse("cardNew", true);
    }

    /**
     * 服务端主动下发拨号提示信息
     */
    @Deprecated
    public void Prompt() {
        sendReponse("Prompt", false);
    }


    private static String builder(String cmd) {
        JSONObject o = new JSONObject();
        try {
            o.put("cmd", cmd);
            o.put("guid", DeviceInformation.getInstance().getDeviceID());
            o.put("version", DeviceInformation.getInstance().getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o.toString();
    }

    /**
     * 86 盒 被拆
     *
     * @param boxid
     * @return
     */
    public static String boxBeenDestory(String boxid) {
        JSONObject o = new JSONObject();
        try {
            o.put("cmd", "alert");
            o.put("guid", DeviceInformation.getInstance().getDeviceID());
            o.put("version", DeviceInformation.getInstance().getVersion());
            o.put("box", boxid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o.toString();
    }

    private static String builder(int cmd, String content) {
        JSONObject o = new JSONObject();
        try {
            o.put("cmd", "proxy");
            o.put("success", true);
            o.put("proxy", cmd);
            if (!TextUtils.isEmpty(content)) {
                JSONArray array = new JSONArray();
                array.put(content);
                o.put("data", array);
            }
            // o.put("content", content);
            o.put("guid", DeviceInformation.getInstance().getDeviceID());
            o.put("version", DeviceInformation.getInstance().getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o.toString();
    }

    private static String builer(String cmd, boolean success) {
        return builder(cmd, null, success);
    }

    private static String builder(String cmd, String content, boolean success) {
        return builder(cmd, content, null, success);
    }

    public static String builder(String cmd, String content, String sn, boolean success) {
        JSONObject o = new JSONObject();
        try {
            o.put("cmd", cmd);
            o.put("success", success);
            o.put("guid", DeviceInformation.getInstance().getDeviceID());
            if (!TextUtils.isEmpty(content)) {
                o.put("message", content);
            }
            if (!TextUtils.isEmpty(sn)) {
                o.put("sn", sn);
            }
            o.put("version", DeviceInformation.getInstance().getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o.toString();
    }
}
