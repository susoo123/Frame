package com.example.frame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.FeedAdapter;
import com.example.frame.adapter.TicketBoxAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataFeed;
import com.example.frame.etc.DataFeedImg;
import com.example.frame.etc.DataTicket;
import com.example.frame.etc.SessionManager;
import com.example.frame.fragment.NoTicketFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TicketBoxActivity extends AppCompatActivity {

    private RecyclerView rv_ticketbox;
    NoTicketFragment noTicketFragment;
    private static String URL_read_ticket = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_ticket_box.php";
    RecyclerView recyclerView;
    private String user_id, event_id_ticket, event_start_date_ticket, event_end_date_ticket, ticket_img_ticket, event_title_ticket;
    RecyclerView.Adapter adapter;
    private ArrayList<DataTicket> ticketArrayList = new ArrayList<>();
    private JSONArray imagejArray = new JSONArray();
    private ArrayList<DataFeedImg> imgDataArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketbox);
        recyclerView = findViewById(R.id.rv_ticketbox);

    ////////티켓이 있을 때 리사이클러뷰 띄우고

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String,String> user = sessionManager.getUserDetail();

         user_id = user.get(sessionManager.ID);


            sendRequest();

        Log.d("디버그태그", "ticketArrayList 확인1. ");
//            init_rv_search();
//
//        noTicketFragment = new NoTicketFragment();
//        getSupportFragmentManager().beginTransaction().hide(noTicketFragment).commit();




//        if(ticketArrayList.isEmpty()) {
//            ///////티켓이 없으면 프래그먼트를 띄운다.
//            Log.d("디버그태그", "ticketArrayList가 비었습니다. ");
//            noTicketFragment = new NoTicketFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.ticketbox_container, noTicketFragment).commit();
//        }else {
//            Log.d("디버그태그", "ticketArrayList is not null. ");
//            init_rv_search();
//
//            noTicketFragment = new NoTicketFragment();
//            getSupportFragmentManager().beginTransaction().remove(noTicketFragment).commit();
//
//            Log.d("디버그태그", "ticketArrayList 확인2. ");
//        }

        Log.d("디버그태그", "ticketArrayList 확인3. ");

    }

    private void init_rv_search(){
        //리사이클러뷰 관련

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//리사이클러뷰 세로로 코드
        recyclerView.setLayoutManager(layoutManager);

    }

    //볼리로 요청 보내기
    public void sendRequest() {
        String url = URL_read_ticket;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("ticket"); //"feed"라는 jsonArray를 php에서 받음

                            //feed 어레이 풀기
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                event_id_ticket = object.getString("event_id");
                                event_start_date_ticket = object.getString("event_start_date");
                                event_end_date_ticket = object.getString("event_end_date");
                                event_title_ticket = object.getString("title");

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


                                event_id_ticket = object.getString("event_id");
                                event_start_date_ticket = object.getString("event_start_date");
                                event_end_date_ticket = object.getString("event_end_date");
                                event_title_ticket = object.getString("title");

                                Log.d("soo", "php에서 json으로 받은 array 값: " + jsonArray);
                                Log.d("soo", "php에서 json으로 받은 imageArray 값: " + imagejArray);
                                Log.d("soo", "imgDataArray 값: " + imgDataArray);


                                //feed_uid = feed_id 임!!
                                ticketArrayList.add(new DataTicket(event_id_ticket,event_start_date_ticket,event_end_date_ticket,imgDataArray,event_title_ticket));

                            }


                            //리사이클러뷰 클릭 이벤트 - 6. onClick 내에 리스너 set
                            //인텐트로 피드상세액티비티로 이동하기 위함(리사이클러뷰 아이템 클릭시)
                            adapter = new TicketBoxAdapter(getApplicationContext(),TicketBoxActivity.this,ticketArrayList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if(ticketArrayList.isEmpty()) {
                                ///////티켓이 없으면 프래그먼트를 띄운다.
                                Log.d("디버그태그", "ticketArrayList가 비었습니다. ");
                                noTicketFragment = new NoTicketFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.ticketbox_container, noTicketFragment).commit();
                            }else {
                                Log.d("디버그태그", "ticketArrayList is not null. ");
                                init_rv_search();

                                noTicketFragment = new NoTicketFragment();
                                getSupportFragmentManager().beginTransaction().remove(noTicketFragment).commit();

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("soo1", "응답 -> " + response);
                    }


                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }




                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                Log.d("디버그태그", "티켓함에서 user_id 확인 : " + user_id);

                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext()); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }













}
