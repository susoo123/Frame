package com.example.frame;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

//import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;

import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetPWActivity extends AppCompatActivity {
    private TextInputEditText email;
    private ProgressBar loading;
    private static String URL_REGIST ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/forget.php";
    private TextView  btn_sendPWEmail;
    private Button btn_ck_email;
    private AlertDialog dialog;
    private boolean validate = false;



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foget_password);//레이아웃과 연결


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //String email3 = bundle.getString("email");
        //email2.setText(email3);



        //레이아웃에 선언한 컨포넌트들 연결
        btn_ck_email = findViewById(R.id.btn_ck_email);//이메일 중복 확인 버튼
        btn_sendPWEmail = findViewById(R.id.btn_sendPWEmail);
        email = findViewById(R.id.email);
        loading = findViewById(R.id.loading);


        //이메일 중복 확인 버튼
        btn_ck_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserEmail = email.getText().toString();
                if (validate) {

                    return; //검증 완료
                }

                if (UserEmail.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPWActivity.this);
                    dialog = builder.setMessage("이메일을 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPWActivity.this);
                                dialog = builder.setMessage("가입되지 않은 이메일입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPWActivity.this);
                                dialog = builder.setMessage("가입한 이메일입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                                email.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료
                                btn_ck_email.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                ValidateRequest validateRequest = new ValidateRequest(UserEmail, responseListener);

                RequestQueue queue = Volley.newRequestQueue(ForgetPWActivity.this);
                queue.add(validateRequest);

            }
        });

        //이메일인증하기 버튼을 누르면 함수가 실행됨.
        btn_sendPWEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPWEamil();
            }
        });


    }


    private void sendPWEamil() {
        loading.setVisibility(View.VISIBLE); //로딩 보이게
        btn_sendPWEmail.setVisibility(View.GONE); //버튼 보이지 않게

        //비밀번호 변경 액티비티에서 edittext에 적힌 텍스트들을 스트링으로 바꾼다.
        final String email = this.email.getText().toString().trim();

        Intent intent1 = new Intent(ForgetPWActivity.this,ResetPasswordActivity.class);
        intent1.putExtra("email",email);
        Log.d("soo1","인텐트로 넘기는 email : "+email);
        startActivity(intent1);

        //아이디 중복체크 했는지 확인
        if (!validate) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPWActivity.this);
            dialog = builder.setMessage("가입한 이메일인지 확인을 해주세요.").setNegativeButton("확인", null).create();
            dialog.show();
            loading.setVisibility(View.GONE); //로딩 보이게
            btn_sendPWEmail.setVisibility(View.VISIBLE); //버튼 보이지 않게
            return;
        }

        //한 칸이라도 입력 안했을 경우
        if (email.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPWActivity.this);
            dialog = builder.setMessage("입력칸을 모두 채워 주세요.").setNegativeButton("확인", null).create();
            dialog.show();
            return;
        }



        //volley라이브러리에 포함된 StringRequest = 서버로 부터 문자열 정보 얻기
        //첫 번째 매개변수는 HTTP Method로서 GET이나 POST로 지정됨. 두 번째 매개변수는 서버 URL 정보. 세 번째 매개변수가 결과 콜백. 네 번째 매개변수가 에러 콜백
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() { //결과 콜백
                    @Override
                    public void onResponse(String response) {
                        try{

                           JSONObject jsonObject = new JSONObject(response);
                            //제이슨으로 보내진 success 메세지 받음.
                            String success = jsonObject.getString("success");
                            String pwAuthCode = jsonObject.getString("authCode");
                            if(success.equals("1")){
                                Toast.makeText(ForgetPWActivity.this, "이메일 보내기 성공",Toast.LENGTH_SHORT).show();
                                Log.d("soo","비밀번호 인증코드 확인: "+pwAuthCode);
                                Intent intent = new Intent(ForgetPWActivity.this, ResetPasswordActivity.class); //로그인클래스로 감
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ForgetPWActivity.this, "비밀번호 변경 이메일 인증 에러" + e.toString(),Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_sendPWEmail.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(ForgetPWActivity.this, "비밀번호 변경 이메일 인증 에러" + error.toString(),Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_sendPWEmail.setVisibility(View.VISIBLE);

                    }
                })
        {

            @Override //클라이언트 데이터 서버로 전송하기 위해
            protected Map<String, String> getParams() throws AuthFailureError {

                //서버에 전송할 데이터 맵 객체에 담아 변환.
                Map<String,String> params = new HashMap<>();

                params.put("email",email);

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);    //서버에 요청


    }




}

