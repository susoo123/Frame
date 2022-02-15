package com.example.frame;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Parcelable;
import android.widget.Toast;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Message;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.frame.etc.DataChatItem;
import com.example.frame.etc.ServiceThread;
import com.example.frame.etc.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

//서버 서비스
public class SocketService extends Service {

    public static final int MSG_REGISTER_CLIENT = 1;
    //public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SEND_TO_SERVICE = 3;
    public static final int MSG_SEND_TO_ACTIVITY = 4;

    private Messenger mClient = null;   // Activity 에서 가져온 Messenger
    private Handler mHandler, mHandler2;

    Socket socket;
    PrintWriter sendWriter, sendReceiver;
    private String ip = "52.79.204.252"; //aws 퍼플릭 ip
    private int port = 6137;
    private static String URL_create_chat = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/create_chat.php";

    TextView textView;
    String UserID, user_id, UserName, read;
    private String chat_date, chat_id, user_id_chat, chat_text, name, receiver, type;


    private ArrayList<DataChatItem> dataChat = new ArrayList<>();

    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification Notifi ;


    // Binder given to clients
    private final IBinder binder = new LocalBinder();


    @Override
    public IBinder onBind(Intent intent) {

        // mMessenger.getBinder();
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetail();

        UserName = user.get(sessionManager.NAME);
        UserID = user.get(sessionManager.ID); //접속한 유저의 아이디


        SocketThread Sthread = new SocketThread();
        Sthread.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", "onStartCommand() called");

//        try {
//            getChatText(intent);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;

//
//        if (intent == null) {
//            return Service.START_STICKY; //서비스가 종료되어도 자동으로 다시 실행시켜줘!
//        } else {
//
//        }
//
//        return super.onStartCommand(intent, flags, startId);
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(SocketService.this, ChatActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(SocketService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            Notifi = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Content Title")
                    .setContentText("Content Text")
                    .setTicker("알림!!!")
                    .setSmallIcon(R.drawable.app_logo)
                    .setContentIntent(pendingIntent)
                    .build();

            //소리추가
            Notifi.defaults = Notification.DEFAULT_SOUND;

            //알림 소리를 한번만 내도록
            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //확인하면 자동으로 알림이 제거 되도록
            Notifi.flags = Notification.FLAG_AUTO_CANCEL;


            Notifi_M.notify( 777 , Notifi);

            //토스트 띄우기
           // Toast.makeText(SocketService.this, "뜸?", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        SocketService getService() {
            Log.d("LocalBinder", "LocalBinder() called");
            // Return this instance of LocalService so clients can call public methods
            //getAlreadyDataFromSV();

            return SocketService.this;
        }
    }

    /**
     * method for clients
     */  //서비스가 연결되면 소켓이 연결된다.
    class SocketThread extends Thread { //소켓 연결 스레드
        @Override
        public void run() {
            try {
                //ip 주소 변환
                InetAddress serverAddr = InetAddress.getByName(ip);

                //1. 소켓 연결
                socket = new Socket(serverAddr, port);//서버에 연결하기 위한 소켓 생성
                //서버에 연결하기 위한 소켓 객체를 만드는 세 가지 생성자
                //a - Socket(InetAddress address, int port)
                //b - Socket(InetAddress address, int port, InetAddress localAddr, int localPort)
                //c - Socket(String host, int port)
                //입출력 에러 발생 가능성 있기 때문에 IOException 처리를 해줘야함.

                Log.d("UserID 확인 : ", UserID);
                //우선 서버로 user_id 를 보낸다. (그 user_id 에 맞는 데이터 가져오기 위해서)
                if (!UserID.equals("50")) { //관리자가 아닐 때
                    sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
                    sendWriter.println(UserID);
                    sendWriter.flush();//서버로 보내짐



                    Log.e("SS", "서버로 유저 id 보내기 성공 : " + UserID);


                } else { //관리자 일 때
                    sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
                    sendWriter.println("50");//user_id
                    sendWriter.flush();//서버로 보내짐

                    Log.e("SS", "서버로 유저 id 보내기 성공 : " + UserID);

                }

                    //소켓을 통해 데이터를 읽어온다.
                    //getInputStream(서버에서 데이터 읽기 위해 소켓에서 가져와야할 객체)
                    //->InputStreamReader->BufferedReader
                    InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
//                    Intent showIntent = new Intent(SocketService.this, MainActivity.class);
//                        showIntent.addFlags(
//                                Intent.FLAG_ACTIVITY_NEW_TASK |
//                                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
//                                        Intent.FLAG_ACTIVITY_CLEAR_TOP
//                        );
//                     showIntent.putExtra("read", read);
//                        startActivity(showIntent); // Service에서 Activity로 데이터를 전달
//                        Log.d("SS", "서비스가 받은 read 액티비티로 보내기 : " + read); //잘 작동!!


//                try {
//                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    for (int i = 0; i < 1; i++) {
//                        //while (true) {
//                        //이미 저장되어 있는 디비에서 받아오는 inputstream...
//                        read = input.readLine(); //이미 저장된 데이터
//
//                        Log.d("SS", "서비스가 받은 read  : " + read); //잘 작동!!
//                        Intent showIntent = new Intent(SocketService.this, MainActivity.class);
//                        showIntent.addFlags(
//                                Intent.FLAG_ACTIVITY_NEW_TASK |
//                                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
//                                        Intent.FLAG_ACTIVITY_CLEAR_TOP
//                        );
//                        showIntent.putExtra("read", read);
//                        startActivity(showIntent); // Service에서 Activity로 데이터를 전달
//                        Log.d("SS", "서비스가 받은 read 액티비티로 보내기 : " + read); //잘 작동!!
//                    }
//                } catch (Exception e) {
//
//                }

//                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                    for (int i = 0; i < 1; i++){
//                    //while (true) {
//                        //이미 저장되어 있는 디비에서 받아오는 inputstream...
//                        read = input.readLine(); //이미 저장된 데이터
//                        Log.e("SS", "서비스가 받은 read  : " + read); //잘 작동!!
//
//
//                            Intent showIntent = new Intent(SocketService.this, MainActivity.class);
//                            showIntent.addFlags(
//                                    Intent.FLAG_ACTIVITY_NEW_TASK |
//                                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
//                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
//                            );
//                            showIntent.putExtra("read", read);
//                            startActivity(showIntent); // Service에서 Activity로 데이터를 전달
//                        }





//                   getAlreadyDataFromSV(); //빈화면 채팅이 앱 켜자마자 뜬다.


            } catch (Exception e) {

            }

        }
    }

//

        //이미 있던 채팅 내용 받는 메서드 => 얘는 된다...
        private void getAlreadyDataFromSV() {
            Intent showIntent = new Intent(SocketService.this, ChatActivity.class);
            showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            showIntent.putExtra("serviceData", read);
            startActivity(showIntent); // Service에서 Activity로 데이터를 전달
            Log.d("SS", "서비스가 보내는 serviceData  : " + read); //여기서는 잘 나온다..
        }


        //액티비티에서 보낸 채팅 받기 (텍스트)??
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
                            sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
                            sendWriter.println(joClientInfo); //제이슨 객체
                            sendWriter.flush();//서버로 보내짐
                            Log.d("서버로 채팅 보내기 성공", joClientInfo.toString());//동작함..!!

                            //디비에 있는걸 가져와서 Activity로 데이터를 보내준다.
                            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
                            BufferedReader reader = new BufferedReader(streamReader);
                            String read2 = reader.readLine();
                            Log.d("SS", "read2 디비에 새로 저장된 정보 확인 해보기 : " + read2);//새로 저장된 정보 맞음!!

                            JSONArray ja = new JSONArray(read2);
                            JSONObject object = ja.getJSONObject(0);
                            Log.e("json object 확인해봐라", "object : "  + object.toString() );
//                            String newTextData = (String) object.get("chat_text");
//                            Log.e("SS", "newTextData 확인해봐라 " + newTextData);

                            //다시 액티비티로 데이터 보내주기
                            Intent sendingTextMsgIntent = new Intent(SocketService.this, ChatActivity.class);
                            //액티비티로 보낸다.
                            sendingTextMsgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            sendingTextMsgIntent.putExtra("newTextData", object.toString());
                            startActivity(sendingTextMsgIntent);
                            Log.e("SS", "새로 받은 데이터 CA 보냄!!!");

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


                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
                        .start();

//            mHandler2 = new Handler();
//            mHandler2.post(new ChatActivity.getData2(dataChat));
            }
        }

        private void getChatText2(Intent intent) throws JSONException {

            if (intent.getStringExtra("jsonObject1") != null) {

                String jsonObject1 = intent.getStringExtra("jsonObject1");

                Log.d("socketService에 채팅 jsonObject1 도착함. ", "String jsonObject1 : " + jsonObject1);//

                JSONObject joClientInfo = new JSONObject((intent.getStringExtra("jsonObject1")));

                Log.e("확인해봐라!!", "joClientInfo 확인 : " + joClientInfo);
            }
        }

        //이미지
        private void getChatImg(Intent intent2) throws JSONException {
            String img = intent2.getStringExtra("img");

            Log.d("socketService에 채팅 json 도착함. ", "img : " + toString());
            JSONObject joClientInfo = new JSONObject((intent2.getStringExtra("img")));
            ////////채팅 이미지 보내는 쓰레드
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {

                        Intent showIntent2 = new Intent();
                        showIntent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        showIntent2.putExtra("joClientInfo", joClientInfo.toString());
                        startActivity(showIntent2); // Service에서 Activity로 데이터를 전달
                        Log.d("SS", "서비스가 보내는 serviceData  : " + joClientInfo.toString()); //여기서는 잘 나온다..

                        sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
                        sendWriter.println(joClientInfo); //제이슨 객체
                        sendWriter.flush();
                        Log.d("SS", "joClientInfo 보낸 것 확인 : " + joClientInfo.toString());

                        //해당 소켓스트림이 해당하는 화면을 업데이트 한다.
                        //mHandler2.post(new getData2(dataChat));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();

        }


    }


//    private void getAlreadyData2(Intent intentt) {
//        // if (intentt != null) {
////            Thread thread = new Thread() {
////                @Override
////                public void run() {
//        try {
//
//            //while(true){
//            //String read = reader.readLine();
//            serviceData = intentt.getStringExtra("serviceData");
//            Log.e("CA", "서비스로부터 전달받은 이미 저장되어 있는 데이터 serviceData : " + serviceData);
//
//            try { //채팅 데이터 받기
//                JSONArray ja = new JSONArray(serviceData);
//                Log.d("soo", "db jsonArray 값: " + ja.toString());
//                for (int i = 0; i < ja.length(); i++) {
//                    JSONObject object = ja.getJSONObject(i);
//
//                    user_id_chat = object.getString("user_id_chat");
//                    chat_text = object.getString("chat_text");
//                    chat_date = object.getString("chat_date");
//                    name = object.getString("name");
//                    receiver = object.getString("receiver");
//                    type = object.getString("type");
//
//                    if (UserID != "50") {
//                        if (name.equals(UserName)) { //user가 쓴 채팅이면
//                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//
//                        } else if (user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓰고, 받는 사람이 user
//                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                        }
//                    }
//
//                    //관리자 계정일 때 데이터 받기
//                    if (receiver.equals(user_id)) {//초록색
//                        dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//                    } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) {
//                        dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                    }
//
//                    System.out.println("dataChat : " + dataChat);
//                    mHandler.post(new ChatActivity.getDataTH(dataChat));
//                    stopService(intentt);
//
//                }
//
//                //핸들러 작동 //데이터 가져와서 리사이클러뷰 반영
//
//
//                System.out.println("Chat msg: " + serviceData);
//                System.out.println("sendmsg : " + sendmsg);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            // }//while 문 close
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // }
//
//    };


//            thread.start();
//
//
//        }//if (intent) close
//    } //


//    //이미 저장되어 있는 데이터 가져오는 메서드
//    private void getAlreadyData(Intent intent) {
// //       if (intent != null) {
////            Thread thread = new Thread() {
////                @Override
////                public void run() {
//            try {
//                //while(true){
//                String read = intent.getStringExtra("read");
//
//                Log.e("CA", "서비스로부터 전달받은 이미 저장되어 있는 데이터 serviceData : " + read);
//
//                try { //채팅 데이터 받기
//                    JSONArray ja = new JSONArray(read);
//                    Log.d("CA", "getAlreadyData() 안의 db jsonArray 값: " + ja.toString());
//                    for (int i = 0; i < ja.length(); i++) {
//                        JSONObject object = ja.getJSONObject(i);
//
//                        user_id_chat = object.getString("user_id_chat");
//                        chat_text = object.getString("chat_text");
//                        chat_date = object.getString("chat_date");
//                        name = object.getString("name");
//                        receiver = object.getString("receiver");
//                        type = object.getString("type");
//
//                        //관리자 계정이 아닐 때 리사이클러뷰에 필요한 데이터 받기
//                        if (UserID != "50") {
//                            if (user_id_chat.equals(UserID)) { //user가 쓴 채팅이면
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//
//                            } else if (user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓰고, 받는 사람이 user
//                                dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                            }
//                        }
//
//                        //관리자 계정일 때 데이터 받기
//                        if (receiver.equals(user_id)) {//초록색
//                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 2));
//                        } else if (receiver.equals(UserID) && user_id_chat.equals(user_id)) {
//                            dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver, type, 1));
//                        }
//
//                        System.out.println("dataChat : " + dataChat);
//                        mHandler.post(new getDataTH(dataChat));
//                        //stopService(intentt);
//
//                    }
//
//                    //핸들러 작동 //데이터 가져와서 리사이클러뷰 반영
//
//
//                    System.out.println("Chat msg: " + serviceData);
//                    System.out.println("sendmsg : " + sendmsg);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                // }//while 문 close
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//       // }
//
//    };


//            thread.start();
//
//
//}//if (intent) close
//    } //
