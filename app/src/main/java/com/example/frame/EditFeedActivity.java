package com.example.frame;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;

import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.frame.adapter.AddFeedImgAdapter;
import com.example.frame.etc.DataFeedImg;
import com.example.frame.etc.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


//피드 수정하기 액티비티
public class EditFeedActivity extends AppCompatActivity {

    //아이템 피드 id를 가져오기
    //그걸 서버로 요청하고
    //피드id와 일치하는 정보 가져오기
    private static String URL_display_feed = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/display_feed.php";

    private EditText feed_contents;
    private ImageButton btn_feed_camera;
    private TextView btn_upload_feed;
    private ImageView feed_img;

    RecyclerView recyclerView;
    AddFeedImgAdapter adapter;
    private ArrayList<String> feedImgArrayList = new ArrayList<>(); //pathList

    private  ArrayList<Uri> uriList = new ArrayList<>();
    private JSONArray imagejArray = new JSONArray(); //db에 저장되어 있는 이미지 어레이
    private ArrayList imgDataArray;
    private ArrayList imgDataArray2; //uri안넣고 경로만 넣은 어레이
    private ArrayList removeImgArray = new ArrayList<>();


    private String feed_id;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);
        feed_contents = findViewById(R.id.feed_contents_et);
        btn_feed_camera = findViewById(R.id.feed_camera_ib);
        btn_upload_feed = findViewById(R.id.btn_upload_feed);
        //feed_img = findViewById(R.id.feed_img_iv);
        recyclerView = findViewById(R.id.rv_add_feed_img);

        //이전페이지에서 intent로 해당 피드 아이템의 feed_id를 전송했고, 전송한 값을 받음.
        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            feed_id = extras.getString("feed_id");
        }

        //피드 정보 가져오기
        displayFeed(feed_id);



        //피드 업로드 버튼 누르면 실행
        btn_upload_feed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                sendFeedInfo();

            }
        });



        btn_feed_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(EditFeedActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                //사진 다중 선택 하기 위한 코드!!
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // EXTRA_ALLOW_MULTIPLE을 true로 값을 입력해야 사진 다중 선택 가능
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

                    Intent data = result.getData(); //갤러리에서 받아오는 데이터
                    Uri imgUri = data.getData(); //이미지 uri


                    if(data == null){//갤러리에서 받아온 데이터가 없을 때

                        Toast.makeText(getApplicationContext(),"이미지를 선택하지 않았습니다", Toast.LENGTH_SHORT).show();

                    }else {//이미지를 하나라도 선택한 경우


                        ClipData clipData = data.getClipData();
                        if (clipData == null) {
                            // getClipData()는 EXTRA_ALLOW_MULTIPLE가 제대로 먹힌 디바이스에서 가져오는 결과 값이며
                            // null을 리턴한다면 여러 장 선택을 지원하지 않는 기기를 의미함.
                            // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함

                            Log.d("soo", "단일 사진 선택 : " + String.valueOf(data.getData()));

                            uriList.add(imgUri);
                            //imgDataArray.add(imgUri);

                            adapter = new AddFeedImgAdapter(getApplicationContext(), imgDataArray);

                            recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                            recyclerView.setLayoutManager(new LinearLayoutManager(EditFeedActivity.this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용



                        } else {//이미지를 여러장 선택한 경우
                            // ClipData clipData = data.getClipData();
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
                                        Log.e("soo", "uriList2 : " + uriList);

                                        String path = getPathFromUri(imageUri);

                                        feedImgArrayList.add(path);
                                        imgDataArray.add(path);//리사이클러뷰에 띄우려면 필요함.
                                        Log.e("soo", "feedImgArrayList2 : " + feedImgArrayList);//에뮬 내의 경로
                                        Log.e("soo", "imgDataArray2 : " + imgDataArray); //최종적으로 가야하는.. 근데 에뮬 내의 경로가 섞여 있음.
                                        Log.e("soo", "imagejArray2 : " + imagejArray); //원래 디비에 저장되어 있던 경로



                                    } catch (Exception e) {

                                        Log.e("soo", "File select error!", e);
                                    }
                                }

                                adapter = new AddFeedImgAdapter(getApplicationContext(), imgDataArray);
                                recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                                recyclerView.setLayoutManager(new LinearLayoutManager(EditFeedActivity.this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용


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



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendFeedInfo(){
        String url = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/update_feed.php";

        final String contents = this.feed_contents.getText().toString().trim();//적힌 피드 내용


        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String post = jsonObject.getString("upload");

                    if(post.equals("1")){

                        Toast.makeText(EditFeedActivity.this, "피드 수정 성공", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditFeedActivity.this, MainActivity.class));

                    }else{
                        Toast.makeText(EditFeedActivity.this, "피드 수정 실패!!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }




        }); //SimpleMultiPartRequest 끝.


        //데이터 추가
        //smpr.addStringParam("user_id", user_id); //쉐어드에서 가져온 로그인한 사용자 id
        smpr.addStringParam("contents", contents); //피드에 적힐 내용
        smpr.addStringParam("feed_id", feed_id); //피드 고유번호

        if(feedImgArrayList != null){
            smpr.addStringParam("cntImage", String.valueOf(feedImgArrayList.size()));//이미지 개수 보내기
            //이미지 파일 추가
            if(feedImgArrayList.size() > 0 ){ //이미지가 추가되면
                for(int i = 0; i < feedImgArrayList.size(); i++){
                    smpr.addFile("image" + i, feedImgArrayList.get(i)); //파일에 이미지들을 추가함.
                    Log.d("soo","전송 후 path 확인 : " + feedImgArrayList.get(i)); //에뮬 내의 경로가 feedImgArrayList에 들어감
                }
            }
        }

        String str_url ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/profile_image/";
        //contains(url)
        String dataImg = String.join(",",imgDataArray);

//        for(int j = 0; j < imgDataArray.size(); j++) {
//               String data = (String) imgDataArray.get(j);
//
//            if (data.contains(str_url)) {
//                smpr.addStringParam("urlStrArrayList", dataImg);
//                Log.d("soo", "전송 후 urlArrayList 확인 : " + dataImg);
//            }
//        }
//
//            }else {
//
//                if(feedImgArrayList != null){
//                    smpr.addStringParam("cntImage", String.valueOf(feedImgArrayList.size()));//이미지 개수 보내기
//                    //이미지 파일 추가
//                    if(feedImgArrayList.size() > 0 ){ //이미지가 추가되면
//                        for(int i = 0; i < feedImgArrayList.size(); i++){
//                            smpr.addFile("image" + i, feedImgArrayList.get(i)); //파일에 이미지들을 추가함.
//                            Log.d("soo","전송 후 path 확인 : " + feedImgArrayList.get(i)); //에뮬 내의 경로가 feedImgArrayList에 들어감
//                        }
//                    }
//                }
//
//            }
//
//
//
//        }

        if(imgDataArray != null) {

            for(int j = 0; j < imgDataArray.size(); j++) {
                String data = (String) imgDataArray.get(j);

                if (data.contains(str_url)) {
                    smpr.addStringParam("urlStrArrayList", data);
                    //Log.e("soo", "전송 후 urlArrayList 확인 : " + dataImg);
                    Log.e("soo", "전송 후 urlArrayList 확인 : " + data);
                }

            }
            //smpr.addStringParam("urlStrArrayList", data);

            smpr.addStringParam("imgDataArray", String.join(",",imgDataArray)); //섞인게 감...
            //smpr.addStringParam("imgDataArray2", String.valueOf(imgDataArray2));
            Log.d("soo","전송 후 imgDataArray 확인 : " + String.join(",",imgDataArray));
        }

//        if(imgDataArray2 != null) {
//            smpr.addStringParam("imgDataArray2", String.join(",",imgDataArray2));
//            //smpr.addStringParam("imgDataArray2", String.valueOf(imgDataArray2));
//            Log.d("soo","전송 후 imgDataArray2 확인 : " + String.join(",",imgDataArray2));
//        }




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
                //adapter.notifyDataSetChanged();
                removeImgArray.add(item.getGroupId());
                Log.e("soo","삭제된 이미지 어레이: "+removeImgArray);


                return true;

        }

        return super.onContextItemSelected(item);

    }



    //볼리 사용해서 피드id 보내면 해당 피드 정보 가져와서 띄우기
    private void displayFeed(String feed_id3) {

        //디비에 저장된 content와 이미지들만 가져오면 됨...

        //디비에 저장된 값을 가져오기
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL_display_feed,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("feed_display");

                            if(success.equals("1")){
                                for (int i = 0; i < jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String db_contents = object.getString("feed_contents").trim();
                                    //이미지 어레이 가져와야함..
                                    String feed_id = object.getString("feed_id").trim();

                                    imagejArray = object.getJSONArray("imageArray"); //json어레이 안에 들 이미지 어레이(얘도 jsonArray)


                                    //jsonArray를 ArrayList에 담기 위해
                                    if(imagejArray !=null) {
                                        imgDataArray = new ArrayList<Uri>();
                                        for (int j = 0; j < imagejArray.length(); j++) {

                                            imgDataArray.add(imagejArray.getString(j));

                                        }
                                        Log.e("어레이확인","imgDataArray : "+imgDataArray);
                                    }

//                                    // 디비에 저장되어 있던 이미지 경로 그대로 arraylist 만들어주려고
//                                    if(imagejArray !=null) {
//                                        imgDataArray2 = new ArrayList<Uri>();
//                                        for (int j = 0; j < imagejArray.length(); j++) {
//
//                                            imgDataArray2.add(imagejArray.getString(j));
//                                            Log.e("어레이확인","imgDataArray2 : "+imgDataArray2);
//                                        }
//                                    }



                                    adapter = new AddFeedImgAdapter(getApplicationContext(), imgDataArray);
                                    recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                                    recyclerView.setLayoutManager(new LinearLayoutManager(EditFeedActivity.this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용

                                    feed_contents.setText(db_contents);

                                }



                            }else {

                                Log.d("soo","피드 에러/ 피드 읽어오지 못함.");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(EditFeedActivity.this, "피드 가져오기  에러" + e.toString(),Toast.LENGTH_SHORT).show();
                            Log.d("soo","피드 가져오기  에러: " +e.toString());

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }




                })


        {

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("feed_id",feed_id3);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }








}











