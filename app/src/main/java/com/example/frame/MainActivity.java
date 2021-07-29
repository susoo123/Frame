package com.example.frame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class MainActivity extends AppCompatActivity {

    private String StrProfileImg, StrEmail;
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //카카오 로그인 데이터 받기
        Intent intent= getIntent();
        StrProfileImg = intent.getStringExtra("profileImg");
        StrEmail = intent.getStringExtra("email");

        TextView tv_email = findViewById(R.id.tv_email);
        ImageView iv_profileimg = findViewById(R.id.iv_profileimg);
        btn_logout = findViewById(R.id.btn_logout);

        Log.d("soo","이미지 경로 받은 값 : "+StrProfileImg);

        tv_email.setText(StrEmail);

        //프로필 이미지 사진 set
        //Glide.with(this).load(StrProfileImg).into(iv_profileimg);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });



            }
        });

    }
}