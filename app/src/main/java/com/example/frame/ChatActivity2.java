package com.example.frame;
import android.os.Message;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.ChatItemAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataChat;
import com.example.frame.etc.DataChatItem;
import com.example.frame.etc.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity2 extends AppCompatActivity {
    TextView textView;
    String UserID, user_id, UserName;
    private String chat_date, chat_id, user_id_chat, chat_text, name, receiver, type;
    private String chat_date2, chat_id2, user_id_chat2, chat_text2, name2, receiver2, type2;
    Button connectbutton;
    Button chatbutton;
    RecyclerView chatView;
    EditText inputChat;
    String sendmsg;

   // private ChatItemAdapter adapter;
    private ImageView btn_camera;
    SessionManager sessionManager;
    Bitmap bitmap;
    private String encodeImageString;

    private ArrayList<DataChat> dataList = new ArrayList<>();
    private ArrayList<DataChatItem> dataChat = new ArrayList<>();
    private ArrayList<DataChatItem> dataChat2 = new ArrayList<>();
    ArrayList readArray = new ArrayList<>();

    private ArrayList<Uri> feedImgArrayList = new ArrayList<Uri>();
    private ArrayList<Uri> urlList = new ArrayList<>();
    private static String URL = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/send_img_chat.php";
    boolean cBound;
    boolean isService = false; // 서비스 중인 확인용

    MyService myService;



    private static String URL_read_chat = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_chat.php";


    JsonObject joClientInfo;
    Intent socketIntent;
    String read,chatTextData;
    Intent intent;

    InputStream writer;
    OutputStream sender;
    InputStreamReader streamReader;

    private Handler mHandler;
    private Handler mHandler2, handler3;
    MyService mService;
//    boolean mBound = false;

    PrintWriter sendWriter;
    RecyclerView.Adapter adapter;
    JSONObject jsonObject;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        textView = (TextView) findViewById(R.id.chat_user_name);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //btn_camera = findViewById(R.id.chat_camera_btn);

        inputChat = (EditText) findViewById(R.id.message);
        chatbutton = (Button) findViewById(R.id.chatbutton);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetail();

        UserName = user.get(sessionManager.NAME);
        UserID = user.get(sessionManager.ID); //접속한 유저의 아이디
        textView.setText("1:1 문의사항");
        Log.d("UserID", UserID);
        mHandler2 = new Handler();


        chatView = findViewById(R.id.chatRView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        chatView.setLayoutManager(layoutManager);

        adapter = new ChatItemAdapter(ChatActivity2.this, dataChat);
        chatView.setAdapter(adapter);



        //관리자 계정인지 구분 후  관리자가 클릭한 방의 유저 아이디를 인텐트로 받아온다.
        if (UserID.equals("50")) { //만약 관리자 계정이면?
            Intent intent = getIntent(); //인텐트로 받아온다.
            user_id = intent.getExtras().getString("user_id"); //관리자가 클릭한 방의 유저 아이디를
        }

        //이미 저장된 데이터 가져오기 (볼리+)
        gettingChat();

        //소켓 연결
        intent = new Intent(ChatActivity2.this, MyService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        startService(intent);

        Log.e("CA2", "소켓 연결 성공"); //여기까지 작동.....

        cBound = true;

        Log.e("CA2", "176");



        //보내기 버튼을 누른다.
        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendmsg = inputChat.getText().toString();//edittext에 적은 채팅 내용 스트링으로 가져오기
                Log.e("chatAct", "보내기 버튼 클릭  ");
                new Thread(){

                    @Override
                    public void run() {
                        Log.e("chatAct", " 스레드 실행 ");
                        try {
                            //화면에 바로 업데이트 하고


                            JsonObject joClientInfo = new JsonObject(); //제이슨 객체를 만든다.
                            Log.e("chatAct", " 225 ");

                            //제이슨 객체에 각 정보를 넣는다.
                            joClientInfo.addProperty("user_id_chat", UserID);
                            Log.e("chatAct", " 232  ");
                            joClientInfo.addProperty("chat_text", sendmsg);
                            joClientInfo.addProperty("type", "0");//text
                            joClientInfo.addProperty("chat_date", getTime());
                            joClientInfo.addProperty("name", UserName);

                            Log.e("chatAct", "json 저장 전 ");
                            //텍스트 저장 시킬 때
                            if (UserID.equals("50")) { //관리자 계정이면
                                joClientInfo.addProperty("receiver", user_id);//메세지 받는 사람을 저장하고
                            } else {//관리자 계정이 아니면
                                joClientInfo.addProperty("receiver", "50"); //메세지 받는 사람은 무조건 관리자
                            }
                            Log.e("chatAct", "json 저장 후 " + joClientInfo);

//                            String user_id_chat = joClientInfo.get("user_id_chat").toString();
//                            String chat_text = joClientInfo.get("chat_text").toString();
//                            String chat_date = joClientInfo.get("chat_date").toString();
//                            String name = joClientInfo.get("name").toString();
//                            String receiver = joClientInfo.get("receiver").toString();
//                            String type = joClientInfo.get("type").toString();

                            DataChatItem dataChatItem = new DataChatItem(sendmsg, UserName, getTime(), receiver, type, 2);
                            Log.d("디버그태그","dataChatItem" + dataChatItem.getItem_chat_content() );


                            sendWriter = new PrintWriter(mService.Ssocket.getOutputStream());
                            sendWriter.println(joClientInfo); //제이슨 객체
                            sendWriter.flush();//서버로 보내짐
                            Log.d("서버로 채팅 보내기 성공", joClientInfo.toString());//동작함..!!

                            inputChat.setText(""); //입력창 비움

                            //키보드 올라오는 기능 관련
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(inputChat.getWindowToken(), 0);

                            mHandler2.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("오류태그", "dataChatItem : " + dataChatItem);
                                    dataChat.add(dataChatItem);
                                    adapter.notifyItemInserted(dataChat.size() - 1);
                                    chatView.scrollToPosition(dataChat.size() - 1);
                                }
                            }); //보낸 채팅 화면에 띄우는 핸들러




                            //dataChat.add(new DataChatItem(chat_text, UserName, chat_date , user_id ,type, 1));




                        } catch (Exception e) {


                        }
                    }//close run()

                }.start();


            }
        });


    }



    //서비스가 이미 실행되고 있을 때
    @Override
    protected void onNewIntent(Intent intent){
        //새로 저장된 채팅 가져오기
        getNewData();

        super.onNewIntent(intent);
    }

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
            isService = true;
            Log.i("CA2 115", "서비스 연결됨. ");


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isService = false;
            Log.i("CA2 115", "서비스 연결 끊기. ");
        }
    };

    //시각 나타내는 메소드
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = dateFormat.format(date);
        return getTime;
    }

    //이미 저장된 데이터 가져오기
    public void gettingChat(){
        String url = URL_read_chat;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("chat"); //"feed"라는 jsonArray를 php에서 받음

                            //feed 어레이 풀기
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                chat_id = object.getString("chat_id");
                                user_id_chat = object.getString("user_id_chat");
                                chat_text = object.getString("chat_text");
                                chat_date = object.getString("chat_date");
                                name = object.getString("name");
                                receiver = object.getString("receiver");
                                type = object.getString("type");

                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                                //관리자 계정이 아닐 때 리사이클러뷰에 필요한 데이터 받기
                                if (!UserID.equals("50")) {
                                    if (name.equals(UserName)) { //user가 쓴 채팅이면
                                        dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));

                                    } else if (user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓰고, 받는 사람이 user
                                        dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
                                    }
                                }else{
                                    //관리자 계정일 때 데이터 받기
                                    if(receiver.equals(user_id) && name.equals(UserName)){ //초록색
                                        dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
                                    } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) { //보라색
                                        dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
                                    }

                                }



                            }

                            System.out.println("dataChat 확인  : " + dataChat);



                            adapter = new ChatItemAdapter(ChatActivity2.this, dataChat);
                            Log.e("CA2", " 7 ");
                            chatView.setAdapter(adapter); //
//                            Log.e("CA2", " 8 ");
//                            adapter.notifyDataSetChanged();
//                            Log.e("CA2", " 9 ");

                            adapter.notifyItemInserted(dataChat.size() -1);
                            chatView.scrollToPosition(dataChat.size() -1);


                            //mHandler.post(new ChatActivity.getDataTH(dataChat));


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("soo1", "응답1 -> " + e.getMessage());
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

                params.put("user_id", UserID);
                Log.e("CA", "user_id : " + UserID);


                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
    }


    //새롭게 저장된 데이터 가져오기
    public void getNewData(){

        new Thread(){
            public void run(){
                Log.e("CA2", "188");

                try{

                    Log.e("CA2", "194");

//                   while (true) {
                   // Log.e("CA2 ", "459 내용 : " + mService.ChatObject.toString());

                    String read2 = mService.read2;
                    Log.d("CA2", "397 read2 디비에 새로 저장된 정보 확인 해보기 : " + read2);//새로 저장된 정보 맞음!!

                    JSONArray ja = new JSONArray(read2);
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jsonObject = ja.getJSONObject(i);
                        user_id_chat = jsonObject.getString("user_id_chat");
                        chat_text = jsonObject.getString("chat_text");
                        chat_date = jsonObject.getString("chat_date");
                        name = jsonObject.getString("name");
                        receiver = jsonObject.getString("receiver");
                        type = jsonObject.getString("type");

                        //관리자 계정이 아닐 때 리사이클러뷰에 필요한 데이터 받기
                        if (!UserID.equals("50")) {
                            if (name.equals(UserName)) { //user가 쓴 채팅이면
                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));

                            } else if (user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓰고, 받는 사람이 user
                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
                            }
                        } else {
                            //관리자 계정일 때 데이터 받기
                            if (receiver.equals(user_id) && name.equals(UserName)) { //초록색
                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
                            } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) { //보라색
                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
                            }

                        }
                    }

                       Log.e("CA2", " 533");

//                    JSONObject jsonObject = mService.ChatObject;
//
//                        Log.e("CA2 ", "read2 JSONObject 내용 : " + jsonObject.toString());
//
//                        user_id_chat = jsonObject.getString("user_id_chat");
//                        chat_text = jsonObject.getString("chat_text");
//                        chat_date = jsonObject.getString("chat_date");
//                        name = jsonObject.getString("name");
//                        receiver = jsonObject.getString("receiver");
//                        type = jsonObject.getString("type");
//
//                        //관리자 계정이 아닐 때 리사이클러뷰에 필요한 데이터 받기
//                        if (!UserID.equals("50")) {
//                            if (name.equals(UserName)) { //user가 쓴 채팅이면
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//
//                            } else if (user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓰고, 받는 사람이 user
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                            }
//                        } else {
//                            //관리자 계정일 때 데이터 받기
//                            if (receiver.equals(user_id) && name.equals(UserName)) { //초록색
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//                            } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) { //보라색
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                            }
//
//                        }



//                    adapter = new ChatItemAdapter(ChatActivity2.this, dataChat);
//                    Log.e("CA2", " 7 ");
//                    chatView.setAdapter(adapter); //셋 어댑터가 안됨!!!
//                    Log.e("CA2", " 8 ");
//                    adapter.notifyDataSetChanged();

//                     adapter = new ChatItemAdapter(ChatActivity2.this, dataChat);
//                     chatView.setAdapter(adapter);
//                     adapter.notifyItemInserted(dataChat.size() - 1);
//                     chatView.scrollToPosition(dataChat.size() - 1);
                    mHandler2.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("오류태그", "dataChatItem : " + dataChat);

                            adapter.notifyItemInserted(dataChat.size() - 1);
                            chatView.scrollToPosition(dataChat.size() - 1);
                            //adapter.notifyDataSetChanged();
//                                    Log.e("오류태그", "dataChat size : " + dataChat.size());
//                                    chatView.scrollToPosition(dataChat.size() - 1);
                        }
                    }); //보낸 채팅 화면에 띄우는 핸들러



//                    } //close

                }catch (Exception e){

                }
            }


        }.start();

    }

    //새롭게 저장된 데이터 가져오기
    public void getNewData2(){

        new Thread(){
            public void run(){
                Log.e("CA2", "188");

                try{

                    Log.e("CA2", "194");

                    while (true) {

                    String read = mService.read2;
                    //JSONObject jsonObject = mService.ChatObject;
                    JSONArray ja = new JSONArray(read);
                    jsonObject = ja.getJSONObject(0);

                    Log.e("CA2 ", "read2 JSONObject 내용 : " + jsonObject.toString());

                    user_id_chat = jsonObject.getString("user_id_chat");
                    chat_text = jsonObject.getString("chat_text");
                    chat_date = jsonObject.getString("chat_date");
                    name = jsonObject.getString("name");
                    receiver = jsonObject.getString("receiver");
                    type = jsonObject.getString("type");

                    //관리자 계정이 아닐 때 리사이클러뷰에 필요한 데이터 받기
                        if (UserID != "50") {
                            if (name.equals(UserName)) { //user가 쓴 채팅이면
                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));

                            } else if (user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓰고, 받는 사람이 user
                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
                            }
                        }

                        //관리자 계정일 때 데이터 받기
                        if (receiver.equals(user_id)) {//초록색
                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
                        } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) {
                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
                        }

                        //데이터 그대로 가져와서 화면에 업데이트 (내화면 업데이트)
                        //  mHandler.post(new getDataTH(dataChat));


                        //해당 소켓스트림이 해당하는 화면을 업데이트 한다.
                       // mHandler2.post(new ChatActivity2.getData2(dataChat));
                    adapter = new ChatItemAdapter(ChatActivity2.this, dataChat);
                    Log.e("CA2", " 7 ");
                    chatView.setAdapter(adapter); //셋 어댑터가 안됨!!!
                    Log.e("CA2", " 8 ");
                    adapter.notifyDataSetChanged();
                    Log.e("CA2", " 9 ");
                     //mHandler2.post(new getData2(dataChat));
//                        ChatItemAdapter chatItemAdapter = new ChatItemAdapter(ChatActivity2.this, dataChat);
//                        chatView.setAdapter(chatItemAdapter);
//                        chatItemAdapter.notifyItemInserted(dataChat);
//                        //chatItemAdapter.notifyItemInserted(dataChatItemArrayList.size() - 1);
//                        chatView.scrollToPosition(dataChat.size() - 1);

                    } //close

                }catch (Exception e){

                }
            }


        }.start();

    }



    // 내가 보낸 텍스트 바로 화면에 반영하는 핸들러  (메세지 업데이트)
    class getData2 implements Runnable {
        private ArrayList<DataChatItem> dataChatItemArrayList;
        private RecyclerView chatView2;

        public getData2(ArrayList<DataChatItem> dataChat) {
            this.dataChatItemArrayList = dataChat;
        }


        @Override
        public void run() {

//            ChatItemAdapter chatItemAdapter = new ChatItemAdapter(ChatActivity2.this, dataChatItemArrayList);
//            chatView.setAdapter(chatItemAdapter);
//            chatItemAdapter.notifyDataSetChanged();
            adapter.notifyItemInserted(dataChatItemArrayList.size());
            chatView.scrollToPosition(dataChatItemArrayList.size() - 1);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       if(isService){
           unbindService(connection);
           isService = false;
       }
    }


}


//관리자 계정이 아닐 때 리사이클러뷰에 필요한 데이터 받기
//                        if (!UserID.equals("50")) {
//                            if (name.equals(UserName)) { //user가 쓴 채팅이면
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//
//                            } else if (user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓰고, 받는 사람이 user
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                            }
//                        }
//                        //관리자 계정일 때 데이터 받기
//                        if (receiver.equals(user_id) && user_id_chat.equals("50")) {//초록색 받는사람이 관리자가 클릭한 유저면
//                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//                        } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) { //리시버는 관리자이고 유저아이디가 클릭한 유저아이디랑 같으면
//                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                        }



///////////////////////////////////////////////////////////
//                                if (receiver.equals(user_id) && UserID.equals("50")) {//초록색 user_id 클릭한 방 user_id
//                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//                                } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) {
//                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                                }
//
//
