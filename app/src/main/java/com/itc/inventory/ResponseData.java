package com.itc.inventory;

import com.google.gson.annotations.SerializedName;

public class ResponseData {

    @SerializedName("code")
    Integer code;

    @SerializedName("msg")
    String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
