package com.example.frame;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.frame.etc.DataChatItem;
import com.example.frame.etc.ServiceThread;
import com.example.frame.etc.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MyService extends Service {

    // Binder given to clients
    //private final IBinder binder = new LocalBinder();
    String UserName,UserID;
    Socket Ssocket;
    PrintWriter sendWriter;
    private String ip = "52.79.204.252"; //aws 퍼플릭 ip
    private int port = 6137;
    private String chat_date, chat_id, user_id_chat, chat_text, name, receiver, type;
    private ArrayList<DataChatItem> dataChat = new ArrayList<>();
    String user_id;

    OutputStream sender;
    InputStream writer;
    BufferedReader reader;
    Notification Notifi ;
    NotificationManager mNotificationManager2;
    ServiceThread thread;
    String read2;
    JSONObject ChatObject;
    NotificationCompat.Builder notifyBuilder2;

    // Channel에 대한 id 생성 : Channel을 구부하기 위한 ID 이다.
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_ID2 = "0";
    // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
    private NotificationManager mNotificationManager;
    //NotificationCompat.Builder notifyBuilder;

    NotificationCompat.Builder notification;

    private static final int NOTIFICATION_ID = 0;

    private boolean isService ; // false..!
    private boolean eBound ; // false..!

    NotificationCompat.Builder notifyBuilder;

    IBinder mBinder = new LocalBinder();
    ChatActivity2 chatActivity2 = new ChatActivity2();



    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        MyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetail();

        UserName = user.get(sessionManager.NAME);
        UserID = user.get(sessionManager.ID); //접속한 유저의 아이디

        //소켓 스레드
        MyService.SocketThread Sthread = new MyService.SocketThread();
        Sthread.start();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            getChatText(intent); //데이터 주고 받기 메서드

            //getWinnerList(intent);

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        if(eBound == true){

            Intent eventIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent eventPendingIntent
                    = PendingIntent.getActivity(this, 0, eventIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            //notification manager 생성
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
            if(android.os.Build.VERSION.SDK_INT
                    >= android.os.Build.VERSION_CODES.O) {
                //Channel 정의 생성자( construct 이용 )
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID2, "Test Notification", mNotificationManager.IMPORTANCE_HIGH);
                //Channel에 대한 기본 설정
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setDescription("Notification from Mascot");
                // Manager을 이용하여 Channel 생성
                mNotificationManager.createNotificationChannel(notificationChannel);

                 notifyBuilder2 = new NotificationCompat.Builder(this, CHANNEL_ID2)
                        .setContentTitle("[Frame 이벤트 당첨]")
                        .setContentText(" '마이페이지 > 내 티켓' 에서 티켓을 확인해보세요.")
                        .setSmallIcon(R.drawable.app_logo);

//                mNotificationManager2.notify(NOTIFICATION_ID, notifyBuilder.build());


            }

        }


        // PendingIntent를 이용하면 포그라운드 서비스 상태에서 알림을 누르면 앱의 MainActivity를 다시 열게 된다.
        Intent testIntent = new Intent(getApplicationContext(), ChatActivity2.class);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(this, 0, testIntent, PendingIntent.FLAG_CANCEL_CURRENT);



        //notification manager 생성
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if(android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O) {
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Test Notification", mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);

            notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setContentTitle("Frame")
//                    .setContentText("[문의사항]을 확인해보세요.")
                    .setContentTitle("[Frame 이벤트 당첨]")
                    .setContentText(" '마이페이지 > 내 티켓' 에서 티켓을 확인해보세요.")
                    .setSmallIcon(R.drawable.app_logo);


        }

        /////////////////////////////////////////



//        //만약에 ticket에 추가되면 , 해당 user_id 소켓을 통해 알림이 간다.





        return START_STICKY;


    }


    /** method for clients */
    //소켓 연결하는 스레드
        class SocketThread extends Thread { //소켓 연결 스레드
            @Override
            public void run() {
                try {
                    //ip 주소 변환
                    InetAddress serverAddr = InetAddress.getByName(ip);

                    //1. 소켓 연결
                    Ssocket = new Socket(serverAddr, port);//서버에 연결하기 위한 소켓 생성
                    //서버에 연결하기 위한 소켓 객체를 만드는 세 가지 생성자
                    //a - Socket(InetAddress address, int port)
                    //b - Socket(InetAddress address, int port, InetAddress localAddr, int localPort)
                    //c - Socket(String host, int port)
                    //입출력 에러 발생 가능성 있기 때문에 IOException 처리를 해줘야함.
                    sender = Ssocket.getOutputStream();//보내는 사람...
                    writer = Ssocket.getInputStream(); //쓰는

                    Log.d("UserID 확인 : ", UserID);
                    //우선 서버로 user_id 를 보낸다. (그 user_id 에 맞는 데이터 가져오기 위해서)
                    if (!UserID.equals("50")) { //관리자가 아닐 때

                        sendWriter = new PrintWriter(sender);//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
                        sendWriter.println(UserID);
                        sendWriter.flush();//서버로 보내짐
                        Log.e("MS", "서버로 유저 id 보내기 성공 : " + UserID);


                    } else { //관리자 일 때
                        sendWriter = new PrintWriter(sender);//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
                        sendWriter.println("50");//user_id
                        sendWriter.flush();//서버로 보내짐
                        Log.e("MS", "서버로 유저 id 보내기 성공 : " + UserID);
                    }

                    InputStreamReader streamReader = new InputStreamReader(writer);
                    reader = new BufferedReader(streamReader);

                } catch (Exception e) {

                }

            }
        }



//

        //채팅 보내고 받기 메서드
    private void getChatText(Intent intent) throws JSONException, IOException {


//        if (intent.getStringExtra("json") != null) {
//            String json = intent.getStringExtra("json");
//
//            Log.d("MyService 채팅 json 도착함. ", "String json : " + json);//
//
//            JSONObject joClientInfo = new JSONObject((intent.getStringExtra("json")));
//
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
//                        sendWriter.println(joClientInfo); //제이슨 객체
//                        sendWriter.flush();//서버로 보내짐
//                        Log.d("서버로 채팅 보내기 성공", joClientInfo.toString());//동작함..!!
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//                    .start();
//
//        }


        //데이터 받기
        new Thread() {
            public void run() {

                try {
                    sendWriter = new PrintWriter(Ssocket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(Ssocket.getInputStream()));

                    while (true) {

                        read2 = input.readLine();
                       //ChatObject = ja.getJSONObject(0);
                        Log.e("MS", "402 read2 확인해봐라 " + " object : " + read2);
                       //String newTextData = (String) object.get("chat_text");
//                        Log.e("MS", "newTextData 확인해봐라 " + newTextData);


//                        if(chatActivity2.cBound) //유저가 보고 있는 화면이 채팅 화면이면 아래 인텐트 실행하기
//                        {
                            Log.e("MS", "323" );
                            // 다시 액티비티로 데이터 보내주기
                            Intent sendingTextMsgIntent = new Intent(MyService.this, ChatActivity2.class);
                            //액티비티로 보낸다.
                            sendingTextMsgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //sendingTextMsgIntent.putExtra("newTextData", ChatObject.toString());
                            sendingTextMsgIntent.putExtra("newTextData", read2);
                            startActivity(sendingTextMsgIntent);
                            Log.e("MS", "새로 받은 데이터 CA 보냄!!!");

//                        }else { //else 그렇지 않다면 노티피케이션 알람 띄우기
//                            mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
//
//                        }





                    }


                } catch (Exception e) {

                }
            }


        }.start();


    }


//    private void getWinnerList(Intent intent) throws JSONException, IOException {
//
//
//        if (intent.getStringExtra("winnerList") != null) {
//
//            eBound = true;
//            String winnerList = intent.getStringExtra("winnerList");
//
//            Log.d("MyService 채팅 winnerList 도착함. ", "String winnerList : " + winnerList);//
//
//            JSONArray jsonArray = new JSONArray(winnerList);
//            Log.d("MyService  jsonArray. ", "String winnerList jsonArray : " + jsonArray);//
//
//            mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
//
//            //JSONObject joClientInfo = new JSONObject((intent.getStringExtra("json")));
//
//            ////////채팅 보내는 쓰레드
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    try {
//                        //메세지창에 뜨는 메세지
//
//                        //서버에 데이터 보내기
////                        // PrintWirte에 OutputStream을 래핑하여 다음과 같이 데이터를 텍스트 형식으로 보낼 수 있음.
////                        sendWriter.println(joClientInfo); //제이슨 객체
////                        sendWriter.flush();//서버로 보내짐
////                        Log.d("서버로 채팅 보내기 성공", joClientInfo.toString());//동작함..!!
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//                    .start();
//
//        }
//
//
//        //데이터 받기
//        new Thread() {
//            public void run() {
//
//                try {
//                    sendWriter = new PrintWriter(Ssocket.getOutputStream());
//                    BufferedReader input = new BufferedReader(new InputStreamReader(Ssocket.getInputStream()));
//
//                    while (true) {
//
//                        read2 = input.readLine();
//                        //ChatObject = ja.getJSONObject(0);
//                        Log.e("MS", "402 read2 확인해봐라 " + " object : " + read2);
//                        //String newTextData = (String) object.get("chat_text");
////                        Log.e("MS", "newTextData 확인해봐라 " + newTextData);
//
//
//                        // 다시 액티비티로 데이터 보내주기
//                        Intent sendingTextMsgIntent = new Intent(MyService.this, ChatActivity2.class);
//                        //액티비티로 보낸다.
//                        sendingTextMsgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
//                                Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        //sendingTextMsgIntent.putExtra("newTextData", ChatObject.toString());
//                        sendingTextMsgIntent.putExtra("newTextData", read2);
//                        startActivity(sendingTextMsgIntent);
//                        Log.e("MS", "새로 받은 데이터 CA 보냄!!!");
//                        //mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
//
//
//
//                    }
//
//
//                } catch (Exception e) {
//
//                }
//            }
//
//
//        }.start();
//
//
//    }
//
//
//
//
//





}




//디비에 있는걸 가져와서 Activity로 데이터를 보내준다.
//                            InputStreamReader streamReader = new InputStreamReader(Ssocket.getInputStream());
//                            BufferedReader reader = new BufferedReader(streamReader);
//                            String read2 = reader.readLine();
//                            Log.d("MS", "314 read2 디비에 새로 저장된 정보 확인 해보기 : " + read2);//새로 저장된 정보 맞음!!
//
//                            JSONArray ja = new JSONArray(read2);
//                            JSONObject object = ja.getJSONObject(0);
//                            Log.e("MS","318 json object 확인해봐라 " + " object : " + object.toString());
////                            String newTextData = (String) object.get("chat_text");
////                            Log.e("SS", "newTextData 확인해봐라 " + newTextData);
//
//                            //다시 액티비티로 데이터 보내주기
//                            Intent sendingTextMsgIntent = new Intent(MyService.this, ChatActivity.class);
//                            //액티비티로 보낸다.
//                            sendingTextMsgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
//                                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            sendingTextMsgIntent.putExtra("newTextData", object.toString());
//                            startActivity(sendingTextMsgIntent);
//                            Log.e("SS", "새로 받은 데이터 CA 보냄!!!");


//                        if(watchingChatAct = true) {
//
//                            mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());

//sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
////                        Log.d("object  779줄 ", object.toString());
//String outStream = intent.getStringExtra("outStream");

//sendWriter.println(outStream); //제이슨 객체

//제이슨 어레이 벗기기
//                        JSONArray ja = new JSONArray(read2);
//                        JSONObject object = ja.getJSONObject(0);
//                        Log.e("object 확인해봐라", "object : "  + object );
//                        String newTextData = (String) object.get("chat_text");
//                        Log.e("SS", "newTextData 확인해봐라 " + newTextData);
//
//
//                        if (UserID.equals("50")) { //관리자 계정이면
//                            dataChat.add(new DataChatItem(chat_text, UserName, newTextData , user_id ,type, 1));
//                        } else {//관리자 계정이 아니면
//                            dataChat.add(new DataChatItem(chat_text, UserName, newTextData , user_id ,type, 2));
//                        }
//
//                        sendWriter.println(object); //제이슨 객체
//                        Log.e("확인", "여기까지 됨!!!!!");
//                        sendWriter.flush();
//                        Log.e("확인2", "여기까지 됨2!!!!!");

//
//                        sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
//                        sendWriter.println(object); //제이슨 객체
//                        sendWriter.flush();
//                        Log.d("object 보낸 것 확인 ", object.toString());
//
//                        //해당 소켓스트림이 해당하는 화면을 업데이트 한다.
//                        }


//class myServiceHandler extends Handler {
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            Intent intent = new Intent(MyService.this, ChatActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//            Notifi = new Notification.Builder(getApplicationContext())
//                    .setContentTitle("Content Title")
//                    .setContentText("Content Text")
//                    .setTicker("알림!!!")
//                    .setSmallIcon(R.drawable.app_logo)
//                    .setContentIntent(pendingIntent)
//                    .build();
//
//            //소리추가
//            Notifi.defaults = Notification.DEFAULT_SOUND;
//
//            //알림 소리를 한번만 내도록
//            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;
//
//            //확인하면 자동으로 알림이 제거 되도록
//            Notifi.flags = Notification.FLAG_AUTO_CANCEL;
//
//
//            Notifi_M.notify( 777 , Notifi);
//
//            //토스트 띄우기
//            Toast.makeText(MyService.this, "뜸?", Toast.LENGTH_LONG).show();
//        }
//    };