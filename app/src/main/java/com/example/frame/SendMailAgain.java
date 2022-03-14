package com.example.frame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SendMailAgain extends AppCompatActivity {

    Button btn_ck_email2;
    TextInputEditText et_email_again;
    private ProgressBar loading;
    private boolean validate = false;
    private AlertDialog dialog;
    private TextView btn_mail_again;

    private static String URL_send_mail_again ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/send_mail_again.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_again);
        btn_ck_email2 = findViewById(R.id.btn_ck_email2);
        et_email_again = findViewById(R.id.et_email_again);
        loading = findViewById(R.id.loading);
        btn_mail_again = findViewById(R.id.btn_mail_again);

        //이메일 중복 확인 버튼
        btn_ck_email2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserEmail = et_email_again.getText().toString();
                if (validate) {
                    return; //검증 완료
                }

                if (UserEmail.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SendMailAgain.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(SendMailAgain.this);
                                dialog = builder.setMessage("가입되지 않은 메일입니다.").setNegativeButton("확인", null).create();
                                dialog.show();

                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SendMailAgain.this);
                                dialog = builder.setMessage("가입된 메일 입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                et_email_again.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료
                                btn_ck_email2.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(UserEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SendMailAgain.this);
                queue.add(validateRequest);

            }
        });


        //회원가입하기 버튼을 누르면 Register()함수가 실행됨.
        btn_mail_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_mail_again();
            }
        });

    }

    private void send_mail_again() {
        loading.setVisibility(View.VISIBLE); //로딩 보이게
        btn_mail_again.setVisibility(View.GONE); //버튼 보이지 않게

        //회원가입 액티비티에서 edittext에 적힌 텍스트들을 스트링으로 바꾼다.
        final String email = this.et_email_again.getText().toString().trim();

        //아이디 중복체크 했는지 확인
        if (!validate) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SendMailAgain.this);
            dialog = builder.setMessage("이메일 중복 확인을 해주세요.").setNegativeButton("확인", null).create();
            dialog.show();
            return;
        }

        //한 칸이라도 입력 안했을 경우
        if (email.equals("") ) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SendMailAgain.this);
            dialog = builder.setMessage("메일을 입력해주세요.").setNegativeButton("확인", null).create();
            dialog.show();
            return;
        }



        //volley라이브러리에 포함된 StringRequest = 서버로 부터 문자열 정보 얻기
        //첫 번째 매개변수는 HTTP Method로서 GET이나 POST로 지정됨. 두 번째 매개변수는 서버 URL 정보. 세 번째 매개변수가 결과 콜백. 네 번째 매개변수가 에러 콜백
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_send_mail_again,
                new Response.Listener<String>() { //결과 콜백
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            //제이슨으로 보내진 success 메세지 받음.
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(SendMailAgain.this, "인증 메일 보내기 성공",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SendMailAgain.this, LoginActivity.class); //로그인클래스로 감
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SendMailAgain.this, "메일 다시 보내기 에러" + e.toString(),Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_mail_again.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SendMailAgain.this, "에러" + error.toString(),Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_mail_again.setVisibility(View.VISIBLE);
                    }



                })
        {
            //            @Nullable
//            @org.jetbrains.annotations.Nullable
            @Override //클라이언트 데이터 서버로 전송하기 위해
            protected Map<String, String> getParams() {

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
