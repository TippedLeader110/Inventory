package com.itc.inventory.ui;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginInterface {

    @FormUrlEncoded
    @POST("login_users")
    Call<Login> Login(@Field("username") String username, @Field("password") String password);

}
