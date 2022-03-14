package com.example.frame;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.EventRegisterAdapter;
import com.example.frame.adapter.EventWinnerListAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataRegister;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//이벤트 당첨자 띄우는 어댑터
public class EventWinnerListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private ArrayList<DataRegister> entry = new ArrayList<>();
    private EventWinnerListAdapter adapter;
    private String event_id,event_title,user_name,user_id,winner_id,success;
    private TextView btn_send_notice;
    private ArrayList<String> winnerList;
    private CheckBox checkBox;
    private TextView event_title_tv;
    private static String URL_read_winner_list ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_winner_list.php";
    String send_check;
    MyService mService;
    boolean eBound = false;



    /**
     * Defines callbacks for service binding, passed to bindService()
     */
//    private ServiceConnection connection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            MyService.LocalBinder binder = (MyService.LocalBinder) service;
//            mService = binder.getService();
//            eBound = true;
//            Log.e("EWL", "eBound true 서비스 연결됨. ");
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            eBound = false;
//        }
//    };


    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_winner_list);
        winnerList = new ArrayList();

        event_title_tv = findViewById(R.id.winner_event_title_tv);
        //checkBox = findViewById(R.id.checkbox_all_check);
        //btn_send_notice = findViewById(R.id.btn_send_notice);

        Intent intent = getIntent();
        event_id = intent.getExtras().getString("event_id"); // 인텐트로 넘긴 event_id
//        winner_id = intent.getExtras().getString("winnerList"); // 인텐트로 넘긴 event_id
        Log.d("EWL event_id", " 당첨자 event_id : " + event_id);
        Log.d("EWL winner_id", " 당첨자 winner_id : " + winner_id);

//        success = intent.getExtras().getString("sendSuccess");
        Log.d("EWL sendSuccess", "  sendSuccess : " + success);

        //8.다중선택리사이클러뷰
        recyclerView = findViewById(R.id.rv_event_winner_list);

        //if(success.equals("false")) {

//            btn.setEnabled(false);
//            checkBox.setEnabled(false);
//            checkBox.setTextColor(getColor(R.color.colorGrey));
//            //checkBox.setBackgroundColor(getResources().getColor(R.color.colorGrey));
//            btn.setBackgroundColor(getResources().getColor(R.color.colorGrey));

//        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //9.다중선택리사이클러뷰
        //CreateListOfData();

        //리사이클러뷰에 당첨자 목록 데이터 띄우기
        sendRequest();



        //adapter = new EventRegisterAdapter(this,entry);
        //recyclerView.setAdapter(adapter);
//        checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (checkBox.isChecked()) {
//
//                    for (DataRegister model : entry) {
//                        model.setChecked(true); //모두 체크
//                    }
//                } else {
//
//                    for (DataRegister model : entry) {
//                        model.setChecked(false);
//                    }
//                }
//
//                adapter.notifyDataSetChanged();
//            }
//        });

        //10.다중선택리사이클러뷰 handling click event on the button
//        btn_send_notice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(adapter.getSelected().size() > 0){
//                    //getting a list of item selected
//                    StringBuilder stringBuilder = new StringBuilder();
////                    winnerList = new ArrayList();
//
//                    for (int i = 0; i < adapter.getSelected().size(); i++){
//                        stringBuilder.append(adapter.getSelected().get(i).getUser_id());
//                        //stringBuilder.append("\n");
//                        winnerList.add(adapter.getSelected().get(i).getUser_id());
//                    }
//
//                    Log.e("winnerList 확인  EWLAct 128", "winnerList : " + winnerList);
//                    //showToast(stringBuilder.toString().trim());
//
//                    String winnerData = new Gson().toJson(winnerList);
//                    Intent intent2 = new Intent(getApplicationContext(), SendTicketActivity.class);
//                    intent2.putExtra("winnerList", winnerData);
//                    intent2.putExtra("event_id", event_id);
//                    intent2.putExtra("cntWinner",  String.valueOf(winnerList.size()));
//
//                    Log.e("EWL", " 전송 완료 ");
//                    startActivity(intent2);
//
//
//
//                    Log.e("EWL", "event_id 전송 : " + event_id);
//                    Log.e("EWL", "winnerList 전송 : " + winnerList);
//                    Log.e("EWL", "cntWinner 전송 : " +  String.valueOf(winnerList.size()));
//
////                    Intent serviceIntent = new Intent(EventWinnerListActivity.this, MyService.class);
////                    bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
////                    startService(serviceIntent);
////
////                    Log.d("winnerList 전송 확인 ", "winnerList 전송 : " + winnerList);
////                    serviceIntent.putExtra("winnerList", String.valueOf(winnerList));
////                    startService(serviceIntent); // 인텐트를 서비스로 보낸다.
////                    Log.e("EWL", "서비스로 데이터  보냄 3143");
//
//
//                }else {
//                    showToast("선택되지 않았습니다.");
//                    Log.e("오류태그", "선택되지 않았습니다.");
//                }
//            }
//        });




    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
       // unbindService(connection);


        eBound = false;
    }

    //당첨자 목록 띄우기
    private void sendRequest() {
        String url = URL_read_winner_list;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("winner");
                            //feed 어레이 풀기
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                event_title = object.getString("title");
                                user_id = object.getString("user_id");
                                user_name = object.getString("user_name");
                                String user_email = object.getString("user_email");
                                send_check = object.getString("send_check");


                                entry.add(new DataRegister(user_id,user_name,user_email,send_check));

                            }

                            event_title_tv.setText(event_title);

                            //리사이클러뷰 클릭 이벤트 - 6. onClick 내에 리스너 set

                            adapter = new EventWinnerListAdapter(getApplicationContext(),entry);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

//                            if(send_check.equals("Y")){
//                                btn_send_notice.setEnabled(false);
//                                checkBox.setEnabled(false);
//                                checkBox.setTextColor(getColor(R.color.colorGrey));
//                                //checkBox.setBackgroundColor(getResources().getColor(R.color.colorGrey));
//                                //btn_send_notice.setBackgroundColor(getResources().getColor(R.color.colorGrey));
//                                btn_send_notice.setText("완료된 이벤트 입니다.");
//                                btn_send_notice.setBackgroundColor(Color.TRANSPARENT);
//                            }

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

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EventWinnerListActivity.this, EventManageActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);    //인텐트 플래그 설정
        startActivity(intent);  //인텐트 이동
        finish();   //현재 액티비티 종료
    }




}





