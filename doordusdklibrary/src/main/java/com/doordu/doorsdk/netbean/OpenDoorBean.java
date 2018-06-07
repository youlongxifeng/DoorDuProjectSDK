package com.doordu.doorsdk.netbean;

import java.io.Serializable;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.netbean
 * @class describe
 * @time 2018/6/7 14:23
 * @change
 * @class describe
 */

public class OpenDoorBean implements Serializable {
    /**
     * 开门类型
     */
    private int mOpenType;
    /**
     * 开门是否成功
     */
    private String iSopenDoor;
    /**
     *
     */
    private String mDescribe;

    public int getOpenType() {
        return mOpenType;
    }

    public void setOpenType(int openType) {
        mOpenType = openType;
    }

    public String getiSopenDoor() {
        return iSopenDoor;
    }

    public void setiSopenDoor(String iSopenDoor) {
        this.iSopenDoor = iSopenDoor;
    }

    public String getDescribe() {
        return mDescribe;
    }

    public void setDescribe(String describe) {
        mDescribe = describe;
    }
}
