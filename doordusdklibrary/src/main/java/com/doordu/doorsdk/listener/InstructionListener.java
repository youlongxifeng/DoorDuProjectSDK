package com.doordu.doorsdk.listener;

import com.doordu.doorsdk.netbean.CardInfo;
import com.doordu.doorsdk.netbean.Floor;
import com.doordu.doorsdk.netbean.OpenDoorBean;
import com.doordu.doorsdk.netbean.RandomPwd;

import java.util.List;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.listener
 * @class describe
 * @time 2018/6/4 17:00
 * @change
 * @class describe
 * 操作指令监听
 */

public interface InstructionListener {
    /**
     * 配置拉取指令  处理配置更新指令，上报给应用层，告知可调用配置更新接口
     */
   // void getConfig( );

    /**
     * 重启指令 处理配置更新指令，上报给应用层，告知可重启
     */
    void reBoot();

    /**
     * 开门指令   处理开门指令，并上报给应用层，等待应用层返回处理结果，回包给ddconnector
     */
    OpenDoorBean openDoor(int openDoorType, OpenDoorListener listener);

    /**
     * 拉取黑白名单指令  处理配置更新指令，上报给应用层，告知可更新本地刷卡数据
     */
    List<CardInfo<Floor>> getBlackAndWhiteList();

    /**
     * 网络密码指令    处理密码指令，并上报给应用层，等待应用层返回接收结果，回包给ddconnector
     */
    PasswordState getNetworkCipher(RandomPwd  pwd);
}
