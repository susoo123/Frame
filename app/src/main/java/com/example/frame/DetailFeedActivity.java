package com.example.frame;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.frame.adapter.FeedAdapter;
import com.example.frame.adapter.ImageSliderInFeedAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataFeed;
import com.example.frame.etc.DataFeedImg;
import com.example.frame.etc.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class DetailFeedActivity extends AppCompatActivity {
    private ImageButton btn_feed_option;
    private TextView item_feed_username,item_feed_contents,item_feed_time;
    private ImageView item_feed_profile_image;
    String username, user_id;
    AlertDialog dialog;
    private static String URL_read_feed_detail = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_feed_detail.php";
    private FloatingActionButton btn_add_feed;
    RecyclerView item_feed_image_rv;
    private String feed_writer, feed_contents, feed_img, feed_id_i, feed_time, feed_profile_img, feed_id, feed_user_id, feed_uid;
    RecyclerView.Adapter adapter;
    private ArrayList<DataFeed> feedList = new ArrayList<>();
    private JSONArray imagejArray = new JSONArray();
    private ArrayList<DataFeedImg> imgDataArray;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_feed);

        item_feed_username = findViewById(R.id.item_feed_username);
        item_feed_contents = findViewById(R.id.item_feed_contents);
        item_feed_time = findViewById(R.id.item_feed_time);
        item_feed_profile_image = findViewById(R.id.item_feed_profile_image);


        Intent intent = getIntent();
        feed_id_i = intent.getExtras().getString("feed_id");
        Log.e("오류태그", feed_id_i);
        sendRequest();

        init_rv();

//        Bundle extras = getIntent().getExtras();
//        if(extras !=null){
//            username = extras.getString("userName");
//
//        }
//        item_feed_username.setText(username);


        //피드 옵션 메뉴 설정!! 피드 수정하기, 삭제하기
        btn_feed_option = findViewById(R.id.btn_feed_option);
        btn_feed_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.menu_feed, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.edit) {
                            Toast.makeText(DetailFeedActivity.this, "메뉴 1 클릭", Toast.LENGTH_SHORT).show();
                        } else if (menuItem.getItemId() == R.id.del) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DetailFeedActivity.this);
                            dialog = builder.setMessage("피드를 삭제하시겠습니까?")
                                    .setNegativeButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            sendDelMessage();
                                        }
                                    })
                                    .setPositiveButton("취소", null)
                                    .create();

                            dialog.show();
                            Toast.makeText(DetailFeedActivity.this, "피드를 삭제했습니다.", Toast.LENGTH_SHORT).show();

                        }
                        return false;
                    }
                });


                popupMenu.show();
            }
        });


    }

    public void sendDelMessage() {
        String url = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/del_feed.php";
        String feed_id;


        StringRequest smpr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String post = jsonObject.getString("upload");

                    if (post.equals("1")) {

                        Toast.makeText(DetailFeedActivity.this, "피드 삭제 완료", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DetailFeedActivity.this, MainActivity.class));

                    } else {
                        Toast.makeText(DetailFeedActivity.this, "피드 삭제 실패!!", Toast.LENGTH_SHORT).show();
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
        });

        //데이터 추가
        //smpr.setParams("feed_id", feed_id); //피드 고유 아이디를 보내서 서버에 del_status 삭제 상태 되도록!!
        //smpr.setParams("feed_uid", feed_uid);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(smpr);
    }

    private void init_rv(){
        //리사이클러뷰 관련
        item_feed_image_rv =findViewById(R.id.item_feed_image_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//리사이클러뷰 가로로만드는 코드
        item_feed_image_rv.setLayoutManager(layoutManager);

    }


    //볼리로 요청 보내기
    public void sendRequest() {
        String url = URL_read_feed_detail;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("feed_detail"); //"feed"라는 jsonArray를 php에서 받음

                            //feed 어레이 풀기
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                feed_writer = object.getString("name");
                                feed_contents = object.getString("feed_contents");
                                feed_img = object.getString("profile_img");

                                //feed_img = object.getString("imageArray");
                                imagejArray = object.getJSONArray("imageArray"); //json어레이 안에 들 이미지 어레이(얘도 jsonArray)

                                //jsonArray를 ArrayList에 담기 위해
                                if(imagejArray !=null){
                                    imgDataArray = new ArrayList<>();
                                    for (int j = 0; j < imagejArray.length(); j++){
                                        // imgDataArray.add(imagejArray.getString(j));

                                        //이미지를 담을 리사이클러뷰(피드 내 이미지 슬라이더 기능) model class에 담아줌.
                                        imgDataArray.add(new DataFeedImg(imagejArray.getString(j)));
                                    }

                                }


                                feed_time = object.getString("feed_date");
                                feed_profile_img = object.getString("profile_img");
                                feed_user_id = object.getString("feed_user_id");
                                feed_id = object.getString("feed_id");


                                item_feed_username.setText(feed_writer);
                                item_feed_contents.setText(feed_contents);
                                Glide.with(getApplicationContext()) //해당 환경의 Context나 객체 입력
                                        .load(feed_img) //URL, URI 등등 이미지를 받아올 경로
                                        .centerCrop()
                                        .into(item_feed_profile_image); //받아온 이미지를 받을 공간(ex. ImageView)




                                Log.d("soo", "php에서 json으로 받은 array 값: " + jsonArray);
                                Log.d("soo", "php에서 json으로 받은 imageArray 값: " + imagejArray);
                                Log.d("soo", "imgDataArray 값: " + imgDataArray);

                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Date date = fm.parse(feed_time);
                                Log.d("soo", "date 값: " + date);
                                beforeTime(date);
                                Log.d("soo", "beforeTime 값: " +  beforeTime(date));
                                item_feed_time.setText(beforeTime(date));

                                ImageSliderInFeedAdapter imageSliderInFeedAdapter = new ImageSliderInFeedAdapter(getApplicationContext(),imgDataArray);
                                item_feed_image_rv.setAdapter(imageSliderInFeedAdapter);
                                imageSliderInFeedAdapter.notifyDataSetChanged();
                                //feed_uid = feed_id 임!!
                                feedList.add(new DataFeed(feed_writer,feed_contents,imgDataArray,beforeTime(date),feed_profile_img,feed_user_id,feed_id));

                            }






                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Log.d("soo1", "응답1 -> " + e.getMessage());
                        }

                        Log.d("soo1", "응답 -> " + response);
                    }


                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){

                        Log.d("soo1", "에러 -> " + error.getMessage());

                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("feed_id_i",feed_id_i);
                Log.d("feed_id_i 아이디 전송 성공", feed_id_i);



                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }


    //몇 분 전 으로 시간 표시해주는 메서드
    public String beforeTime(Date date){

        Calendar c = Calendar.getInstance();

        long now = c.getTimeInMillis(); //현재 시각을 미리 세컨드로 바꾼다.
        long dateM = date.getTime(); // 해당 날짜의 시각을 가져온다.
        long gap = now - dateM; // 현재 시각 - 해당 날짜 시각


        String ret = "";

//        초       분   시
//        1000    60  60
        gap = (long)(gap/1000); // 밀리세컨이라서
        long hour = gap/3600; // 밀리세컨 제거한 시각/3600(3600= 1시간을 초로 나타낸것)
        gap = gap%3600; // 3600으로 나누고 난 나머지

        long min = gap/60; // 1분이 60초
        long sec = gap%60; // 초는 60(1분이 60초라서)으로 나누고 난 후 나머지는 모두 초!!

        if(hour > 24){ // 시가 24보다 크면 (하루 지나면)
            ret = new SimpleDateFormat("MM월dd일").format(date);
        }
        else if(hour > 0){ //시가 0보다 크면 (즉 시(hour)가 존재한다는 의미 )
            ret = hour+"시간 전";
        }
        else if(min > 0){
            ret = min+"분 전";
        }
        else if(sec > 0){
            ret = "방금";
        }
        else{
            ret = new SimpleDateFormat("HH:mm").format(date);
        }
        return ret;

    }



}