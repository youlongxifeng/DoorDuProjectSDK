package com.doordu.doorsdk.listener;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.listener
 * @class describe
 * @time 2018/6/7 15:07
 * @change
 * @class describe
 */

public enum PasswordState {
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
