package com.doordu.doorsdk.app;


import android.app.Application;

import com.doordu.doorsdk.DoorDuSDK;
import com.doordu.doorsdk.listener.InstructionListener;
import com.doordu.doorsdk.listener.OpenDoorListener;
import com.doordu.doorsdk.listener.PasswordState;
import com.doordu.doorsdk.netbean.CardInfo;
import com.doordu.doorsdk.netbean.Floor;
import com.doordu.doorsdk.netbean.OpenDoorBean;
import com.doordu.doorsdk.netbean.RandomPwd;

import java.util.List;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk
 * @class describe
 * @time 2018/6/5 16:34
 * @change
 * @class describe
 */

public class DoorApplication extends Application implements InstructionListener {
    @Override
    public void onCreate() {
        super.onCreate();
        DoorDuSDK.init(this, "", "", "DDD4001708-05946", this);
    }


    @Override
    public void reBoot() {//重启设备

    }

    @Override
    public OpenDoorBean openDoor(int openDoorType, OpenDoorListener listener) {
        OpenDoorBean doorBean = new OpenDoorBean();
        switch (openDoorType) {
            case OpenDoorListener.TYPE_CARD://刷卡开门
                doorBean.setOpenType(OpenDoorListener.TYPE_CARD);
                break;
            case OpenDoorListener.TYPE_PHONE_OPEN_DOOR://手机开门 2-钥匙包开门
                doorBean.setOpenType(OpenDoorListener.TYPE_PHONE_OPEN_DOOR);
                break;

            case OpenDoorListener.TYPE_CALL_OPEN://被呼叫接通后开门
                doorBean.setOpenType(OpenDoorListener.TYPE_CALL_OPEN);
                break;
            case OpenDoorListener.TYPE_SECRET_OPEN_DOOR://密码开门
                doorBean.setOpenType(OpenDoorListener.TYPE_SECRET_OPEN_DOOR);
                break;
            case OpenDoorListener.TYPE_VIEW_OPEN://主动查看门禁视频开门
                doorBean.setOpenType(OpenDoorListener.TYPE_VIEW_OPEN);
                break;
        }
        return doorBean;
    }

    @Override
    public List<CardInfo<Floor>> getBlackAndWhiteList() {
        //获取卡信息 信息获取后保存导数据，刷卡卡门时用到
        List<CardInfo<Floor>> cardInfos = DoorDuSDK.getCardInfo("DDD4001708-05946", 0, null);

        return cardInfos;
    }

    @Override
    public PasswordState getNetworkCipher(RandomPwd pwd) {//服务器下发的临时密码
        return PasswordState.getPasswordState(PasswordState.SUCCESS);
    }


}
