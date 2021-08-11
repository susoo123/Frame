package com.example.frame;

import static com.kakao.usermgmt.StringSet.user_id;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.frame.etc.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddFeedActivity extends AppCompatActivity {

    private static String URL_create_feed = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/create_feed.php";
    private EditText feed_contents;
    private ImageButton btn_feed_camera;
    private TextView btn_upload_feed;
    private ImageView feed_img;
    SessionManager sessionManager;
    Bitmap bitmap;
    String encodeImageString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);
        feed_contents = findViewById(R.id.feed_contents_et);
        btn_feed_camera = findViewById(R.id.feed_camera_ib);
        btn_upload_feed = findViewById(R.id.btn_upload_feed);
        feed_img = findViewById(R.id.feed_img_iv);


        //피드 업로드 버튼 누르면 실행
        btn_upload_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_feed();
            }
        });



        btn_feed_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(AddFeedActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                launcher.launch(intent);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

            }
        });




    }



    //이미지 업로드 관련
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Intent intent = result.getData();
                        Uri filepath = intent.getData();

//                      imageview.setImageURI(uri);

                        Glide.with(AddFeedActivity.this)
                                .load(filepath)
                                .centerCrop()
                                .into(feed_img);

                        try{
                            InputStream inputStream = getContentResolver().openInputStream(filepath);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            feed_img.setImageBitmap(bitmap);
                            encodeBitmapImage(bitmap);

                        }catch (Exception e){

                        }



                    }
                }
            });



    //이미지 인코딩
    private void encodeBitmapImage(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImageString = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);

    }

    private void upload_feed() {
        //내정보 수정 액티비티에서 editText에 적힌 텍스트들을 스트링으로 바꾼다.
        final String contents = this.feed_contents.getText().toString().trim();
        sessionManager = new SessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetail();
        String user_id = user.get(sessionManager.ID);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_create_feed,
                new Response.Listener<String>() { //결과 콜백
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){

                                Toast.makeText(AddFeedActivity.this, "피드 업로드 성공",Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(AddFeedActivity.this, MainActivity.class));

                            }else {

                                Log.d("soo","피드 에러1");

                            }


                        } catch (JSONException e) {

                            e.printStackTrace();
                            Toast.makeText(AddFeedActivity.this, "피드 에러2" + e.toString(),Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {//에러 콜백
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(AddFeedActivity.this, "에러" + error.toString(),Toast.LENGTH_SHORT).show();

                    }
                })
        {


            @Override //클라이언트 데이터 서버로 전송하기 위해
            protected Map<String, String> getParams() throws AuthFailureError {

                //서버에 전송할 데이터 맵 객체에 담아 변환.
                Map<String,String> params = new HashMap<>();

                //서버로 보내야하는 것  = 피드 내용, 피드 이미지, 유저id
                params.put("contents",contents);
                params.put("feed_img",encodeImageString);
                params.put("user_id",user_id); //쉐어드에 저장해둔 유저 id

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);    //서버에 요청

    }




}
