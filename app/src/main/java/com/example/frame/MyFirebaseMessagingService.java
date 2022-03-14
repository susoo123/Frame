package com.example.frame;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

//파이어 베이스 메시징 서비스
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG ="MyFirebaseMessagingService";
    String get_who;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // get_who = remoteMessage.getData().get("who");

        if (remoteMessage.getNotification() != null) { //포그라운드  Check if message contains a notification payload.
            popNoti(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }
        else if (remoteMessage.getData().size() > 0) { //백그라운드 Check if message contains a data payload.
            popNoti(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"));

        }

//        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
//            popNoti(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
//          //  popNoti(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"));
//        }


    }

    private void popNoti( String title, String message) {

        Log.d(TAG+ " sendNotificationtitle", String.valueOf(title));
        Log.d(TAG+ " sendNotificationmessage", String.valueOf(message));


        final String CHANNEL_ID = "ChannerID"; //Android 8.0(API 수준 26) 이상에서는 호환성을 유지하기 위해 필요함. 이전 버전에서는 무시됨.
        NotificationManager mManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        //채널 만들기
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_NAME = "ChannerName";
            final String CHANNEL_DESCRIPTION = "ChannerDescription";
            final int importance = NotificationManager.IMPORTANCE_HIGH;

            // add in API level 26
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            mChannel.setDescription(CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            mManager.createNotificationChannel(mChannel);
        }

        //인텐트 값 설정 현재액티비티-> 내 티켓 액티비티로!!
        Intent mIntent = new Intent(getApplicationContext(), TicketBoxActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mIntent.putExtra("get_data_postnumber",action_post_number);
        mIntent.putExtra("FROM_INTENT","notification");
//        startActivity(mIntent);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(this, 0 /* Request code */, mIntent,
                PendingIntent.FLAG_ONE_SHOT);



        //노티피케이션 형태
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_launcher_background);//필수
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);//필수
        builder.setContentText(message);//필수
        builder.setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { //단말의 현재 버전 < 오레오 버전
            builder.setContentTitle(title);
            builder.setVibrate(new long[]{500, 500});
        }

        mManager.notify(0, builder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }


}
