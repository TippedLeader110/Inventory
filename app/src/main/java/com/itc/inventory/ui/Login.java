package com.itc.inventory.ui;

import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("status")
    Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
