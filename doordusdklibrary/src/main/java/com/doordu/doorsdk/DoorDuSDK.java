package com.doordu.doorsdk;

import android.app.Application;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.VolleyBuilder;
import com.doordu.doorsdk.common.DeviceInformation;
import com.doordu.doorsdk.common.NetworkState;
import com.doordu.doorsdk.listener.InstructionListener;
import com.doordu.doorsdk.listener.OpenDoorListener;
import com.doordu.doorsdk.listener.RegisterListener;
import com.doordu.doorsdk.listener.RegisterResponseListener;
import com.doordu.doorsdk.listener.ResponseListener;
import com.doordu.doorsdk.net.NetworkHelp;
import com.doordu.doorsdk.netbean.BaseGsonClass;
import com.doordu.doorsdk.netbean.CardInfo;
import com.doordu.doorsdk.netbean.DoorConfig;
import com.doordu.doorsdk.netbean.DoorConfig2;
import com.doordu.doorsdk.netbean.Floor;

import java.util.List;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk
 * @class describe
 * @time 2018/6/4 16:28
 * @change
 * @class describe
 */

public class DoorDuSDK {
    private final static int TOKEN_REGISTER = 1;
    private final static int TOKEN_GET_CONFIG = 2;
    private static NetworkState mNetworkState;
    private static RequestQueue mNetWorkRequest;
    private static InstructionListener mInstructionListener;

    /**
     * 先写到一块儿，等会儿要分开
     *
     * @param application 应用
     * @param app_id      APP秘钥
     * @param deviceID    设备id
     * @param listener    回调监听
     */
    public static void init(Application application, String app_id, String secret_key, String deviceID, InstructionListener listener) {
        mInstructionListener = listener;
        VolleyBuilder b = new VolleyBuilder(application);
        b.setThreadPoolSize(3);
        mNetWorkRequest = Volley.newRequestQueue(b);
        mNetworkState = new NetworkState(application);

        DeviceInformation.getInstance().setGuid(deviceID);
        NetworkHelp.getRegisterDevice(mNetWorkRequest, "15018500000", "", "", application, new Listener<BaseGsonClass<DoorConfig>>() {
            @Override
            public void onResponse(BaseGsonClass<DoorConfig> response, int token) {
                Log.i("EEE", "init  response==" + response);
                if (response.isSuccess()) {
                    getconfigure();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error, int token) {
                Log.i("EEE", "init error==" + error);
            }
        }, TOKEN_REGISTER);
    }

    /**
     * 检查注册
     */
    public static void checkRegister(final String deviceID) {
        NetworkHelp.checkRegister(new RegisterListener() {
            @Override
            public void alreadyRegistered() {//已经注册

            }

            @Override
            public void unregistered() {//尚未注册
                equipmentRegistration(deviceID);
            }
        });
    }

    /**
     * 注册设备
     */
    public static void equipmentRegistration(String deviceID) {
        NetworkHelp.equipmentRegistration(new RegisterResponseListener() {
            @Override
            public void responseSuccess() {

            }

            @Override
            public void responseFail() {

            }
        });
    }

    /**
     * 拉取配置信息
     */
    public static void getconfigure() {
        NetworkHelp.getConfig(mNetWorkRequest, new Listener<BaseGsonClass<DoorConfig2>>() {
                    @Override
                    public void onResponse(BaseGsonClass<DoorConfig2> response, int token) {
                        Log.i("EEE", "getConfig  response==" + response);
                        if (response != null && response.isSuccess()) {
                            List<DoorConfig2> doorConfig2s = response.getData();
                            new Thread(mNetworkState).start();
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error, int token) {
                        Log.i("EEE", "getConfig error==" + error);
                    }
                }, TOKEN_GET_CONFIG);

    }

    /**
     * 获取网络操作指令
     */
    public static DoorDuSDK getServerInstruction(InstructionListener instructionListener) {
        DoorDuSDK doorDuSDK = new DoorDuSDK();
        return doorDuSDK;
    }

    /**
     * 释放相关资源
     */
    public static void release(Application application) {
        if (null != mNetworkState) {
            mNetworkState.release();
            mNetworkState = null;
        }
    }

    /**
     * 拉取黑白名单
     *
     * @param
     * @param door_guid
     * @param curid
     * @param listener
     */
    public static List<CardInfo<Floor>> getCardInfo(String door_guid, int curid, ResponseListener<BaseGsonClass<CardInfo<Floor>>> listener) {
        return null;
    }

    /**
     * 上报配置信息
     *
     * @param size_wxh 显示屏分辨率	1920x1032 屏幕宽 x 高
     * @param modeType 设备类型
     * @param netType  网络类型
     */
    public static void postDeviceConfig(String size_wxh, String modeType, String netType) {

    }

    /**
     * 获取配置信息
     *
     * @param app
     * @param listener
     */
    public static void getConfig(Application app, ResponseListener<BaseGsonClass<DoorConfig2>> listener) {


    }

    /**
     * @param fileType     文件类型 Constant.VIDEO_TYPE Constant.PICTURE_TYPE，
     * @param door_guid    门禁机 GUID
     * @param device_guid  开门设备 GUID
     * @param device_type  必选，设备类型，枚举值
     * @param operate_type 必选，操作类型
     * @param video_time   必选，视频时间
     * @param inside       必选，开门内外的区别
     * @param objectkey    文件名称
     * @param room_id      必选，房号
     * @param box          必选，86 盒子
     * @param fileAddress  必选  文件地址
     * @return 上传访客的视频和拍照文件
     */
    public static boolean uploadVideoOrPicture(String fileType, String door_guid, String device_guid, int device_type, int operate_type, long video_time, int inside, String objectkey, String room_id, String box, String fileAddress) {


        return false;
    }



/*    *//**
     * 请求开门
     * @param openDoorType 开门类型
     * @param listener  开门结果回调
     *//*
    public static void openDoor(OpenDoorType openDoorType , OpenDoorResultListener listener){

    }

    */

    /**
     * 密码开门
     *
     * @param password 开门密码
     * @param listener 开门状态回调
     */
    public static void pWOpenDoor(int password, OpenDoorListener listener) {

    }

}
