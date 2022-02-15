package com.example.frame;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.frame.etc.ServiceThread;
import com.example.frame.etc.SessionManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class EventService extends Service {

    // Binder given to clients
    private final IBinder binder = new LocalBinder();
    String UserName,UserID;
    Socket Esocket;
    PrintWriter sendWriter;
    private String ip = "52.79.204.252"; //aws 퍼플릭 ip
    private int port = 6138;

    OutputStream sender;
    InputStream writer;
    BufferedReader reader;
    Notification Notifi ;
    NotificationManager Notifi_M;
    ServiceThread thread;

    // Channel에 대한 id 생성 : Channel을 구부하기 위한 ID 이다.
    private static final String CHANNEL_ID = "2";
    // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
    private NotificationManager mNotificationManager;
    //NotificationCompat.Builder notifyBuilder;

    NotificationCompat.Builder notification;

    private static final int NOTIFICATION_ID = 1;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        EventService getService() {
            // Return this instance of LocalService so clients can call public methods
            return EventService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetail();

        UserName = user.get(sessionManager.NAME);
        UserID = user.get(sessionManager.ID); //접속한 유저의 아이디


        EventService.SocketThread Sthread = new EventService.SocketThread();
        Sthread.start();






    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        myServiceHandler handler = new myServiceHandler();
//        thread = new ServiceThread(handler);
//        thread.start();

        // PendingIntent를 이용하면 포그라운드 서비스 상태에서 알림을 누르면 앱의 MainActivity를 다시 열게 된다.
        Intent testIntent = new Intent(getApplicationContext(), ChatActivity.class);
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

            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("[Frame 이벤트 당첨]")
                    .setContentText("응모한 이벤트에 당첨되었습니다. \n [내 티켓]에서 티켓을 확인해보세요.")
                    .setSmallIcon(R.drawable.app_logo);

            mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());


        }



        return START_STICKY;


    }


    /** method for clients */
        class SocketThread extends Thread { //소켓 연결 스레드
            @Override
            public void run() {
                try {
                    //ip 주소 변환
                    InetAddress serverAddr = InetAddress.getByName(ip);

                    //1. 소켓 연결
                    Esocket = new Socket(serverAddr, port);//서버에 연결하기 위한 소켓 생성
                    //서버에 연결하기 위한 소켓 객체를 만드는 세 가지 생성자
                    //a - Socket(InetAddress address, int port)
                    //b - Socket(InetAddress address, int port, InetAddress localAddr, int localPort)
                    //c - Socket(String host, int port)
                    //입출력 에러 발생 가능성 있기 때문에 IOException 처리를 해줘야함.
                    sender = Esocket.getOutputStream();//보내는 사람...
                    writer = Esocket.getInputStream(); //쓰는

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



    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(EventService.this, ChatActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(EventService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

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
            Toast.makeText(EventService.this, "뜸?", Toast.LENGTH_LONG).show();
        }
    };





}
