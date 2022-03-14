package com.example.frame.Retrofit2;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @FormUrlEncoded
    @POST("retrofit.php")
    Call<ResponseModel> callWithNoRequestCode(
            @Field("feed_id") String feed_id
    );
}

