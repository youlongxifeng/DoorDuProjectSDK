package com.dd.sdk.listener;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.listener
 * @class describe
 * @time 2018/6/7 15:07
 * @change
 * @class describe
 */

public enum PasswordState {//失败，成功，密码超时
     FAILURE(0),SUCCESS(1), TIMEOUT(2);

    private int value;

    private PasswordState(int value) {
        this.value = value;
    }

    public static PasswordState getPasswordState(PasswordState nowDay) {
        int stateValue = nowDay.value;

        if (++stateValue == 3) {
            stateValue = 0;
        }

        return getPasswordStateValue(stateValue);
    }

    public static PasswordState getPasswordStateValue(int value) {
        for (PasswordState c : PasswordState.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return null;
    }
}
