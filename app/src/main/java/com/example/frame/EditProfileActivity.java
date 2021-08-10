package com.example.frame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.frame.etc.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import com.example.frame.etc.SessionManager;


public class EditProfileActivity extends AppCompatActivity {

private TextInputEditText email,name,birth,phone_num;
private TextView btn_edit_profile;
private ImageButton btn_photo_upload;
private static String URL_Edit_profile = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/edit_profile.php";
private static String URL_read_profile = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_profile.php";

SessionManager sessionManager;

private Bitmap bitmap;
CircleImageView profile_image;
ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btn_photo_upload = findViewById(R.id.btn_photo_profile);
        //profile_image.findViewById(R.id.imageView3);
        photo = findViewById(R.id.imageView33);

        //레이아웃에 선언한 컴포넌트들과 변수 연결
        email = findViewById(R.id.til_profile_email);
        name = findViewById(R.id.til_name);
        birth = findViewById(R.id.til_date);
        phone_num = findViewById(R.id.til_phone);
        btn_edit_profile =findViewById(R.id.btn_edit_profile);

        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Edit_profile();
            }
        });

        sessionManager = new SessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetail();
        String sEmail = user.get(sessionManager.EMAIL);
        //Log.e("soo","쉐어드 확인: "+mEmail);

        // Glide로 이미지 표시하기
        String imageUrl2 = " http://www.culture.go.kr/upload/rdf/21/03/rdf_2021033110325407198.jpg";
        String imageUrl = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png";
        Glide.with(this).load(imageUrl2).into(photo);






        btn_photo_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //디비에 저장된 값을 가져오기
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL_read_profile,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("profile");

                            if(success.equals("1")){
                                for (int i = 0; i < jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String db_name = object.getString("name").trim();
                                    String db_email = object.getString("email").trim();
                                    String db_birth = object.getString("birth").trim();
                                    String db_phone_num = object.getString("phone_num").trim();

                                    email.setText(db_email);
                                    email.setEnabled(false); //이메일 값 고정

                                    name.setText(db_name);
                                    birth.setText(db_birth);
                                    phone_num.setText(db_phone_num);

                                }





                            }else {

                                Log.d("soo","프로필 에러/ 프로필 읽어오지 못함.");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(EditProfileActivity.this, "로그인 에러" + e.toString(),Toast.LENGTH_SHORT).show();
                            Log.d("soo","프로필 에러: " +e.toString());

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {




                        Toast.makeText(EditProfileActivity.this, "프로필 에러2" + error.toString(),Toast.LENGTH_SHORT).show();

                    }
                })


        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("email",sEmail);


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void Edit_profile() {

        //내정보 수정 액티비티에서 editText에 적힌 텍스트들을 스트링으로 바꾼다.
        final String email = this.email.getText().toString().trim();
        final String name = this.name.getText().toString().trim();
        final String birth = this.birth.getText().toString().trim();
        final String phone_num = this.phone_num.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Edit_profile,
                new Response.Listener<String>() { //결과 콜백
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONObject jsonObject = new JSONObject(response);
                            //제이슨으로 보내진 success 메세지 받음.
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(EditProfileActivity.this, "내 정보 수정 성공",Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, "회원가입 에러" + e.toString(),Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {//에러 콜백
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfileActivity.this, "에러" + error.toString(),Toast.LENGTH_SHORT).show();


                    }
                })
        {

            //@org.jetbrains.annotations.Nullable
            @Override //클라이언트 데이터 서버로 전송하기 위해
            protected Map<String, String> getParams() throws AuthFailureError {

                //서버에 전송할 데이터 맵 객체에 담아 변환.
                Map<String,String> params = new HashMap<>();

                params.put("name",name);
                params.put("email",email);
                params.put("birth",birth);
                params.put("phone_num",phone_num);

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);    //서버에 요청


    }





    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select picture"),1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            uploadPicture(getStringImage(bitmap));
        }
    }

    private void uploadPicture(final String photo) {


    }
    public String getStringImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        //String encodeImage = Base64.encodeToString(imageByteArray,Base64.DEFAULT);

        return null; //encodeImage;
    }


}