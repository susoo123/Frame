package com.example.frame;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.android.volley.AuthFailureError;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;

import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.AddFeedImgAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;

public class AddFeedActivity extends AppCompatActivity {

    private static String URL_create_feed = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/create_feed.php";
    private static String URL_create_feed2 = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/create_feed2.php";

    private EditText feed_contents;
    private ImageButton btn_feed_camera;
    private TextView btn_upload_feed;
    private ImageView feed_img;
    SessionManager sessionManager;
    Bitmap bitmap;
    private String encodeImageString;
    RecyclerView recyclerView;
    AddFeedImgAdapter adapter;
    private ArrayList<Uri> feedImgArrayList = new ArrayList<Uri>();
    private Dialog dialog;
    ProgressDialog progressDialog;
    private  ArrayList<Uri> urlList = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);
        feed_contents = findViewById(R.id.feed_contents_et);
        btn_feed_camera = findViewById(R.id.feed_camera_ib);
        btn_upload_feed = findViewById(R.id.btn_upload_feed);
        feed_img = findViewById(R.id.feed_img_iv);
        recyclerView = findViewById(R.id.rv_add_feed_img);


        //피드 업로드 버튼 누르면 실행
        btn_upload_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
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
//                                intent.setAction(Intent.ACTION_GET_CONTENT);
//                                launcher.launch(intent);

                                //사진 다중 선택 하기 위한 코드!!
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // true로 값을 입력해야 사진 다중 선택 가능
                                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //데이터를 uri로 받는다.
//                                intent.setAction(Intent.ACTION_GET_CONTENT);//얘를 넣으면 파일로 감
                                launcher.launch(intent);

                                //startActivityForResult()는 deprecated .
                                //startActivityForResult(intent, GET_GALLERY_IMAGE); //GET_GALLERY_IMAGE는

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
                        Intent data = result.getData();
                        Uri filepath = data.getData();

                        if(data == null){
                            Toast.makeText(getApplicationContext(), "사진을 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                        }else {
                            if(data.getClipData() == null){
                                Log.e("single choice: ", String.valueOf(data.getData()));
                                filepath = data.getData();
                                feedImgArrayList.add(filepath);

                                adapter = new AddFeedImgAdapter(getApplicationContext(), feedImgArrayList);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(AddFeedActivity.this, LinearLayoutManager.HORIZONTAL, true));

                            }else {
                                ClipData clipData = data.getClipData();
                                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                                if(clipData.getItemCount() > 5){   // 선택한 이미지가 5장 이상인 경우
                                    Toast.makeText(getApplicationContext(), "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                                }
                                else{   // 선택한 이미지가 1장 이상 5장 이하인 경우
                                    Log.e("soo", "multiple choice");

                                    for (int i = 0; i < clipData.getItemCount(); i++){
                                        filepath = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                                        try {
                                            feedImgArrayList.add(filepath);  //uri를 list에 담는다.

                                        } catch (Exception e) {
                                            Log.e("soo", "File select error", e);
                                        }
                                    }

                                    adapter = new AddFeedImgAdapter(getApplicationContext(), feedImgArrayList);
                                    recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                                    recyclerView.setLayoutManager(new LinearLayoutManager(AddFeedActivity.this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용

                                }

                            }
                        }


//                        adapter = new AddFeedImgAdapter(AddFeedActivity.this,feedImgArrayList);
//                        recyclerView.setAdapter(adapter);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(AddFeedActivity.this,LinearLayoutManager.HORIZONTAL,true));
//
                        //feedImgArrayList.add(filepath);






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



//    private void upload_feed() {
//        //내정보 수정 액티비티에서 editText에 적힌 텍스트들을 스트링으로 바꾼다.
//        final String contents = this.feed_contents.getText().toString().trim();
//        sessionManager = new SessionManager(this);
//        HashMap<String,String> user = sessionManager.getUserDetail();
//        String user_id = user.get(sessionManager.ID);
//
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_create_feed,
//                new Response.Listener<String>() { //결과 콜백
//                    @Override
//                    public void onResponse(String response) {
//                        try{
//                            JSONObject jsonObject = new JSONObject(response);
//                            String success = jsonObject.getString("success");
//
//                            if(success.equals("1")){
//
//                                Toast.makeText(AddFeedActivity.this, "피드 업로드 성공",Toast.LENGTH_SHORT).show();
//
//                                startActivity(new Intent(AddFeedActivity.this, MainActivity.class));
//
//                            }else {
//
//                                Log.d("soo","피드 에러1");
//
//                            }
//
//
//                        } catch (JSONException e) {
//
//                            e.printStackTrace();
//                            Toast.makeText(AddFeedActivity.this, "피드 에러2" + e.toString(),Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                },
//                new Response.ErrorListener() {//에러 콜백
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast.makeText(AddFeedActivity.this, "에러" + error.toString(),Toast.LENGTH_SHORT).show();
//
//                    }
//                })
//        {
//
//
//            @Override //클라이언트 데이터 서버로 전송하기 위해
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                //서버에 전송할 데이터 맵 객체에 담아 변환.
//                Map<String,String> params = new HashMap<>();
//
//                //서버로 보내야하는 것  = 피드 내용, 피드 이미지, 유저id
//                params.put("contents",contents);
//               // params.put("feed_img",encodeImageString);
//                params.put("user_id",user_id); //쉐어드에 저장해둔 유저 id
//
//                return params;
//            }
//        };
//
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);    //서버에 요청
//
//    }


    // 포스트를 서버에 업로드하는 메소드
    private void uploadPost() {
        // 서버로 보낼 데이터
        //String post = feed_contents.getText().toString();
        final String contents = this.feed_contents.getText().toString().trim();
        sessionManager = new SessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetail();
        String user_id = user.get(sessionManager.ID);

        // urlList를 json 배열로 변환
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<urlList.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("url", urlList.get(i));
//                jsonObject.put("urlTitle", urlTile.get(i));
//                jsonObject.put("urlImage", urlImage.get(i));
//                jsonObject.put("urlDescription", urlDescription.get(i));
                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // 파일 전송 요청 객체 생성[결과를 String으로 받음]
        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, URL_create_feed2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success) {
                        // 업로드 성공
                        Toast.makeText(AddFeedActivity.this, "피드가 업로드되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        // 업로드 실패
                        Toast.makeText(AddFeedActivity.this, "피드 업로드를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddFeedActivity.this, "서버와 통신 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 요청 객체에 보낼 데이터를 추가
        smpr.addStringParam("contents", contents);
        smpr.addStringParam("user_id", user_id);
        smpr.addStringParam("url", jsonArray.toString()); // json 배열을 문자열로 변환
        smpr.addStringParam("cntImage", String.valueOf(feedImgArrayList.size())); // 첨부된 사진 개수

        //이미지 파일 추가 (pathList는 첨부된 사진의 내부 uri string 리스트)
        for(int i=0; i<feedImgArrayList.size(); i++) {
            // uri 절대 경로 구하기
            String[] proj= {MediaStore.Images.Media.DATA};
            CursorLoader loader= new CursorLoader(this, Uri.parse(String.valueOf(feedImgArrayList.get(i))), proj, null, null, null);
            Cursor cursor= loader.loadInBackground();
            assert cursor != null;
            int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String abUri= cursor.getString(column_index);
            cursor.close();
            // 이미지 파일 첨부
            smpr.addFile("image"+i, abUri);
        }

        // 서버에 데이터 보내고 응답 요청
        RequestQueue requestQueue= Volley.newRequestQueue(AddFeedActivity.this);
        requestQueue.add(smpr);
    }
















}
