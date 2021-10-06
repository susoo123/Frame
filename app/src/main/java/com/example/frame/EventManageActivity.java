package com.example.frame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.EventAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventManageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private static String URL_read_event ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_event.php";
    private ArrayList<DataEvent> eventList = new ArrayList<>();
    RecyclerView.Adapter adapter;
    private EventAdapter.RecyclerViewClickListener clickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_management);

        sendRequest();
        init_rv_search();
    }

    private void init_rv_search(){
        //리사이클러뷰 관련
        recyclerView = findViewById(R.id.rv_event);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

    }
    private void sendRequest() {
        String url = URL_read_event;
        StringRequest request = new StringRequest(Request.Method.GET, url,
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

                                eventList.add(new DataEvent(img,event_title,contents,event_start,event_end,num_people,event_id));

                            }

                            setOnClickListener();
                            //리사이클러뷰 클릭 이벤트 - 6. onClick 내에 리스너 set
                            adapter = new EventAdapter(getApplicationContext(),eventList,clickListener);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

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

                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }

    private void setOnClickListener() {
        clickListener = new EventAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), EventRegisterActivity.class);
                intent.putExtra("event_id",eventList.get(position).getEvent_id());
                startActivity(intent);
            }
        };
    }


}
