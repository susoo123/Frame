package com.example.frame.Retrofit2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frame.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RetrofitActivity extends AppCompatActivity {

    TextView retrofit_result;
    Button retrofit_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrofit_activity);

        retrofit_result = (TextView) findViewById(R.id.retrofit_result);
        retrofit_request = (Button) findViewById(R.id.retrofit_request);
        retrofit_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = RetrofitClient.getClient();
                RetrofitInterface api = retrofit.create(RetrofitInterface.class);
                api.callWithNoRequestCode("137").enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            // 서버로부터 전달받은 데이터
                            ResponseModel responseResult = response.body();
                            Log.e("RetrofitActivity 확인 : ", responseResult.toString());
                            retrofit_result.setText(responseResult.getTitle());

                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                        Log.e("오류", "오류뜸");
                        t.printStackTrace();
                    }
                });


            }
        });
    }
}
