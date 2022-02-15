package com.example.frame;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;


//import static org.junit.Assert.*;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;


import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.frame.adapter.ChatItemAdapter;
import com.example.frame.adapter.CommentAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataChat;
import com.example.frame.etc.DataChatItem;
import com.example.frame.etc.DataComment;
import com.example.frame.etc.SessionManager;

import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


//소켓통신(클라이언트)
//1. 클라이언트가 ip/포트번호로 지정된 서버에 연결 시작
//2. OutputStream 사용해 서버에 데이터 전송
//3. InputStream을 사용해 서버에서 데이터 읽기
//4. 연결 종료


public class ChatActivity extends AppCompatActivity {
    private Handler mHandler;
    private Handler mHandler2, handler3;
    MyService mService;
    boolean mBound = false;

   // Socket socket;
    PrintWriter sendWriter, sendReceiver;
    private String ip = "52.79.204.252"; //aws 퍼플릭 ip
    private int port = 6137;
    private static String URL_create_chat = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/create_chat.php";

    private static String serviceData,newTextData;
    TextView textView;
    String UserID, user_id, UserName;
    private String chat_date, chat_id, user_id_chat, chat_text, name, receiver, type;
    private String chat_date2, chat_id2, user_id_chat2, chat_text2, name2, receiver2, type2;
    Button connectbutton;
    Button chatbutton;
    RecyclerView chatView;
    EditText inputChat;
    String sendmsg;
    private ChatItemAdapter adapter;
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
    private boolean mIsBound;


    private static String URL_read_chat = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_chat.php";


    JsonObject joClientInfo;
    Intent socketIntent;
    String read,chatTextData;
    Intent intent;

    InputStream writer;
    OutputStream sender;
    InputStreamReader streamReader;


    @Override
    protected void onNewIntent(Intent intent) {
        //getAlreadyData(intent);    //이거 호출
        //GetNewChatText(intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mHandler = new Handler();
        mHandler2 = new Handler();


        textView = (TextView) findViewById(R.id.chat_user_name);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        btn_camera = findViewById(R.id.chat_camera_btn);


        inputChat = (EditText) findViewById(R.id.message);


        chatbutton = (Button) findViewById(R.id.chatbutton);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetail();

        UserName = user.get(sessionManager.NAME);
        UserID = user.get(sessionManager.ID); //접속한 유저의 아이디
        textView.setText("1:1 문의사항");
        Log.d("UserID", UserID);



        //관리자 계정인지 구분 후  관리자가 클릭한 방의 유저 아이디를 인텐트로 받아온다.
        if (UserID.equals("50")) { //만약 관리자 계정이면?
            Intent intent = getIntent(); //인텐트로 받아온다.
            user_id = intent.getExtras().getString("user_id"); //관리자가 클릭한 방의 유저 아이디를
        }

        //채팅 관련 리사이클러뷰
        chatView = findViewById(R.id.chatRView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatView.setLayoutManager(layoutManager);

        //이미 저장되어 있는 데이터 volley+로 가져오기
        //gettingChat();

        //소켓 연결하기(서비스로)
        intent = new Intent(ChatActivity.this, MyService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        startService(intent);
        Log.e("CA", "소켓 연결 성공"); //여기까지 작동.....



        //채팅 보내기 버튼을 클릭
        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("chatAct", "버튼 클릭후 ");


                try {
                    Log.e("chatAct", "215 실행  ");

                    //새로 얻은 text 데이터 얻는 스레드// 스레드가 동작한다.
                    Thread thread = new Thread(){
                        @Override
                        public void run(){
                            try{
                                sendmsg = inputChat.getText().toString();//editText에서 메세지를 string으로 가져온다.
                                joClientInfo = new JsonObject(); //제이슨 객체를 만든다.
                                //제이슨 객체에 각 정보를 넣는다.
                                joClientInfo.addProperty("user_id_chat", UserID);
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
                                Log.e("chatAct", "json 저장 후 ");


                                //Intent intent2 = new Intent(ChatActivity.this,MyService.class);
//                                intent.putExtra("json", String.valueOf(joClientInfo));
//                                startService(intent);
//                                sendWriter = new PrintWriter(mService.Ssocket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
//                                sendWriter.println(joClientInfo); //제이슨 객체
//                                sendWriter.flush();//서버로 보내짐
                                Log.d("joClientInfo를 서버로 채팅 보내기 235 : ",joClientInfo.toString());//여기까지 동작함..!!

                                 user_id_chat = joClientInfo.get("user_id_chat").toString();
                                 chat_text = joClientInfo.get("chat_text").toString();
                                 chat_date = joClientInfo.get("chat_date").toString();
                                 name = joClientInfo.get("name").toString();
                                 receiver = joClientInfo.get("receiver").toString();
                                 type = joClientInfo.get("type").toString();

//                                Log.d("joClientInfo chat_text2 252 : ",chat_text2);
//                                Log.d("joClientInfo chat_date2 252 : ",chat_date);//여기까지 동작함..!!

                                //이건 동작 화면이 동작 안함.
//                                dataChat2.add(new DataChatItem("ㅁㅁㅁ", "예시 * ", "예시 * 날짜", receiver, type, 2));



                                Log.e("CA", "268 까지 동작함. ");


                             dataChat.add(new DataChatItem(chat_text, user_id_chat, chat_date, receiver, type, 2));
//                                dataChat.add(new DataChatItem(chat_text2, user_id_chat2, chat_date2, receiver2, type2, 2));


                                Log.e("CA", "dataChat2 확인 274: " + dataChat.toString());

                                //둘다 화면 업데이트는 되지만 빈 것으로 업데이트 됨다.
                                //mHandler.post(new getDataTH(dataChat));
                               mHandler2.post(new getData2(dataChat));

//                                adapter = new ChatItemAdapter(ChatActivity.this, dataChat2);
//
////                                //chatItemAdapter.notifyItemInserted(dataChatItemArrayList.size() - 1);
////                                chatView.scrollToPosition(dataChatItemArrayList.size() - 1);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
// chatView.setAdapter(adapter);
//                                adapter.notifyItemInserted(dataChat2);
////                                        adapter.notifyDataSetChanged();
//                                    }
//                                });



//                                Log.e("joClientInfo chat_date2 284 : ",chat_date);//여기까지 동작함..!!

                                //화면 업데이트 하고
//

                                //인텐트로 json이란 이름으로 정보가 저장된 제이슨객체를 보낸다.
                                intent.putExtra("json", String.valueOf(joClientInfo));
                                startService(intent); // 인텐트를 서비스로 보낸다.

                                Log.e("CA", "271 까지 동작함. ");// 여기까지 동작함.

                               // GetNewChatText(intent);
                                // 키보드를 내려준다.
                                new Thread() {
                                        @Override
                                        public void run() {
                                            super.run();
                                            inputChat.setText(""); //입력창 비움
                                            //키보드 올라오는 기능 관련
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(inputChat.getWindowToken(), 0);

                                        }
                                    }.start();



                                Thread thread2 = new Thread() {
                                    @Override
                                    public void run() {
                                        try {

                                            InputStreamReader streamReader = new InputStreamReader(mService.Ssocket.getInputStream());
                                            mService.reader = new BufferedReader(streamReader);
                                          while (true) { ///얘를 추가하니까 채팅이 된다.


                                                //String read = reader.readLine();
                                                newTextData = intent.getStringExtra("newTextData"); //새로 추가된 데이터만 ..
                                                Log.e("CA", " 304 서비스로부터 전달받은 데이터3 String : " + newTextData);
                                        if(newTextData != null) {
                                            //try { //채팅 데이터 받기
//                    JSONArray ja = new JSONArray(newTextData);
                                            JSONObject object = new JSONObject(newTextData);
                                            Log.d("CA", "서비스로 받은 object 값: " + object.toString()); //채팅 전체
                                            //                    for (int i = 0; i < ja.length(); i++) {
                                            //                                    for (int i = 0; i < 1; i++) {
                                            //                        JSONObject object = ja.getJSONObject(i);
                                            //                        while(true){
                                            user_id_chat = object.getString("user_id_chat");
                                            chat_text = object.getString("chat_text");
                                            chat_date = object.getString("chat_date");
                                            name = object.getString("name");
                                            receiver = object.getString("receiver");
                                            type = object.getString("type");

                                            Log.e("receiver확인 ", "receiver : " + receiver);

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
                                            mHandler2.post(new getData2(dataChat));
                                        }


//                                    }



//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
                                        }//while 문 close

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
//                }
//
                                    }



                                    };
                                    thread2.start();
//


                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                     };
                    thread.start();







/////////////////////////////채팅 받는 쓰레드///////////////////////////////////////////////
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            super.run();
//                            try {
//
//                               // Log.d("서버로 채팅 보내기 성공", joClientInfo.toString());//동작함..!!
//
//                                    //디비에 있는걸 가져와서 Activity로 데이터를 보내준다.
//                                    InputStreamReader streamReader = new InputStreamReader(mService.Ssocket.getInputStream());
//                                    mService.reader = new BufferedReader(streamReader);
////                                while (true) { ///얘를 추가하니까 채팅이 된다.
//                                    String read2 = mService.reader.readLine();
//                                    Log.d("CA", "read2 서버에서 받아온 정보 확인 해보기 : " + read2);//새로 저장된 정보 맞음!!
//
//                                    JSONArray ja = new JSONArray(read2);
//                                    JSONObject object = ja.getJSONObject(0);
//                                    Log.e("json object 확인하기 269 ", "object : " + object.toString());
//
//                                    user_id_chat = object.getString("user_id_chat");
//                                    chat_text = object.getString("chat_text");
//                                    chat_date = object.getString("chat_date");
//                                    name = object.getString("name");
//                                    receiver = object.getString("receiver");
//                                    type = object.getString("type");
//
//                                    Log.e("receiver확인 ", "receiver 271  : " + receiver);
//
//                                    //관리자 계정이 아닐 때 리사이클러뷰에 필요한 데이터 받기
//                                    if (!UserID.equals("50")) {
//                                        if (name.equals(UserName)) { //user가 쓴 채팅이면
//                                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//
//                                        } else if (user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓰고, 받는 사람이 user
//                                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                                        }
//                                    } else {
//                                        //관리자 계정일 때 데이터 받기
//                                        if (receiver.equals(user_id) && name.equals(UserName)) { //초록색
//                                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//                                        } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) { //보라색
//                                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                                        }
//
//                                    }
//                                    //데이터 그대로 가져와서 화면에 업데이트 (내화면 업데이트)
//                                    //mHandler.post(new getDataTH(dataChat));
//                                    //해당 소켓스트림이 해당하는 화면을 업데이트 한다.
//                                    mHandler2.post(new getData2(dataChat));
//
//                                    Log.e("CA", "295 여기까지 동작 !!");
//
//                                    new Thread() {
//                                        @Override
//                                        public void run() {
//                                            super.run();
//                                            inputChat.setText(""); //입력창 비움
//                                            //키보드 올라오는 기능 관련
//                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                            imm.hideSoftInputFromWindow(inputChat.getWindowToken(), 0);
//
//                                        }
//                                    }.start();
////                                } //close while
//
//                            } catch (IOException | JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                    }.start();


                } catch (Exception  e) {
                    e.printStackTrace();
                }



 ////////////////////////디비 저장후 채팅 바가 변화되어야할 모습 (키보드 내려가기, 채팅 보낸거 없애기)
//                if (sendmsg != null) {
//                    Log.e("CA", "362번째 줄 실행 ");
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            super.run();
//                            inputChat.setText(""); //입력창 비움
//                            //키보드 올라오는 기능 관련
//                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(inputChat.getWindowToken(), 0);
//
//                        }
//                    }.start(); //이것도 다 동작함.


//                    Intent intent = new Intent(ChatActivity.this, SocketService.class);
//                    GetNewChatText(intent);

                    //새로 업데이트 된 텍스트 데이터 핸들러로 화면에 띄우기(내 화면에만 뜸)
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            super.run();
//                            Intent intent = new Intent(ChatActivity.this, SocketService.class);
//                            GetNewChatText(intent);
//
////                            String newTextData = intent.getStringExtra("newTextData");
////                            Log.d("CA", " String newTextData 확인하기1 : " + newTextData);
////
////                            if (UserID.equals("50")) { //관리자 계정이면
////                                dataChat.add(new DataChatItem(chat_text, UserName, newTextData , user_id ,type, 1));
////                            } else {//관리자 계정이 아니면
////                                dataChat.add(new DataChatItem(chat_text, UserName, newTextData , user_id ,type, 2));
////                            }
////                            //dataChat.add(new DataChatItem(chat_text, UserName, newTextData , user_id ,type, 1));
////                            Log.d("CA", " 업데이트된 텍스트트 dataChat 확인하기1 : " + dataChat);
////
////                            mHandler2.post(new getData2(dataChat)); //보낸 채팅 화면에 띄우는 핸들러
//
//
//                        }
//                    }.start();





//                    final Handler mhandler = new Handler();
//                    Thread mthread = new Thread(new Runnable(){
//                        @Override
//                        public void run() {
//                            mhandler.post(new Runnable(){
//                                @Override
//                                public void run() {
//
//                                    ChatItemAdapter chatItemAdapter = new ChatItemAdapter(getApplicationContext(),dataChat);
//                                    chatView.setAdapter(chatItemAdapter);
//                                    chatItemAdapter.notifyItemInserted(dataChat);
//
//                                }
//                            });
//                        }
//                    });
//                    mthread.start();






                //}


            }
        });

//        //////////////////////채팅 보내는 쓰레드///////////////////////////////////////////////
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//
//                    // Log.d("서버로 채팅 보내기 성공", joClientInfo.toString());//동작함..!!
//
//                    //디비에 있는걸 가져와서 Activity로 데이터를 보내준다.
//                    streamReader = new InputStreamReader(mService.Ssocket.getInputStream());
//                    mService.reader = new BufferedReader(streamReader);
//                    while (true) { ///얘를 추가하니까 채팅이 된다.
//                        String read2 = mService.reader.readLine();
//                        Log.d("CA", "read2 서버에서 받아온 정보 확인 해보기 : " + read2);//새로 저장된 정보 맞음!!
//
//                        JSONArray ja = new JSONArray(read2);
//                        JSONObject object = ja.getJSONObject(0);
//                        Log.e("json object 확인하기 269 ", "object : " + object.toString());
//
//                        user_id_chat = object.getString("user_id_chat");
//                        chat_text = object.getString("chat_text");
//                        chat_date = object.getString("chat_date");
//                        name = object.getString("name");
//                        receiver = object.getString("receiver");
//                        type = object.getString("type");
//
//                        Log.e("receiver확인 ", "receiver 271  : " + receiver);
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
//                        //데이터 그대로 가져와서 화면에 업데이트 (내화면 업데이트)
//                        //mHandler.post(new getDataTH(dataChat));
//                        //해당 소켓스트림이 해당하는 화면을 업데이트 한다.
//                        mHandler2.post(new getData2(dataChat));
//
//                        Log.e("CA", "295 여기까지 동작 !!");
//
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                super.run();
//                                inputChat.setText(""); //입력창 비움
//                                //키보드 올라오는 기능 관련
//                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(inputChat.getWindowToken(), 0);
//
//                            }
//                        }.start();
//                    }
//
//                } catch (IOException | JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        }.start();





        //갤러리로 이동하는 버튼
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(ChatActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                launcher.launch(intent);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();


            }
        });


    }//onCreate close




    //이미지 업로드 관련
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Uri filepath = intent.getData();

//                      imageview.setImageURI(uri);

//                        Glide.with(EditProfileActivity.this)
//                                .load(filepath)
//                                .centerCrop()
//                                .into(photo);

                        try {
                            InputStream inputStream = getContentResolver().openInputStream(filepath);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            ImageView chat_img;
                            chat_img = findViewById(R.id.chat_img);
                            chat_img.setVisibility(View.INVISIBLE);
                            chat_img.setImageBitmap(bitmap);
                            encodeBitmapImage(bitmap);
                           // upload_img();//이미지를 디비에 저장하고 화면에 업데이트 한다.

                        } catch (Exception e) {

                        }


                    }
                }
            });

    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = dateFormat.format(date);
        return getTime;
    }





    //이미지 인코딩
    private void encodeBitmapImage(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImageString = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);

    }



    public void gettingChat(){
        String url = URL_read_chat;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
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


//                                if (receiver.equals(user_id) && UserID.equals("50")) {//초록색 user_id 클릭한 방 user_id
//                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//                                } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) {
//                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                                }
//

                            }

                            System.out.println("dataChat 확인  : " + dataChat);
                            mHandler.post(new getDataTH(dataChat));







                        } catch (JSONException e) {
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





    //데이터 가져와서 화면에 반영하는 핸들러
    class getDataTH implements Runnable {
        private ArrayList<DataChatItem> dataChatItemArrayList;

        public getDataTH(ArrayList<DataChatItem> dataChat) {
            this.dataChatItemArrayList = dataChat;
        }


        @Override
        public void run() {
            ChatItemAdapter chatItemAdapter = new ChatItemAdapter(getApplicationContext(),dataChatItemArrayList);
            chatView.setAdapter(chatItemAdapter);
            chatView.scrollToPosition(dataChatItemArrayList.size() - 1);


        }
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

            ChatItemAdapter chatItemAdapter = new ChatItemAdapter(ChatActivity.this, dataChatItemArrayList);
            chatView.setAdapter(chatItemAdapter);
            chatItemAdapter.notifyItemInserted(dataChatItemArrayList);
            //chatItemAdapter.notifyItemInserted(dataChatItemArrayList.size() - 1);
            chatView.scrollToPosition(dataChatItemArrayList.size() - 1);


        }
    }

    private void getChatText(Intent intent) throws JSONException {

        if (intent.getStringExtra("json") != null) {
            String json = intent.getStringExtra("json");
            String jsonObject1 = intent.getStringExtra("jsonObject1");

            Log.d("socketService에 채팅 json 도착함. ", "String json : " + json);//
            Log.d("socketService에 채팅 jsonObject1 도착함. ", "String jsonObject1 : " + jsonObject1);//

            JSONObject joClientInfo = new JSONObject((intent.getStringExtra("json")));
            ////////채팅 보내는 쓰레드
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        //메세지창에 뜨는 메세지

                        //서버에 데이터 보내기
                        // PrintWirte에 OutputStream을 래핑하여 다음과 같이 데이터를 텍스트 형식으로 보낼 수 있음.
                        sendWriter = new PrintWriter(mService.Ssocket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
                        sendWriter.println(joClientInfo); //제이슨 객체
                        sendWriter.flush();//서버로 보내짐
                        Log.d("서버로 채팅 보내기 성공", joClientInfo.toString());//동작함..!!

                        //디비에 있는걸 가져와서 Activity로 데이터를 보내준다.
                        InputStreamReader streamReader = new InputStreamReader(mService.Ssocket.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);
                        String read2 = reader.readLine();
                        Log.d("SS", "read2 디비에 새로 저장된 정보 확인 해보기 : " + read2);//새로 저장된 정보 맞음!!

                        JSONArray ja = new JSONArray(read2);
                        JSONObject object = ja.getJSONObject(0);
                        Log.e("json object 확인해봐라", "object : "  + object.toString() );
//                            String newTextData = (String) object.get("chat_text");
//                            Log.e("SS", "newTextData 확인해봐라 " + newTextData);




                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
                    .start();

            mHandler2 = new Handler();
            mHandler2.post(new ChatActivity.getData2(dataChat));
        }
    }



    //텍스트 가져오기
    private void GetNewChatText(Intent intenttt) {
        if (intenttt != null) {
//            Thread thread = new Thread() {
//                @Override
//                public void run() {
//                    try {
//
//
//                        //String read = reader.readLine();
//                        newTextData = intenttt.getStringExtra("newTextData"); //새로 추가된 데이터만 ..
//                        Log.e("CA", " 1149 서비스로부터 전달받은 데이터3 String : " + newTextData);
//
//                        try { //채팅 데이터 받기
////                    JSONArray ja = new JSONArray(newTextData);
//                            JSONObject object = new JSONObject(newTextData);
//                            Log.d("CA", "서비스로 받은 object 값: " + object.toString()); //채팅 전체
////                    for (int i = 0; i < ja.length(); i++) {
//                        for (int i = 0; i < 1; i++) {
////                        JSONObject object = ja.getJSONObject(i);
////                        while(true){
//                            user_id_chat = object.getString("user_id_chat");
//                            chat_text = object.getString("chat_text");
//                            chat_date = object.getString("chat_date");
//                            name = object.getString("name");
//                            receiver = object.getString("receiver");
//                            type = object.getString("type");
//
//                            Log.e("receiver확인 ", "receiver : " + receiver);
//
//                            //관리자 계정이 아닐 때 리사이클러뷰에 필요한 데이터 받기
//                            if (UserID != "50") {
//                                if (name.equals(UserName)) { //user가 쓴 채팅이면
//                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//
//                                } else if (user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓰고, 받는 사람이 user
//                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                                }
//                            }
//
//                            //관리자 계정일 때 데이터 받기
//                            if (receiver.equals(user_id)) {//초록색
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//                            } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) {
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                            }
//
//                            //데이터 그대로 가져와서 화면에 업데이트 (내화면 업데이트)
//                          //  mHandler.post(new getDataTH(dataChat));
//
//
//                            //해당 소켓스트림이 해당하는 화면을 업데이트 한다.
//                            mHandler2.post(new getData2(dataChat));
//
//
//
//
//
//                                 }
//
//                            //핸들러 작동 //데이터 가져와서 리사이클러뷰 반영
////                            //해당 소켓스트림이 해당하는 화면을 업데이트 한다.
//
//                            //새로운 텍스트
////                            System.out.println("CA // 1197 newTextData // Chat msg: " + newTextData);
//                            //mHandler2.post(new getData2(dataChat));
////                            System.out.println("CA // 1199 sendmsg : " + sendmsg);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                      //  }//while 문 close
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
////                }
////
//                }
//
//                ;
//
//            };
//            thread.start();
        }
    }





//        }else{
//            Log.d("CA", "newTextData가 없다.");
//        }//if (intent) close
//    } //


    //이미지 업로드 메소드
    private void upload_img() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() { //결과 콜백
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("chat_img"); //"feed"라는 jsonArray를 php에서 받음

                            //feed 어레이 풀기
                            for (int i = 0; i < 1; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                user_id_chat = object.getString("user_id_chat");
                                chat_text = object.getString("chat_text");
                                chat_date = object.getString("chat_date");
                                type = object.getString("type"); //1 = image
                                receiver = object.getString("receiver");
                                chat_id = object.getString("chat_id");
                                dataChat.add(new DataChatItem(chat_text, user_id_chat, chat_date, receiver, type, 2));

                                //핸들러를 통해 보내는 사람(나) 화면 ui를 업데이트 시켜준다.
                                mHandler2.post(new getData2(dataChat));


                            }


                        } catch (JSONException e) {


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }


                }) {

            //@org.jetbrains.annotations.Nullable
            @Override //클라이언트 데이터 서버로 전송하기 위해
            protected Map<String, String> getParams() {

                //서버에 전송할 데이터 맵 객체에 담아 변환.
                Map<String, String> params = new HashMap<>();

                params.put("user_id_chat", UserID);
                params.put("type", "1");


                if (UserID.equals("50")) {
                    params.put("receiver", user_id);

                } else {
                    params.put("receiver", "50");

                }

                params.put("chat_text", encodeImageString);

                return params;
            }



        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);    //서버에 요청




        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                        JsonObject jsonObject1 = new JsonObject();
                        jsonObject1.addProperty("user_id_chat", UserID);
                        jsonObject1.addProperty("type", "1");
                        jsonObject1.addProperty("chat_text", "");
                        jsonObject1.addProperty("receiver", receiver);

//                    Intent intent3= new Intent(getApplicationContext(), MyService.class);
//                    intent3.putExtra("img", jsonObject1.toString());//이미지 데이터 인텐트로 서비스에 전달
//
//                    startService(intent3); // Service에 데이터를 전달한다.
//
//                    Log.d("CA", "json 채팅 서비스에 데이터 전달 : " + jsonObject1.toString());


                        sendWriter = new PrintWriter(mService.Ssocket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
                        sendWriter.println(jsonObject1); //제이슨 객체
                        sendWriter.flush();
                        Log.d("jsonObject1(이미지) 보낸 것 확인 ", jsonObject1.toString());
                        ///////////////////////여기까지 동작..


                        //해당 소켓스트림이 해당하는 화면을 업데이트 한다.
                        InputStreamReader streamReader = new InputStreamReader(mService.Ssocket.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);

                    while(true) {
                        String gettingImg = reader.readLine();
                        Log.e("CA", "gettingImg : " + gettingImg);
                        JSONObject object = new JSONObject(gettingImg);
                        Log.d("CA", "object gettingImg 값: " + object.toString()); //채팅 전체

                        user_id_chat = object.getString("user_id_chat");
                        chat_text = object.getString("chat_text");
                        chat_date = object.getString("chat_date");
                        name = object.getString("name");
                        receiver = object.getString("receiver");
                        type = object.getString("type");

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


                        mHandler2.post(new getData2(dataChat));
                        Log.d("dataChat 확인 1323", dataChat.toString());
                    }
                } catch (Exception e) {
                                            e.printStackTrace();
                }

            }
        }.start();
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
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }


    };






}




//    private void processCommand2(Intent intent) {
//        if (intent != null) {
//            ////////채팅 보내는 쓰레드
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    try {
//                        //메세지창에 뜨는 메세지
//
//                        //서버에 데이터 보내기
//                        // PrintWirte에 OutputStream을 래핑하여 다음과 같이 데이터를 텍스트 형식으로 보낼 수 있음.
//                        sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
//                        sendWriter.println(joClientInfo); //제이슨 객체
//                        sendWriter.flush();//서버로 보내짐
//                        Log.d("서버로 채팅 보내기 성공", joClientInfo.toString());
//
//                        inputChat.setText(""); //입력창 비움
//
//                        //키보드 올라오는 기능 관련
//                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(inputChat.getWindowToken(), 0);
//
//                        //dataChat.add(new DataChatItem(chat_text, UserName, chat_date , user_id ,type, 1));
//                        mHandler2.post(new getData2(dataChat)); //보낸 채팅 화면에 띄우는 핸들러
//                        // adapter.notifyDataSetChanged();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
//
//        }
//    }




//    class SocketServerThread extends Thread {
//        @Override
//        public void run() {
//            try {
//                //ip 주소 변환
//                InetAddress serverAddr = InetAddress.getByName(ip);
//
//                //1. 소켓 연결
//                socket = new Socket(serverAddr, port);//서버에 연결하기 위한 소켓 생성
//                //서버에 연결하기 위한 소켓 객체를 만드는 세 가지 생성자
//                //a - Socket(InetAddress address, int port)
//                //b - Socket(InetAddress address, int port, InetAddress localAddr, int localPort)
//                //c - Socket(String host, int port)
//                //입출력 에러 발생 가능성 있기 때문에 IOException 처리를 해줘야함.
//
//
//                //우선 서버로 user_id 를 보낸다. (그 user_id 에 맞는 데이터 가져오기 위해서)
//                if (!UserID.equals("50")) { //관리자가 아닐 때
//                    sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
//
//                    sendWriter.println(UserID);
//                    sendWriter.flush();//서버로 보내짐
//
//                    Log.d("서버로 유저 id 보내기 성공", UserID);
//
//
//                } else { //관리자 일 때
//                    sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
//                    sendWriter.println("50");//user_id
//                    sendWriter.flush();//서버로 보내짐
//
//
//
//                }
//
//                //getInputStream(서버에서 데이터 읽기 위해 소켓에서 가져와야할 객체)
//                //->InputStreamReader->BufferedReader
//                InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
//                BufferedReader reader = new BufferedReader(streamReader);
//                //BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//
//                while (true) {
//                    String read = reader.readLine();
//
//                    try { //채팅 데이터 받기
//                        JSONArray ja = new JSONArray(read);
//                        Log.d("soo", "db jsonArray 값: " + ja.toString());
//                        for (int i = 0; i < ja.length(); i++) {
//                            JSONObject object = ja.getJSONObject(i);
//                            //chat_id = object.getString("chat_id");
//                            user_id_chat = object.getString("user_id_chat");
//                            chat_text = object.getString("chat_text");
//                            chat_date = object.getString("chat_date");
//                            name = object.getString("name");
//                            receiver = object.getString("receiver");
//                            type = object.getString("type");
//
//                            //관리자 계정이 아닐 때 리사이클러뷰에 필요한 데이터 받기
//                            if (UserID != "50") {
//                                if (name.equals(UserName)) { //user가 쓴 채팅이면
//                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//
//                                } else if (user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓰고, 받는 사람이 user
//                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                                }
//                            }
//
//                            //관리자 계정일 때 데이터 받기
//                            if (receiver.equals(user_id)) {//초록색
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//                            } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) {
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                            }
//
////                                else if(UserID == "50"){ //관리자 계정일 때  userName이 관리자..
////                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, 1));
////                                    if (receiver.equals(user_id)) { //user가 쓰고 받는 사람이 관리자
////                                        dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, 1));
////                                    } else if (user_id_chat.equals("50")) { //관리자가 쓴 채팅이면
////                                        dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, 2));
////                                    }
//
////                                }
//
//                            System.out.println("dataChat : " + dataChat);
//
//                            //핸들러 작동 //데이터 가져와서 리사이클러뷰 반영
//                            mHandler.post(new getDataTH(dataChat));
//
//
//                        }
//
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    System.out.println("Chat msg: " + read);
//                    System.out.println("sendmsg : " + sendmsg);
//
//
//
//                }
//
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//


//    @Override
//    protected void onStop() {
//        super.onStop();
//        try {
//            sendWriter.close();
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }




//processCommand2(intent);
//                ////////채팅 보내는 쓰레드
//                new Thread() {
//                    @Override
//                    public void run() {
//                        super.run();
//                        try {
//                            //메세지창에 뜨는 메세지
//
//                            //서버에 데이터 보내기
//                            // PrintWirte에 OutputStream을 래핑하여 다음과 같이 데이터를 텍스트 형식으로 보낼 수 있음.
//                            sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
//                            sendWriter.println(joClientInfo); //제이슨 객체
//                            sendWriter.flush();//서버로 보내짐
//                            Log.d("서버로 채팅 보내기 성공", joClientInfo.toString());
//
//                            inputChat.setText(""); //입력창 비움
//
//                            //키보드 올라오는 기능 관련
//                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(inputChat.getWindowToken(), 0);
//
//                            //dataChat.add(new DataChatItem(chat_text, UserName, chat_date , user_id ,type, 1));
//                            mHandler2.post(new getData2(dataChat)); //보낸 채팅 화면에 띄우는 핸들러
//                            // adapter.notifyDataSetChanged();
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();

// new Thread() {
//                                    @Override
//                                    public void run() {
//                                        super.run();
//                                        try {
//
//                                            JsonObject jsonObject1 = new JsonObject();
//                                            jsonObject1.addProperty("user_id_chat", UserID);
//                                            jsonObject1.addProperty("type", "1");
//                                            jsonObject1.addProperty("chat_text", "");
//                                            jsonObject1.addProperty("receiver", receiver);
//
//
//                                            sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
//                                            sendWriter.println(jsonObject1); //제이슨 객체
//                                            sendWriter.flush();
//                                            Log.d("jsonObject1 보낸 것 확인 ", jsonObject1.toString());
//
//                                            //해당 소켓스트림이 해당하는 화면을 업데이트 한다.
//                                            mHandler2.post(new getData2(dataChat));
//
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                }.start();





