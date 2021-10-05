package com.example.frame;
import com.bumptech.glide.Glide;
import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.EventAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataEvent;
import com.example.frame.fragment.BottomEventSheetFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailEventActivity extends AppCompatActivity {

    Button btn_event_enter;
    private String event_id;
    private static String URL_read_detail_event ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_detail_event.php";
    private ArrayList<DataEvent> eventList = new ArrayList<>();
    private TextView title_event,num_of_people,start_date,end_date,event_contents;
    private ImageView poster;
    //int position;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        btn_event_enter = findViewById(R.id.btn_event_enter);

        btn_event_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomEventSheetFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });

        Intent intent = getIntent();
        event_id = intent.getExtras().getString("event_id"); // 인텐트로 넘긴 event_id

        title_event = findViewById(R.id.title_event_detail);
        num_of_people =findViewById(R.id.number_people_event_detail_real);
        start_date =findViewById(R.id.start_date_event_detail);
        end_date = findViewById(R.id.end_date_event_detail);
        event_contents = findViewById(R.id.contents_event_detail);
        poster = findViewById(R.id.poster_event_detail);


        Log.d("이벤트 id", event_id);
        sendRequest();





    }


    //볼리 통신으로 이벤트 아이디 보내기
    private void sendRequest() {
        String url = URL_read_detail_event;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("event");
                            //feed 어레이 풀기
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String event_title = object.getString("title");
                                String event_start = object.getString("event_start_date");
                                String event_end = object.getString("event_end_date");
                                String num_people = object.getString("num_people");
                                String contents = object.getString("contents");
                                String img = object.getString("img");
                                String event_id = object.getString("event_id");

//                                eventList.add(new DataEvent(img,event_title,contents,event_start,event_end,num_people,event_id));
//
//                                Log.e("eventList", eventList.toString());
//                                Log.e("eventList=event_title", event_title);
                                title_event.setText(event_title);
                                num_of_people.setText(num_people);
                                start_date.setText(event_start);
                                end_date.setText(event_end);
                                event_contents.setText(contents);

                                Glide.with(getApplicationContext()) //해당 환경의 Context나 객체 입력
                                    .load(img) //URL, URI 등등 이미지를 받아올 경로
                                    .centerCrop()
                                    .into(poster); //받아온 이미지를 받을 공간(ex. ImageView)




                            }





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("soo1", "응답 -> " + response);
                    }


                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){

                        Log.d("soo1", "이벤트 에러 -> " + error.getMessage());

                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("event_id",event_id);
                Log.d("이벤트 아이디 전송 성공", event_id);


                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }







}
