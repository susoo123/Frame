package com.example.frame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.ChatRoomListAdapter;
import com.example.frame.adapter.EventAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataChat;
import com.example.frame.etc.DataEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private static String URL_read_chat_room ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_chat_room_list.php";
    private ArrayList<DataChat> dataChatArrayList = new ArrayList<>();
    RecyclerView.Adapter adapter;
    private ChatRoomListAdapter.RecyclerViewClickListener clickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);

        sendRequest();
        init_rv_chat_list();
    }

    private void init_rv_chat_list(){
        //리사이클러뷰 관련
        recyclerView = findViewById(R.id.chat_room_list_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//리사이클러뷰 세로로만드는 코드
        recyclerView.setLayoutManager(layoutManager);

    }

    private void sendRequest() {
        String url = URL_read_chat_room;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("chatList");
                            //feed 어레이 풀기
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String user_id = object.getString("user_id");
                                String user_name = object.getString("name");
                                String chat_user_img = object.getString("profile_img");
                                String chat_text = object.getString("chat_text_latest");
                                String chat_date = object.getString("chat_date");

                                dataChatArrayList.add(new DataChat(user_id,user_name,chat_user_img,chat_text,chat_date));

                            }

                            setOnClickListener();
                            //리사이클러뷰 클릭 이벤트 - 6. onClick 내에 리스너 set
                            adapter = new ChatRoomListAdapter(getApplicationContext(),dataChatArrayList,clickListener);
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

                        Log.d("soo1", "chatRoomList 에러 -> " + error.getMessage());

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
        clickListener = new ChatRoomListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("user_id",dataChatArrayList.get(position).getUser_id());
                startActivity(intent);
            }
        };
    }







}
