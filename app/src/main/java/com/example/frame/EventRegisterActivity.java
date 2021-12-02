package com.example.frame;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.EventAdapter;
import com.example.frame.adapter.EventRegisterAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataEvent;
import com.example.frame.etc.DataRegister;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//이벤트 응모자 띄우는 액티비티
public class EventRegisterActivity extends AppCompatActivity {

    //다중선택리사이클러뷰 7.
    RecyclerView recyclerView;
    private ArrayList winnerList = new ArrayList<>();
    private ArrayList<DataRegister> entry = new ArrayList<>();
    private EventRegisterAdapter adapter;
    private String event_id,event_title,user_name,user_id,winner_id;
    private Button btn;
    private TextView event_title_tv;
    private static String URL_read_register ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_register.php";
    private static String URL_send_winner="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/update_register_picked.php";
    //private ArrayList<DataRegister> dataRegisters = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_register);
        event_title_tv = findViewById(R.id.register_event_title_tv);

        Intent intent = getIntent();
        event_id = intent.getExtras().getString("event_id"); // 인텐트로 넘긴 event_id

        //8.다중선택리사이클러뷰
        recyclerView = findViewById(R.id.rv_register);
        btn = findViewById(R.id.btn_entry);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        //9.다중선택리사이클러뷰
        //CreateListOfData();
        sendRequest();

        //adapter = new EventRegisterAdapter(this,entry);
        //recyclerView.setAdapter(adapter);

        //10.다중선택리사이클러뷰 handling click event on the button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getSelected().size() > 0){
                    //getting a list of item selected
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < adapter.getSelected().size(); i++){
                        //stringBuilder.append(adapter.getSelected().get(i).getUser_id());
                        //stringBuilder.append("\n");
                        //winner_id = stringBuilder.toString().trim();
                        winner_id = adapter.getSelected().get(i).getUser_id();
                        winnerList.add(winner_id);

                    }




                    //당첨자 선택 버튼 누르면 DB register_event의 picked가 Y로 변경되어야함.
                    send_winner();
                    //당첨자 선택 버튼 클릭하면 당첨자 목록 액티비티로 넘어감(EventWinnerListActivity)
                    Intent intent = new Intent(getApplicationContext(), EventWinnerListActivity.class);
                    intent.putExtra("event_id",event_id);
                    intent.putExtra("winner_id",winner_id);
//                    intent.putExtra("winner_id",stringBuilder.toString().trim());
                    startActivity(intent);
                    //showToast(stringBuilder.toString().trim());
                }else {
                    showToast("선택되지 않았습니다.");
                    Log.e("오류태그", "선택되지 않았습니다.");
                }
            }
        });



    }

    private void send_winner() {

        String url = URL_send_winner;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("picked");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
//                                event_title = object.getString("title");
//                                user_id = object.getString("user_id");
//                                user_name = object.getString("user_name");
//                                String user_email = object.getString("user_email");
//
//
//                                entry.add(new DataRegister(user_id,user_name,user_email));

                            }

//                            event_title_tv.setText(event_title);
//
//                            //리사이클러뷰 클릭 이벤트 - 6. onClick 내에 리스너 set
//                            adapter = new EventRegisterAdapter(getApplicationContext(),entry);
//                            recyclerView.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();

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
                //params.put("winner_id",winner_id);


                String winnerData = new Gson().toJson(winnerList);
                params.put("winnerList", winnerData);
                params.put("cntwinner", String.valueOf(winnerList.size()));//배열크기( 개수 보내기)
                Log.e("winnerList 로그확인", winnerData);
                Log.e("cntwinner 로그확인", String.valueOf(winnerList.size()));


                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);




    }

    //9.다중선택리사이클러뷰
//    private void CreateListOfData() {
//
//        entry = new ArrayList<>();
//
//        for (int i = 0; i < 20; i++){
//            DataRegister dataRegister = new DataRegister();
//            dataRegister.setUser_id("userName" + (i+1));
//
////            //showing at least one selection
////            if(i == 0){
////                dataRegister.setChecked(true);
////            }
//
//            entry.add(dataRegister);
//
//        }
//
//        adapter.setEntry(entry);
//
//    }

    private void sendRequest() {
        String url = URL_read_register;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("register");
                            //feed 어레이 풀기
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                event_title = object.getString("title");
                                user_id = object.getString("user_id");
                                user_name = object.getString("user_name");
                                String user_email = object.getString("user_email");


                                entry.add(new DataRegister(user_id,user_name,user_email));

                            }

                            event_title_tv.setText(event_title);

                            //리사이클러뷰 클릭 이벤트 - 6. onClick 내에 리스너 set
                            adapter = new EventRegisterAdapter(getApplicationContext(),entry);
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
                params.put("event_id",event_id);


                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }



    private void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();

    }

}
