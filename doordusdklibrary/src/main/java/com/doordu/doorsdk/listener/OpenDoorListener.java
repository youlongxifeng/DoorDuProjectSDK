package com.doordu.doorsdk.listener;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.listener
 * @class describe
 * @time 2018/6/7 9:29
 * @change
 * @class describe
 */

public interface OpenDoorListener {
    /**
     * 操作类型 1-IC卡开门 2-钥匙包开门 3-被呼叫接通后开门 4-密码开门 5-主动查看门禁视频开门 6-被呼叫未接通时开门 7-没开门（
     * 没接通或接通但没开门）
     */
    /**
     * 刷卡开门 1-IC卡开门
     */
    int TYPE_CARD = 1;
    /**
     * 手机开门 2-钥匙包开门
     */
    int TYPE_PHONE_OPEN_DOOR = 2;
    /**
     * 拨号3-被呼叫接通后开门
     */
    int TYPE_CALL_OPEN = 3;
    /**
     * 密码开门
     */
    int TYPE_SECRET_OPEN_DOOR = 4;

    /**
     * 5-主动查看门禁视频开门
     */
    int TYPE_VIEW_OPEN = 5;
    /**
     * 6-被呼叫未接通时开门
     */
    int TYPE_FAST_OPEN = 6;
    /**
     * 7-没开门（ 没接通或接通但没开门）
     */
    int TYPE_CALL = 7;
    /**
     * 门磁
     */
    int TYPE_LOCK = 10;
    /**
     * 开门类型
     * @param openDoorType
     */

    void onSuccessResponse(String openDoorType);

    void onOpenDoorFail(String openDoorType,String  message);
}
