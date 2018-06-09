package com.dd.sdk.netbean;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.netbean
 * @class describe
 * @time 2018/6/4 20:20
 * @change
 * @class describe
 */

import com.google.mgson.annotations.SerializedName;

public class WebrtcConfig {

    // sip_server hj.doordu.com SIP代理服务器
    // tls_port 5061 TLS端口
    // tcp_port 5060 TCP端口
    // udp_port 5060 UDP端口
    // ice 0 ICE点对点通信
    // coturn_server None ICE服务器域名
    // coturn_port None ICE服务器端口
    // coturn_user None ICE服务器用户名
    // coturn_pass None ICE服务器密码
    // rtcp_fb 1 RTCP关键帧同步
    @SerializedName("sip_server")
    public String sipServer;
    @SerializedName("tls_port")
    private int tlsPort;
    @SerializedName("tcp_port")
    private int tcpPort;
    @SerializedName("udp_port")
    private int udpPort;
    @SerializedName("ice")
    public int ice;
    @SerializedName("coturn_server")
    public String coturnServer;
    @SerializedName("coturn_port")
    public int coturnPort;
    @SerializedName("coturn_user")
    public String coturnUser;
    @SerializedName("coturn_pass")
    public String coturnPass;
    @SerializedName("rtcp_fb")
    public int rtcpfb;
    @SerializedName("scheme")
    private int scheme;

    public final static transient int SCHEME_TLS = 0;
    public final static transient int SCHEME_TCP = 1;
    public final static transient int SCHEME_UDP = 2;

    public int getTranType() {
        return scheme;
    }

    public void setScheme(int scheme)
    {
        this.scheme = scheme;
    }

    public int getPort() {
        int r = tlsPort < 0 ? 5061 : tlsPort;
        switch (scheme) {
            case SCHEME_TCP:
                r = tcpPort < 0 ? 5060 : tcpPort;
                break;
            case SCHEME_UDP:
                r = udpPort < 0 ? 5060 : udpPort;
                break;
        }
        return r;
    }

    public static String getDescript(int scheme) {
        String r = "TLS";
        switch (scheme) {
            case SCHEME_TCP:
                r = "TCP";
                break;
            case SCHEME_UDP:
                r = "UDP";
                break;
        }
        return r;
    }
}
