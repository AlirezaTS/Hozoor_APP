package com.example.hozoor2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/register")
    Call<ApiModel.register> register(@Field("secureCode") int secureCode);

    @FormUrlEncoded
    @POST("/check_in_out")
    Call<ApiModel.checkInOut> check_in_out(@Field("type") boolean type,
                                           @Header("x-access-token") String token);

    @POST("/check_token")
    Call<ApiModel.checkToken> check_token(@Header("x-access-token") String token);

}
