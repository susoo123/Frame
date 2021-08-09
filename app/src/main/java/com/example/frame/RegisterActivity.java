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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText email, pw, name;
    private ProgressBar loading;
    private static String URL_REGIST ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/register.php";
    private TextView  btn_register,tv_policy;
    TextView email2;
    private Button btn_ck_email;
    private AlertDialog dialog;
    private boolean validate = false;



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);//레이아웃과 연결

        email2 = findViewById(R.id.email2);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //String email3 = bundle.getString("email");
        //email2.setText(email3);


        //개인정보처리방침 및 이용약관 링크 걸기
        tv_policy = (TextView)findViewById(R.id.tv_policy);
        tv_policy.setText("회원가입을 하면 Frame의 개인정보처리방침 및 이용약관에 동의하시게 됩니다.");

        Pattern pattern1 = Pattern.compile("개인정보처리방침");
        Pattern pattern2 = Pattern.compile("이용약관");

        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String s) {
                return "";
            }

        };

        Linkify.addLinks(tv_policy, pattern1, "https://susoo123.github.io/myblog.github.io/", null, transformFilter);
        Linkify.addLinks(tv_policy, pattern2, "https://susoo123.github.io/myblog.github.io/service.html", null, transformFilter);



        //레이아웃에 선언한 컨포넌트들 연결
        btn_ck_email = findViewById(R.id.btn_ck_email);//이메일 중복 확인 버튼
        btn_register = findViewById(R.id.btn_register);
        email = findViewById(R.id.email);
        pw = findViewById(R.id.pw);
        name = findViewById(R.id.name);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                email.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료
                                btn_ck_email.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(UserEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);

            }
        });

        //회원가입하기 버튼을 누르면 Register()함수가 실행됨.
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  Register();
            }
        });


    }


    private void Register() {
        loading.setVisibility(View.VISIBLE); //로딩 보이게
        btn_register.setVisibility(View.GONE); //버튼 보이지 않게

        //회원가입 액티비티에서 edittext에 적힌 텍스트들을 스트링으로 바꾼다.
        final String name = this.name.getText().toString().trim(); //trim()은 양쪽의 쓸데 없는 공백을 제거해줌. 가운데 공백은 제거 안됨.
        //final String email = this.email2.getText().toString().trim();//final은 변하지 않았으면 하는 것에 붙인다... Immutable..!
        final String email = this.email.getText().toString().trim();
        final String pw = this.pw.getText().toString().trim();

        //아이디 중복체크 했는지 확인
        if (!validate) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            dialog = builder.setMessage("이메일 중복 확인을 해주세요.").setNegativeButton("확인", null).create();
            dialog.show();
            return;
        }

        //한 칸이라도 입력 안했을 경우
        if (email.equals("") || pw.equals("") || name.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            dialog = builder.setMessage("입력칸을 모두 채워 주세요.").setNegativeButton("확인", null).create();
            dialog.show();
            return;
        }

        //비밀번호 유효성
        if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pw))
        {
            loading.setVisibility(View.GONE); //로딩 보이게
            btn_register.setVisibility(View.VISIBLE); //버튼 보이지 않게
            Toast.makeText(RegisterActivity.this,"비밀번호는 8~20자 이내, 영어,숫자 및 특수문자가 포함되어야합니다..",Toast.LENGTH_SHORT).show();
            dialog.dismiss();
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
                            if(success.equals("1")){
                                Toast.makeText(RegisterActivity.this, "회원가입 성공",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class); //로그인클래스로 감
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "회원가입 에러" + e.toString(),Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_register.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {//에러 콜백
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "에러" + error.toString(),Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_register.setVisibility(View.VISIBLE);

                    }
                })
        {
//            @Nullable
//            @org.jetbrains.annotations.Nullable
            @Override //클라이언트 데이터 서버로 전송하기 위해
            protected Map<String, String> getParams() throws AuthFailureError {

                //서버에 전송할 데이터 맵 객체에 담아 변환.
                Map<String,String> params = new HashMap<>();

                params.put("name",name);
                params.put("email",email);
                params.put("password",pw);

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);    //서버에 요청


    }




}

