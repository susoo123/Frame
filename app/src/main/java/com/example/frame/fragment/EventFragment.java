package com.example.frame.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.AddEventActivity;
import com.example.frame.DetailEventActivity;
import com.example.frame.DetailFeedActivity;
import com.example.frame.EventRegisterActivity;
import com.example.frame.R;
import com.example.frame.adapter.EventAdapter;
import com.example.frame.adapter.FeedAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataEvent;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class EventFragment extends Fragment {
    FloatingActionButton btn_add_event;
    RecyclerView recyclerView;
    private static String URL_read_event ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_event.php";
    private ArrayList<DataEvent> eventList = new ArrayList<>();
    RecyclerView.Adapter adapter;
    private EventAdapter.RecyclerViewClickListener clickListener;
    SessionManager sessionManager;
    private String role;


    public EventFragment() {
        // Required empty public constructor
    }


    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();

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
       View view = inflater.inflate(R.layout.fragment_event, container, false);

       //버튼 보이고, 안보이고...!!
        sessionManager = new SessionManager(getContext());
        HashMap<String,String> user = sessionManager.getUserDetail();
        role = user.get(sessionManager.ROLE);
       //만약 쉐어드 role이 admin 이면 버튼 보이고, 그렇지 않으면 버튼 보이지 않는다.
        btn_add_event = view.findViewById(R.id.btn_add_event);
        if(role.equals("admin")){
            btn_add_event.setVisibility(View.VISIBLE);
        }else {
            btn_add_event.setVisibility(View.GONE);
        }

       btn_add_event.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getActivity(), AddEventActivity.class));
           }
       });


        sendRequest();
        init_rv_search(view);


        return view;
    }

    private void init_rv_search(View view){
        //리사이클러뷰 관련
        recyclerView = view.findViewById(R.id.rv_event);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

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


                                //feed_img = object.getString("imageArray");
                               // imagejArray = object.getJSONArray("imageArray"); //json어레이 안에 들 이미지 어레이(얘도 jsonArray)

//                                //jsonArray를 ArrayList에 담기 위해
//                                if(imagejArray !=null){
//                                    imgDataArray = new ArrayList<>();
//                                    for (int j = 0; j < imagejArray.length(); j++){
//                                        // imgDataArray.add(imagejArray.getString(j));
//
//                                        //이미지를 담을 리사이클러뷰(피드 내 이미지 슬라이더 기능) model class에 담아줌.
//                                        imgDataArray.add(new DataFeedImg(imagejArray.getString(j)));
//                                    }
//
//                                }


                                eventList.add(new DataEvent(img,event_title,contents,event_start,event_end,num_people,event_id));

                            }

                            setOnClickListener();
                            //리사이클러뷰 클릭 이벤트 - 6. onClick 내에 리스너 set
                            adapter = new EventAdapter(getContext(),eventList,clickListener);
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
        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }

    private void setOnClickListener() {
        clickListener = new EventAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getActivity(),DetailEventActivity.class);
                intent.putExtra("event_id",eventList.get(position).getEvent_id());
                startActivity(intent);
            }
        };
    }




}