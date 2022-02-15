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
    private String event_id,event_title,user_name,user_id,winner_id;
    private Button btn;
    private ArrayList<String> winnerList;
    private TextView event_title_tv;
    private static String URL_read_winner_list ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_winner_list.php";

    MyService mService;
    boolean eBound = false;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            eBound = true;
            Log.e("EWL", "eBound true 서비스 연결됨. ");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            eBound = false;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_winner_list);

        event_title_tv = findViewById(R.id.winner_event_title_tv);

        Intent intent = getIntent();
        event_id = intent.getExtras().getString("event_id"); // 인텐트로 넘긴 event_id
        winner_id = intent.getExtras().getString("winner_id"); // 인텐트로 넘긴 event_id
        Log.d("event_id", " 당첨자 event_id : " + event_id);
        Log.d("winner_id", " 당첨자 winnerList : " + winnerList);

        //8.다중선택리사이클러뷰
        recyclerView = findViewById(R.id.rv_event_winner_list);
        btn = findViewById(R.id.btn_send_notice);

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
                    winnerList = new ArrayList();

                    for (int i = 0; i < adapter.getSelected().size(); i++){
                        stringBuilder.append(adapter.getSelected().get(i).getUser_id());
                        //stringBuilder.append("\n");
                        winnerList.add(adapter.getSelected().get(i).getUser_id());
                    }

                    Log.d("winnerList 확인 ", "winnerList : " + winnerList);
                    //showToast(stringBuilder.toString().trim());


                    //Intent intent = new Intent(getApplicationContext(), SendTicketActivity.class);
                    //intent.putExtra("winnerList", winnerList);

                    //startActivity(intent);

                    Intent serviceIntent = new Intent(EventWinnerListActivity.this, MyService.class);
                    bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
                    startService(serviceIntent);

                    Log.d("winnerList 전송 확인 ", "winnerList 전송 : " + winnerList);
                    serviceIntent.putExtra("winnerList", String.valueOf(winnerList));
                    startService(serviceIntent); // 인텐트를 서비스로 보낸다.
                    Log.e("EWL", "서비스로 데이터  보냄 3143");


                }else {
                    showToast("선택되지 않았습니다.");
                    Log.e("오류태그", "선택되지 않았습니다.");
                }
            }
        });

        //createNotificationChannel();



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);


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


                                entry.add(new DataRegister(user_id,user_name,user_email));

                            }

                            event_title_tv.setText(event_title);

                            //리사이클러뷰 클릭 이벤트 - 6. onClick 내에 리스너 set
                            adapter = new EventWinnerListAdapter(getApplicationContext(),entry);
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

        //Intent intent = new Intent(EventWinnerListActivity.this, EventService.class);
        //startService(intent);
        //sendNotification();
       // Toast.makeText(getApplicationContext(), "서비스 되니??",Toast.LENGTH_SHORT).show();



    }



    // Channel에 대한 id 생성
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
    private NotificationManager mNotificationManager;

    // Notification에 대한 ID 생성
    private static final int NOTIFICATION_ID = 0;
//
//
//
    //채널을 만드는 메소드
    public void createNotificationChannel()
    {
        //notification manager 생성
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if(android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O){
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                    ,"Test Notification",mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }
//
//    // Notification Builder를 만드는 메소드
//    private NotificationCompat.Builder getNotificationBuilder() {
//        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
//                .setContentTitle("You've been notified!")
//                .setContentText("This is your notification text.")
//                .setSmallIcon(R.drawable.app_logo);
//        return notifyBuilder;
//    }
//
//    // Notification을 보내는 메소드
//    public void sendNotification(){
//        // Builder 생성
//        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
//        // Manager를 통해 notification 디바이스로 전달
//        mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
//    }


}





