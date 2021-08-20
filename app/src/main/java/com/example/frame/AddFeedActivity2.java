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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.AddFeedImgAdapter;
import com.example.frame.etc.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AddFeedActivity2 extends AppCompatActivity {

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
    private ArrayList<String> feedImgArrayList = new ArrayList<>(); //pathList
    private Dialog dialog;
    ProgressDialog progressDialog;
    private  ArrayList<Uri> uriList = new ArrayList<>();
    ArrayList<String> mainPathList = new ArrayList<String>();
    private String feed_uid;



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
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                sendImages();

            }
        });



        btn_feed_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(AddFeedActivity2.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                //사진 다중 선택 하기 위한 코드!!
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // true로 값을 입력해야 사진 다중 선택 가능
                                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //데이터를 uri로 받는다.

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

                    Intent data = result.getData();
                    Uri imgUri = data.getData();

                    if(data == null){

                        Toast.makeText(getApplicationContext(),"이미지를 선택하지 않았습니다", Toast.LENGTH_SHORT).show();

                    }else {//이미지를 하나라도 선택한 경우


                        if (data.getClipData() == null) {
                            //이미지를 하나만 선택한 경우
                            Log.d("soo", "단일 사진 선택 : " + String.valueOf(data.getData()));

                            uriList.add(imgUri);

                            adapter = new AddFeedImgAdapter(getApplicationContext(), feedImgArrayList);
                            recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                            recyclerView.setLayoutManager(new LinearLayoutManager(AddFeedActivity2.this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용



                        } else {//이미지를 여러장 선택한 경우
                            ClipData clipData = data.getClipData();
                            Log.d("soo", "여러개 사진 선택 : " + String.valueOf(clipData.getItemCount()));

                            if (clipData.getItemCount() > 5) {//사진을 5장 이상인 경우

                                Toast.makeText(getApplicationContext(), "사진은 5장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();

                            } else {//선택한 사진이 1장 이상 5장 이하인경우
                                Log.d("soo", "멀티 사진 선택 모드");

                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Uri imageUri = clipData.getItemAt(i).getUri();//선택한 이미지들의 uri 를 가져옴

                                    try {

                                        //uri 를  list 에 담는다
                                        uriList.add(imageUri);
                                        Log.d("soo", "uri 확인" + imageUri);

                                        String path = getPathFromUri(imageUri);
                                        Log.d("soo", "path 확인 : " + path);

                                        feedImgArrayList.add(path);
                                        Log.d("soo", "pathList size 확인 : " + feedImgArrayList.size());

                                    } catch (Exception e) {

                                        Log.e("soo", "File select error!", e);
                                    }
                                }

                                adapter = new AddFeedImgAdapter(getApplicationContext(), feedImgArrayList);
                                recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                                recyclerView.setLayoutManager(new LinearLayoutManager(AddFeedActivity2.this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용


                            }
                        }
                    }


                }
            });






    //이미지 경로 가져오기(내부저장소에서 저장된 데이터 가져올때)
    public String getPathFromUri(Uri uri){

        //ContentResolver = content provider의 주소를 통해 데이터에 접근해 결과값을 가져옴.
        //content provider 는 어플리케이션 사이에서 data를 공유하는 통로역할을 함.
        //content provider가 안드로이드 시스템의 각종 설정값이나 디비에 접근하게 해준다면 반환하는 브릿지 역할은 content Resolver가 하게됨.
        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();

        return path;
    }



    public void sendImages(){
        String url = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/create_feed3.php";


        sessionManager = new SessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetail();
        String user_id = user.get(sessionManager.ID);//로그인 한 사용자 id 가져오기(쉐어드에서)

        final String contents = this.feed_contents.getText().toString().trim();//적힌 피드 내용
        feed_uid = createCode(); //feed_uid를 생성함.


        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String post = jsonObject.getString("upload");

                    if(post.equals("1")){

                        Toast.makeText(AddFeedActivity2.this, "피드 등록 성공", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddFeedActivity2.this, MainActivity.class));

                    }else{
                        Toast.makeText(AddFeedActivity2.this, "피드 등록 실패!!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("soo", "전송 실패!!");
            }
        }); //SimpleMultiPartRequest 끝.


        //데이터 추가
        smpr.addStringParam("user_id", user_id); //쉐어드에서 가져온 로그인한 사용자 id
        smpr.addStringParam("contents", contents); //피드에 적힐 내용
        smpr.addStringParam("feed_uid", feed_uid); //피드 고유번호
        smpr.addStringParam("cntImage", String.valueOf(feedImgArrayList.size()));//이미지 개수 보내기


        //이미지 파일 추가
        if(feedImgArrayList.size() > 0 ){ //이미지가 추가되면
            for(int i = 0; i < feedImgArrayList.size(); i++){
                smpr.addFile("image" + i, feedImgArrayList.get(i)); //파일에 이미지들을 추가함.
                Log.d("soo","전송 후 path 확인 : " + feedImgArrayList.get(i));
            }
        }

        //사용자가 request 객체에 요청 내용 담아 RequestQueue에 추가하면,
        //RequestQueue가 알아서 쓰레드 생성해 서버에 요청을 보내고 응답 받음.
        //응답오면 RequestQueue에서 Request에 등록된 ResponseListener로 응답을 전달해줌.
        //volley장점: 사용자가 별도의 쓰레드 관리나 UI접근을 위한 핸들러를 다룰 필요가 없음.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(smpr);
    }


    //특정 이미지 선택하고, 삭제가능하도록
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 102:
                Snackbar.make(findViewById(R.id.rootId),"삭제",Snackbar.LENGTH_LONG).show();
                adapter.RemoveItem(item.getGroupId()); //어댑터에서 이미지가 저장된 아이템이 제거됨.
            return true;

        }

        return super.onContextItemSelected(item);

    }

    //해당 피드 고유 코드 만들기 (feed_uid에 저장됨)
    private String createCode() {
        String[] str = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9","!","@","#","$"};
        String newCode = new String();

        for (int x = 0; x < 8; x++) {
            int random = (int) (Math.random() * str.length);
            newCode += str[random];
        }

        return newCode;
    }


    //이미지 인코딩
    private void encodeBitmapImage(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImageString = Base64.encodeToString(bytesofimage, Base64.DEFAULT);

    }


}
