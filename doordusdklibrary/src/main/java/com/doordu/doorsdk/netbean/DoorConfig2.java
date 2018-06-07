package com.doordu.doorsdk.netbean;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.netbean
 * @class describe
 * @time 2018/6/4 20:20
 * @change
 * @class describe
 */

import com.google.mgson.annotations.SerializedName;

/**
 * 服务器返回的 设备配信息，后来服务器接口又变更了
 *
 * @author Administrator
 *
 */
public class DoorConfig2 extends DoorNewConfig {

    @SerializedName("webrtcconfig")
    public WebrtcConfig mConfig;

    @SerializedName("init_code")
    public String       init_code;
    @SerializedName("sys_code")
    public String       sys_code;
    @SerializedName("door_alert")
    public int          door_alert;

    /**
     * =1  已经开通
     */
    @SerializedName("status")
    public int          status;
    @SerializedName("door_alert_interval")
    public int          OpenTime;

    /**
     * m1卡禁用开关 0 关闭 /1 开启（开启此功能则禁用m1卡）
     */
    @SerializedName("disable_m1_card")
    public int          disable_m1_card;
//    @SerializedName("M1CardAntiCopy")
    /**
     * 动态防复制,估计不启用了
     */
//    public int          M1CardAntiCopy;
    @SerializedName("dynamic_token")
    public String totp;

    @SerializedName("dynamic_config")
    public DynamicConfig dynamicConfig;



    /**
     * 蓝牙开门
     */
    @SerializedName("bluetooth_open_door")
    public int bluetooth_open_door;

    @SerializedName("dep_id")
    public int dep_id;

    /**
     *                 "dynamic_config": {
     "push_domain": "xq1.mqtt.doordu.com",
     "heart_time": 30,
     "heart_domain": "xq1.test.swheart.mdoordu.cn"
     "voip_domain": "xq1.test.hj.mdoordu.cn",
     "door_api_domain": "http://xq1.test.door.mdoordu.cn",
     "temp_password":"123456@1513850075",//开门密码@开门密码过期时间戳
     },
     */
    public static class DynamicConfig {
        @SerializedName("heart_time")
        private int heart_time;
        @SerializedName("heart_domain")
        private String heart_domain;
        @SerializedName("voip_domain")
        private String voip_domain;
        @SerializedName("door_api_domain")
        private String door_api_domain;
        @SerializedName("temp_password")
        private String temp_password;

        public int getHeartTime() {
            return heart_time;
        }

        public String getHeartDomain() {
            return heart_domain;
        }

        public String getVoipDomain() {
            return voip_domain;
        }

        public String getDoorApiDomain() {
            return door_api_domain;
        }

        public String getTempPassword() {
            return temp_password;
        }


        /**
         * 人脸识别开关 1 开
         */
        @SerializedName("face_detect")
        public int face_detect;

        /**
         * 人脸识别抓图开关 1 开
         */
        @SerializedName("face_capture")
        public int face_detect_crop_face_image;

        /**
         * 人脸识别开门开关
         */
        @SerializedName("face_open_door")
        public int face_detect_open_door;


        /**
         * 人脸开门相似度
         */
        @SerializedName("face_open_door_similarity")
        public int face_detect_open_door_confidence;

        /**
         * 人脸识别抓图是否抓整图
         */
        @SerializedName("face_detect_debug")
        public int face_detect_crop_face_image_all;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("DynamicConfig{");
            sb.append("heart_time=").append(heart_time);
            sb.append(", heart_domain='").append(heart_domain).append('\'');
            sb.append(", voip_domain='").append(voip_domain).append('\'');
            sb.append(", door_api_domain='").append(door_api_domain).append('\'');
            sb.append(", temp_password='").append(temp_password).append('\'');
            sb.append(", face_detect=").append(face_detect);
            sb.append(", face_detect_crop_face_image=").append(face_detect_crop_face_image);
            sb.append(", face_detect_open_door=").append(face_detect_open_door);
            sb.append(", face_detect_open_door_confidence=").append(face_detect_open_door_confidence);
            sb.append(", face_detect_crop_face_image_all=").append(face_detect_crop_face_image_all);
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DoorConfig2{");
        sb.append("mConfig=").append(mConfig);
        sb.append(", init_code='").append(init_code).append('\'');
        sb.append(", sys_code='").append(sys_code).append('\'');
        sb.append(", door_alert=").append(door_alert);
        sb.append(", status=").append(status);
        sb.append(", OpenTime=").append(OpenTime);
        sb.append(", disable_m1_card=").append(disable_m1_card);
        sb.append(", totp='").append(totp).append('\'');
        sb.append(", dynamicConfig=").append(dynamicConfig);
        sb.append(", bluetooth_open_door=").append(bluetooth_open_door);
        sb.append(", dep_id=").append(dep_id);
        sb.append('}');
        return sb.toString();
    }
}
/**
 {
 "request_id":"",
 "is_valid":"true",
 "cmd":"door_config",
 "response_type":"",
 "response_params":{
 "success":true,
 "message":"获取配置成功",
 "data":[
 {
 "SecocdServerIP":"ok1",
 "SecocdServerPort":"1",
 "OpenTime":"1",
 "openKey":"0",
 "CallProfix":"",
 "CallDuration":"60",
 "sipDomain":"hj.doordu.com",
 "qrcode":"https://doordustorage.oss-cn-shenzhen.aliyuncs.com/app/android/c4471287797d9071630126b4f56da1a3.jpg",
 "qrcode_hint":"扫描二维码，下载客户端",
 "agent_mobile":"01010205",
 "door_name":"DDD4001708-05946",
 "background":"",
 "hint":"提示信息息",
 "device_id":1407785,
 "DeviceName":"DDD4001708-05946",
 "sipNO":"14015368",
 "sipPwd":"55860982",
 "ManageNO":"0205",
 "ManageCallNO":"00019999",
 "ServerNO":"075528303099",
 "SetupNO":"075528303099",
 "OpenKey":"0",
 "mobile":"18797948361",
 "time":1528193134,
 "companyAdsPhone":"",
 "localType":0,
 "webrtcconfig":{
 "sip_server":"hj.doordu.com",
 "tls_port":5061,
 "tcp_port":5060,
 "udp_port":5060,
 "ice":0,
 "coturn_server":null,
 "coturn_port":null,
 "coturn_user":null,
 "coturn_pass":null,
 "rtcp_fb":0,
 "scheme":2
 },
 "init_code":"",
 "sys_code":"",
 "door_alert":0,
 "door_alert_interval":0,
 "status":0,
 "ic_card_copy":0,
 "open_monitor":0,
 "disable_m1_card":0,
 "m1_card_anti_copy":0,
 "face_detect":0,
 "face_detect_debug":0,
 "face_open_door":0,
 "dynamic_token":"",
 "dynamic_config":{

 },
 "dep_id":0,
 "dep_type":0,
 "is_dial":0,
 "bluetooth_open_door":0
 }
 ],
 "data_v2":{

 },
 "totalCount":"0",
 "box":""
 }
 }*/