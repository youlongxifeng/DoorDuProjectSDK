package com.dd.sdk.netbean;

import com.google.mgson.annotations.SerializedName;

import java.util.List;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.netbean
 * @class describe
 * @time 2018/6/4 19:24
 * @change
 * @class describe
 */

public class SubGsonClass<T> {

    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<T> data;
    @SerializedName("totalCount")
    private String totalCount;

    public SubGsonClass() {
    }

    public SubGsonClass(List<T> d) {
        data = d;
        success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<T> getData() {
        return data;
    }

    public boolean hasData() {
        return null != data && data.size() > 0;
    }

    public int getCount() {
        int r = 0;
        try {
            r = Integer.parseInt(totalCount);
        } catch (Exception e) {
        }
        return r;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SubGsonClass{");
        sb.append("success=").append(success);
        sb.append(", message='").append(message).append('\'');
        sb.append(", data=").append(data);
        sb.append(", totalCount='").append(totalCount).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
