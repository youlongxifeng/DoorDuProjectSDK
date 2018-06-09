package com.dd.sdk.netbean;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.netbean
 * @class describe
 * @time 2018/6/4 20:18
 * @change
 * @class describe
 */

        import com.google.mgson.annotations.SerializedName;
/**
 * 服务器返回的设备信息，这个是新接口返回的，未了兼容旧的所以还是使用之前的版本保存
 * @author Administrator
 *
 */
public class DoorNewConfig {

    /**
     * qrcode door_name qrcode_hint background hint agent_mobile device_id
     * DeviceName sipNO sipPwd ManageNO ManageCallNO ServerNO SetupNO openKey
     */
    @SerializedName("qrcode")
    public String qrcode;
    @SerializedName("door_name")
    public String door_name;
    @SerializedName("qrcode_hint")
    public String qrcode_hint;
    @SerializedName("background")
    public String background;
    @SerializedName("hint")
    public String hint;
    @SerializedName("agent_mobile")
    public String agent_mobile;
    @SerializedName("DeviceName")
    public String DeviceName;
    @SerializedName("sipNO")
    public String sipNO;
    @SerializedName("sipPwd")
    public String sipPwd;
    @SerializedName("sipDomain")
    public String sipDomain;

    @SerializedName("dial_hint")
    public String dial_hint;
    @SerializedName("ManageNO")
    public String ManageNO;
    @SerializedName("ManageCallNO")
    public String ManageCallNO;
    @SerializedName("ServerNO")
    public String ServerNO;
    @SerializedName("SetupNO")
    public String SetupNO;
    @SerializedName("openKey")
    public String openKey;

    @SerializedName("CallDuration")
    public String CallDuration;
    @SerializedName("time")
    public long time;
    @SerializedName("companyAdsPhone")
    public String companyAdsPhone;

    @SerializedName("sipTime")
    public long sipTime;
    @SerializedName("tcpTime")
    public long tcpTime;


    /**
     * ap 名称
     */
    @SerializedName("ssid")
    public String ssid;
    /**
     * ap 密码
     */
    @SerializedName("pwd")
    public String pwd;
    /**
     * ap 通信秘钥
     */
    @SerializedName("secret")
    public String secret;
    @SerializedName("localType")
    public int mWaller; //


}
