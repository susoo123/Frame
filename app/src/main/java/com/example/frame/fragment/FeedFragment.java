package com.example.frame.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.android.volley.AuthFailureError;
import com.android.volley.Request;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.AddFeedActivity;
import com.example.frame.AddFeedActivity2;
import com.example.frame.EditProfileActivity;
import com.example.frame.R;
import com.example.frame.adapter.FeedAdapter;
import com.example.frame.adapter.SearchAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataFeed;
import com.example.frame.etc.DataModel;
import com.example.frame.etc.Utils;
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


public class FeedFragment extends Fragment {


   private static String URL_read_feed = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_feed.php";
   private FloatingActionButton btn_add_feed;
   RecyclerView recyclerView;
   private String feed_writer, feed_contents, feed_img, feed_time, feed_profile_img, feed_id, feed_user_id;
   RecyclerView.Adapter adapter;
   private ArrayList<DataFeed> feedList = new ArrayList<>();



    public FeedFragment() {
        // Required empty public constructor
    }


    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        btn_add_feed = view.findViewById(R.id.btn_add_feed);



        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_add_feed :
//                        startActivity(new Intent(getActivity(), AddFeedActivity.class));
                        startActivity(new Intent(getActivity(), AddFeedActivity2.class));
                        break;

                }
            }
        };

        btn_add_feed.setOnClickListener(clickListener);


        sendRequest();
        init_rv_search(view);


        return view;
    }

    private void init_rv_search(View view){
        //리사이클러뷰 관련
        recyclerView = view.findViewById(R.id.rv_feed);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//리사이클러뷰 가로로만드는 코드
        recyclerView.setLayoutManager(layoutManager);

    }

    public void sendRequest() {
        String url = URL_read_feed;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("feed");


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                feed_writer = object.getString("name");
                                feed_contents = object.getString("feed_contents");
                                feed_img = object.getString("feed_img");
                                feed_time = object.getString("feed_date");
                                feed_profile_img = object.getString("profile_img");

                                Log.d("soo", "php에서 json으로 받은 array 값: " + jsonArray);

                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Date date = fm.parse(feed_time);
                                Log.d("soo", "date 값: " + date);
                                beforeTime(date);
                                Log.d("soo", "beforeTime 값: " +  beforeTime(date));

                                feedList.add(new DataFeed(feed_writer,feed_contents,feed_img,beforeTime(date),feed_profile_img));
                            }


                            adapter = new FeedAdapter(getContext(),feedList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
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

                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue 초기화 필수
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
            ret = new SimpleDateFormat("HH:mm").format(date);
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