package com.example.frame;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.AddFeedImgAdapter;
import com.example.frame.etc.NetworkStatus;
import com.example.frame.etc.SessionManager;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class AddEventActivity extends AppCompatActivity {
    Button btn_pick_date;
    private TextView tv_event_date_start, tv_event_date_end, btn_upload_event;
    private ImageButton btn_camera;
    SessionManager sessionManager;
    private ArrayList<Uri> uriList = new ArrayList<>();
    RecyclerView recyclerView;
    AddFeedImgAdapter adapter;
    private ArrayList<String> feedImgArrayList = new ArrayList<>(); //pathList
    private EditText event_contents,event_add_title,number_entry,address,address_detail;
    private TextView event_date_start,event_date_end;
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        //btn_pick_date=findViewById(R.id.btn_pick_date);
        tv_event_date_start = findViewById(R.id.event_date_start);
        tv_event_date_end = findViewById(R.id.event_date_end);

        btn_upload_event = findViewById(R.id.btn_upload_event);
        btn_camera = findViewById(R.id.btn_event_add_camera);

        event_contents = findViewById(R.id.feed_contents_et);
        recyclerView = findViewById(R.id.rv_add_event_img);
        event_add_title = findViewById(R.id.event_add_title);
        number_entry = findViewById(R.id.number_entry);
        event_date_start = findViewById(R.id.event_date_start);
        event_date_end = findViewById(R.id.event_date_end);
        address = findViewById(R.id.address);
        address_detail = findViewById(R.id.address2);

        address.setFocusable(false);//주소창 터치안되게 막기
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    Log.i("주소설정페이지", "주소입력창 클릭");

                    Intent i = new Intent(getApplicationContext(), AddressApiActivity.class);

                    // 화면전환 애니메이션 없애기

                    overridePendingTransition(0, 0);

                    // 주소결과
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);


                }else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //이벤트 시작일 선택
        tv_event_date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickStartDate();
            }
        });

        //이벤트 종료일 선택
        tv_event_date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickEndDate();
            }
        });


        //피드 업로드 버튼 누르면 실행
        btn_upload_event.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                sendEventInfo();

            }
        });

        //갤러리로 이동하는 버튼
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withContext(AddEventActivity.this)
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



    }//onCreate()



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

                            adapter = new AddFeedImgAdapter(getApplicationContext(), feedImgArrayList);
                            recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                            recyclerView.setLayoutManager(new LinearLayoutManager(AddEventActivity.this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용



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
                                recyclerView.setLayoutManager(new LinearLayoutManager(AddEventActivity.this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용


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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("test", "onActivityResult");

        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        Log.i("test", "data:" + data);
                        address.setText(data);
                    }
                }
                break;
        }
    }







    private void pickStartDate() {

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("이벤트 날짜 선택");

        final MaterialDatePicker materialDatePicker = builder.build();

        materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
        //날짜 하나 선택
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                tv_event_date_start.setText(materialDatePicker.getHeaderText());

            }
        });

    }
    private void pickEndDate() {

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("이벤트 날짜 선택");

        final MaterialDatePicker materialDatePicker = builder.build();

        materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
        //날짜 하나 선택
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                tv_event_date_end.setText(materialDatePicker.getHeaderText());

            }
        });

    }


    private void sendEventInfo() {

        String url = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/add_event.php";

        sessionManager = new SessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetail();
        String user_id = user.get(sessionManager.ID);//로그인 한 사용자 id 가져오기(쉐어드에서)

        final String contents = this.event_contents.getText().toString().trim();//적힌 피드 내용
        final String event_add_title = this.event_add_title.getText().toString().trim();//적힌 피드 내용
        final String event_date_start = this.event_date_start.getText().toString().trim();//적힌 피드 내용
        final String event_date_end = this.event_date_end.getText().toString().trim();//적힌 피드 내용
        final String number_entry = this.number_entry.getText().toString().trim();//적힌 피드 내용
        final String address = this.address.getText().toString().trim();
        final String address_detail = this.address_detail.getText().toString().trim();//상세주소


        SimpleMultiPartRequest smprEvent = new SimpleMultiPartRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String post = jsonObject.getString("upload");

                    if(post.equals("1")){

                        Toast.makeText(AddEventActivity.this, "이벤트 등록 성공", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddEventActivity.this, MainActivity.class));

                    }else{
                        Toast.makeText(AddEventActivity.this, "이벤트 등록 실패!!", Toast.LENGTH_SHORT).show();
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

        smprEvent.addStringParam("event_title", event_add_title);
        smprEvent.addStringParam("start_date", event_date_start);
        smprEvent.addStringParam("end_date", event_date_end);
        smprEvent.addStringParam("entry", number_entry);
        smprEvent.addStringParam("contents", contents);
        smprEvent.addStringParam("cntImage", String.valueOf(feedImgArrayList.size()));//이미지 개수 보내기
        smprEvent.addStringParam("address", address);
        smprEvent.addStringParam("address_detail", address_detail);

        Log.d("soo","전송 event_title : " + event_add_title);
        Log.d("soo","전송 address_detail : " + address_detail);


        //이미지 파일 추가
        if(feedImgArrayList.size() > 0 ){ //이미지가 추가되면
            for(int i = 0; i < feedImgArrayList.size(); i++){
                smprEvent.addFile("image" + i, feedImgArrayList.get(i)); //파일에 이미지들을 추가함.
                Log.d("soo","전송 후 path 확인 : " + feedImgArrayList.get(i));
            }
        }

        //사용자가 request 객체에 요청 내용 담아 RequestQueue에 추가하면,
        //RequestQueue가 알아서 쓰레드 생성해 서버에 요청을 보내고 응답 받음.
        //응답오면 RequestQueue에서 Request에 등록된 ResponseListener로 응답을 전달해줌.
        //volley장점: 사용자가 별도의 쓰레드 관리나 UI접근을 위한 핸들러를 다룰 필요가 없음.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(smprEvent);


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



}
