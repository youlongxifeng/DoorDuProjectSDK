package com.dd.sdk.netbean;

import com.google.mgson.annotations.SerializedName;

import java.util.List;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.netbean
 * @class describe
 * @time 2018/6/4 19:23
 * @change
 * @class describe
 */

public class BaseGsonClass<T> {
    @SerializedName("request_id")
    public String request_id;
    @SerializedName("response_type")
    public boolean response_type;
    @SerializedName("response_params")
    public SubGsonClass<T> response_params;
    @SerializedName("cmd")
    public String cmd;
    public boolean isSuccess() {
        return null != response_params && response_params.isSuccess();
    }

    public List<T> getData() {
        List<T> r = null;
        if (isSuccess())
            r = response_params.getData();
        return r;
    }

    public String getMessage()
    {
        return null == response_params?"":response_params.getMessage();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseGsonClass{");
        sb.append("request_id='").append(request_id).append('\'');
        sb.append(", response_type=").append(response_type);
        sb.append(", response_params=").append(response_params);
        sb.append(", cmd='").append(cmd).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
