package com.doordu.doorsdk.net;

import android.app.Application;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.GsonTypeRequest;
import com.android.volley.toolbox.Volley;
import com.doordu.doorsdk.BuildConfig;
import com.doordu.doorsdk.DoorDuSDK;
import com.doordu.doorsdk.bean.AccessToken;
import com.doordu.doorsdk.bean.BaseResponse;
import com.doordu.doorsdk.common.DeviceInformation;
import com.doordu.doorsdk.listener.RegisterListener;
import com.doordu.doorsdk.listener.RegisterResponseListener;
import com.doordu.doorsdk.netbean.BaseGsonClass;
import com.doordu.doorsdk.netbean.DoorConfig;
import com.doordu.doorsdk.netbean.DoorConfig2;
import com.doordu.doorsdk.netbean.NetConfig;
import com.doordu.doorsdk.tools.Sign;
import com.google.mgson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.net
 * @class describe
 * @time 2018/6/4 16:39
 * @change
 * @class describe.
 * 网络请求帮助类
 */

public class NetworkHelp {
    final static char se = 0x3d; // "=";
    final static char sc = 0x26; // "&";
    private static NetworkHelp sNetworkHelp = new NetworkHelp();


    public static NetworkHelp getInstance() {
        synchronized (NetworkHelp.class) {
            if (sNetworkHelp == null) {
                sNetworkHelp = new NetworkHelp();
            }
        }
        return sNetworkHelp;
    }




    /**
     * 获取接口访问凭证
     * 访问接口时需调用此接口获取访问凭证token。为避免额外消耗，请访问者在本地保存返回的token值
     *
     * @param appid  授权应用ID
     * @param secret 授权密码
     */
    public static void getAccessToken(String appid, String secret, Listener<BaseResponse<AccessToken>> listener, ErrorListener errlistener) {
        StringBuilder builder = new StringBuilder();
        NetConfig config = NetConfig.getAddress();//获取正式地址
        builder.append("appid").append(se).append(appid).append(sc);
        builder.append("secret").append(se).append(secret).append(sc);
        Type t = new TypeToken<BaseResponse<AccessToken>>() {
        }.getType();
        GsonTypeRequest<BaseResponse<AccessToken>> r = GsonTypeRequest.newRequest(config.getHttpUrl()
                + builder.toString(), t, listener, errlistener);
        r.setRetryPolicy(Volley.getDefaultRetryPolicy(5000, 3, 2f));
        r.setShouldCache(true);
        DoorDuSDK.addRequestQueue(r);
    }


    public static void checkRegister(RegisterListener registerListener) {

    }

    /**
     * 注册设备检测
     * @param registerResponseListener
     */
    /**
     * 注册设备 http://114.119.7.101/doordu/api/index.php/device/DeviceReg?device_guid
     * =1866e0df795fb7e&device_type=1&mobile_no=13410309446&v=3.0.6
     *
     * @param mNetWorkRequest
     * @param telNum
     */
    public static void getRegisterDevice(RequestQueue mNetWorkRequest, final String telNum, final String appNo, final String mId,
                                         final Application app, Listener<BaseGsonClass<DoorConfig>> listener, ErrorListener errlistener,
                                         String token) {

        StringBuilder builder = new StringBuilder();
        NetConfig config = NetConfig.getAddress();//获取正式地址
        builder.append("/device/DeviceReg?");
        builder.append("device_guid").append(se).append(DeviceInformation.getInstance().getGuid()).append(sc);
        final DisplayMetrics res = app.getResources().getDisplayMetrics();
        builder.append("width").append(se).append(res.widthPixels).append(sc);
        builder.append("height").append(se).append(res.heightPixels).append(sc);
        builder.append("cpu_mode").append(se).append(Build.DEVICE).append(sc);
        builder.append("mobile_no").append(se).append(telNum).append(sc);
        if (!TextUtils.isEmpty(appNo)) {
            builder.append("sdk_app_id").append(se).append(appNo).append(sc);
        }
        builder.append("apikey").append(se).append("").append(sc);
        builder.append("mac_addr").append(se).append(mId).append(sc);
        builder.append("expreg").append(se).append(appNo).append(sc);
        // reqeust(app, listener, builder, errlistener, token);
        builder.append("timetoken=").append(/*app.getTime()*/3).append(sc);
        builderGeneral(builder);
        Sign.sign(builder);
        Type t = new TypeToken<BaseGsonClass<DoorConfig>>() {
        }.getType();
        GsonTypeRequest<BaseGsonClass<DoorConfig>> r = GsonTypeRequest.newRequest(config.getHttpUrl()
                + builder.toString(), t, listener, errlistener);
        Log.i("EEE", "getHttpUrl==" + config.getHttpUrl() + "  builder=" + builder.toString());
        r.setRetryPolicy(Volley.getDefaultRetryPolicy(5000, 3, 2f));
        r.setToken(token);
        r.setShouldCache(false);
        DoorDuSDK.addRequestQueue(r);

    }

    private final static void builderGeneral(StringBuilder builder) {
        builder.append("device_type").append(se).append("1"/*Device.deviceType*/).append(sc);
        builder.append("sign").append(se).append("").append(sc);
        builder.append("expires").append(se).append("").append(sc);
        builder.append("v").append(se).append(BuildConfig.VERSION_NAME).append(sc);
        builder.append("vcode").append(se).append(BuildConfig.VERSION_CODE);

    }

    public static void equipmentRegistration(RegisterResponseListener registerResponseListener) {

    }

    ;

    public static void getconfigure() {
    }

    /**
     * 程序启动时获取新配置
     * https://door.api.doordu.com/api/index.php/door/config?guid=DDD4001708-05946&version=1.0&timetoken=3&vcode=1&signkey=886595bc32b5c0df21cc0df75cb2492ec514050b     type=com.doordu.doorsdk.netbean.BaseGsonClass<com.doordu.doorsdk.netbean.DoorConfig2>
     */
    public static void getConfig(String guid, String door_ver,Listener<BaseGsonClass<DoorConfig2>> listener,
                                 ErrorListener error, String token) {
        StringBuilder builder = new StringBuilder();
        NetConfig config = NetConfig.getAddress();
        builder.append("/v1/config/?");
        builder.append("guid").append(se).append(guid).append(sc);
        builder.append("door_ver").append(se).append(door_ver).append(sc);
        builder.append("token=").append(token);
        Sign.sign(builder);
        Type t = new TypeToken<BaseGsonClass<DoorConfig2>>() {
        }.getType();
        GsonTypeRequest<BaseGsonClass<DoorConfig2>> r = GsonTypeRequest.newRequest(config.getHttpUrl()
                + builder.toString(), t, listener, error);
        r.setRetryPolicy(Volley.getDefaultRetryPolicy(5000, 3, 2f));
        r.setToken(token);
        r.setShouldCache(false);
        DoorDuSDK.addRequestQueue(r);
    }
}
