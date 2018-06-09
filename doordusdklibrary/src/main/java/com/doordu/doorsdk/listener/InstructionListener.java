package com.doordu.doorsdk.listener;

import com.doordu.doorsdk.netbean.CardInfo;
import com.doordu.doorsdk.netbean.DoorConfig;
import com.doordu.doorsdk.netbean.Floor;
import com.doordu.doorsdk.netbean.RandomPwd;
import com.doordu.doorsdk.netbean.ResultBean;

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
     *  上报应用层，当前设备未绑定，需要绑定设备
     */
    ResultBean noBinding( );
    /**
     * 获取配置信息
     */
    ResultBean getconfig(DoorConfig doorConfig);

    /**
     * 上报配置内容
     */
    //ResultBean postDeviceConfig();


    /**
     * 重启指令 处理配置更新指令，上报给应用层，告知可重启
     */
    ResultBean reBoot();

    /**
     * 开门指令   处理开门指令，并上报给应用层，等待应用层返回处理结果，回包给ddconnector
     */
    ResultBean openDoor(int openDoorType,String describe);



    /**
     * 拉取黑白名单指令  处理配置更新指令，上报给应用层，告知可更新本地刷卡数据
     */
    List<CardInfo<Floor>> getBlackAndWhiteList();

    /**
     * 网络密码指令    处理密码指令，并上报给应用层，等待应用层返回接收结果，回包给ddconnector
     */
    ResultBean getNetworkCipher(RandomPwd  pwd);

    /**
     * token失败重新初始化
     */
    ResultBean tokenFile();
}
