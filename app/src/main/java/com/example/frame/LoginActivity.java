package com.example.frame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.example.frame.etc.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class LoginActivity extends AppCompatActivity {
    private TextInputEditText email, password;
    private  TextView btn_login, btn_findingIdPw, btn_register, btn_send_mail_again;
    private ProgressBar loading;
    private static String URL_Login ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/login.php";
    private static String URL_email ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/register.php";
    private AlertDialog dialog;
    SessionManager sessionManager;
    //private LinearLayout btn_send_mail_again;


    //????????? ????????? ??????
    private ISessionCallback mSessionCallback;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getHashKey();

        sessionManager = new SessionManager(this);

        btn_send_mail_again = findViewById(R.id.check_mail_again);

        //????????? ????????? ??????
        mSessionCallback = new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                //????????? ??????
                UserManagement.getInstance().me(new MeV2ResponseCallback() {

                    //ctrl+o ????????? ?????????.
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        //????????? ??????
                        Toast.makeText(LoginActivity.this, "????????? ??? ????????? ??????????????????.(Login Failed)", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        //????????? ?????? 
                        Toast.makeText(LoginActivity.this, "?????? ????????? ?????????.(Session Closed)", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onSuccess(MeV2Response result) { //result??? ???????????? ???????????? ?????????
                        //????????? ??????
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        //intent.putExtra("name",result.getKakaoAccount().getProfile().getNickname());
                        //intent.putExtra("profileImg",result.getKakaoAccount().getProfile().getProfileImageUrl());

                        //Log.d("soo","????????? ?????? ?????? ??? : "+ result.getKakaoAccount().getProfile().getProfileImageUrl().toString());
                        intent.putExtra("email",result.getKakaoAccount().getEmail());
                        startActivity(intent);

                        Toast.makeText(LoginActivity.this, "????????? ??????", Toast.LENGTH_SHORT).show();



                    }
                });

            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                Toast.makeText(LoginActivity.this, "onSessionOpenFailed", Toast.LENGTH_SHORT).show();

            }
        };

        Session.getCurrentSession().addCallback(mSessionCallback);
        Session.getCurrentSession().getAccessTokenCallback(); //?????? ?????? ????????? ????????? ??????


        //?????? ???????????? ??????
        btn_register = findViewById(R.id.btn_register_tv);
        btn_findingIdPw = findViewById(R.id.btn_findingIdPw_tv);
        btn_login = findViewById(R.id.btn_login);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_pw);
        loading = findViewById(R.id.loading);
        btn_findingIdPw = findViewById(R.id.btn_findingIdPw_tv);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPw = password.getText().toString().trim();

                if (!mEmail.isEmpty() || !mPw.isEmpty()){
                    Login(mEmail,mPw);
                }else {
                    email.setError("???????????? ??????????????????.");
                    password.setError("??????????????? ??????????????????.");
                }

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btn_findingIdPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPWActivity.class));
            }
        });

        //??????????????? ?????? ????????? ??? ????????? ??????
        btn_send_mail_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(LoginActivity.this, SendMailAgain.class));

            }
        });

    }//close create

    //????????? ??????
    private void Login(String email, String password) {
        loading.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL_Login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           JSONObject jsonObject = new JSONObject(response);
                           String success = jsonObject.getString("success");
                           JSONArray jsonArray = jsonObject.getJSONArray("login");

                           if(success.equals("1")){
                               for (int i = 0; i < jsonArray.length(); i++){

                                   //????????? ??????????????? ??? ?????? ?????? ???????????? ????????? ???????????? ???????????? ??? ???????????????
                                   JSONObject object = jsonArray.getJSONObject(i);
                                   String name = object.getString("name").trim();
                                   String email = object.getString("email").trim();
                                   String id = object.getString("id").trim();
                                   String profile_img = object.getString("profile_img").trim();
                                   String role = object.getString("role").trim();
                                   sessionManager.createSession(name,email,id,profile_img,role); //????????? ???????????? ???????????? ????????? ???????????? ????????????.
//
//                                   Log.d("soo","????????? json ????????? email : "+email);
//                                   Log.d("soo","????????? json ?????????  : "+jsonArray);

                                   Toast.makeText(LoginActivity.this,"????????? ??????", Toast.LENGTH_SHORT).show();
                                   loading.setVisibility(View.GONE);
                                   startActivity(new Intent(LoginActivity.this, MainActivity.class));


                              }
                           }else {
                               loading.setVisibility(View.GONE);
                               btn_login.setVisibility(View.VISIBLE);
                               String message = jsonObject.getString("message");
                               //??????????????? ????????? ????????? ?????????????????? ???????????? ???.
                               Toast.makeText(LoginActivity.this, message , Toast.LENGTH_SHORT).show();

                               AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                               dialog = builder.setMessage("???????????? ???????????? ???????????????. ???????????? ?????? ???????????????????")
                                       .setNegativeButton("????????? ?????????", null)
                                       .setPositiveButton("??????",null )
                                       .create();

                               dialog.show();
                               Log.d("soo","????????? ??????//????????? ?????? ??????. ");
                           }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "????????? ??????" + e.toString(),Toast.LENGTH_SHORT).show();
                            Log.d("soo","????????? ??????: " +e.toString());

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "????????? ??????2" + error.toString(),Toast.LENGTH_SHORT).show();

                    }




                })


        {

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("email",email);
                params.put("password",password);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }











    //????????? ?????? ??????
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }




}
