package com.example.frame;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.etc.SessionManager;
import com.example.frame.fragment.EventFragment;
import com.example.frame.fragment.FeedFragment;
import com.example.frame.fragment.HomeFragment;
import com.example.frame.fragment.MypageAdminFragment;
import com.example.frame.fragment.MypageFragment;
import com.example.frame.fragment.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private String StrProfileImg, StrEmail, role;
    private Button btn_logout;
    SessionManager sessionManager;
    private String chat_date2, chat_id2, user_id_chat2, chat_text2, name2, receiver2, type2;

    //????????? ?????? ??????
    MyService mService;
    boolean mBound = false;
    boolean cBound = false;
    Socket socket;
    String read;
    Intent intent;
    String token;


    //????????????????????? ?????? ?????????
    BottomNavigationView bottomNavigationView;
    Deque<Integer> integerDeque = new ArrayDeque<>(5);
    boolean flag = true;

   // String token = FirebaseMessaging.getInstance().getToken().getResult();

    private Messenger mServiceMessenger = null;

    private Messenger mServiceCallback = null;


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



    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceIntent);
        else startService(serviceIntent);

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        token = task.getResult();
//
//                        // Log and toast
////                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.e("MA token 155 ", token);
//                        //????????? ????????? ??????
//                        send_FCM();
//                    }
//                });



        // ?????? ?????? ??????????????? ?????? ??? ?????? ?????? ??????
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_layout, HomeFragment.newInstance()).commit();


        HashMap<String, String> user = sessionManager.getUserDetail();
        String mName = user.get(sessionManager.NAME);
        String mEmail = user.get(sessionManager.EMAIL);
        role = user.get(sessionManager.ROLE);


//        name.setText(mName);


        //????????????????????? ?????? ??????
        bottomNavigationView = findViewById(R.id.bottomNavigationview);

        //?????? ????????????????????? ??????
        integerDeque.push(R.id.home);

        //???????????? ??????
        loadFragment(new HomeFragment());

        //????????? ?????????????????? ????????? set
        bottomNavigationView.setSelectedItemId(R.id.home);

        //????????????????????? ????????? ?????? (???)
        bottomNavigationView.setOnItemSelectedListener(navListener);




//        Fragment searchFragment = SearchFragment.newInstance();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_layout,searchFragment,"search_fragment");
//        transaction.commit();

        //????????? ????????? ????????? ??????
//        Intent intent= getIntent();
//        StrProfileImg = intent.getStringExtra("profileImg");
//        StrEmail = intent.getStringExtra("email");
//
//        TextView tv_email = findViewById(R.id.tv_email);
//        ImageView iv_profileimg = findViewById(R.id.iv_profileimg);

//
//        Log.d("soo","????????? ?????? ?????? ??? : "+StrProfileImg);
//
//        tv_email.setText(StrEmail);
//
//        //????????? ????????? ?????? set
//        //Glide.with(this).load(StrProfileImg).into(iv_profileimg);


//        //???????????? ??????
//        btn_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sessionManager.logout();
//                Toast.makeText(MainActivity.this, "???????????? ??????", Toast.LENGTH_SHORT).show();
//
//                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
//                    @Override
//                    public void onCompleteLogout() {
//                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                    }
//                });
//
//
//
//            }
//        });

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, fragment).commit();      // Fragment??? ????????? MainActivity?????? layout????????? ???????????????.
    }

    private void send_FCM(){

        Log.e("MA" + " send_FCM : ", "??????");
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("MA" + " send_FCM : ", response );


            }//  public void onResponse(String response)
        };

//        fcm_request getUserInformation = new fcm_request(token,responseListener);
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        queue.add(getUserInformation);
    }



    @Override
    protected void onNewIntent(Intent intent) {
        processCommand(intent);    //?????? ??????
        super.onNewIntent(intent);
    }

    //?????? ??????????????? read ????????? ????????? ?????????
    private void processCommand(Intent intent) {
        if (intent != null) {
            String read = intent.getStringExtra("read");
            Log.e("MA", "read????????? mainActivity??? ??????. " + read);

            try {

                try { //?????? ????????? ??????
                    JSONArray ja = new JSONArray(read);
                    Log.d("CA", "getAlreadyData() ?????? db jsonArray ???: " + ja.toString());
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject object = ja.getJSONObject(i);

                        user_id_chat2 = object.getString("user_id_chat");
                        chat_text2 = object.getString("chat_text");
                        chat_date2 = object.getString("chat_date");
                        name2 = object.getString("name");
                        receiver2 = object.getString("receiver");
                        type2 = object.getString("type");

                    }








                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // }//while ??? close
            } catch (Exception e) {
                e.printStackTrace();
            }
            // }

        }
        ;


//            Intent mIntent = new Intent(getApplicationContext(), ChatActivity.class);
//            mIntent.putExtra("chatTextData", read);
//            startActivity(mIntent);

        //room??? ????????????.


    }







    //??????????????? ??????
    private void loadFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_layout,fragment,fragment.getClass().getSimpleName())
                .commit();
        //System.out.println("?????? ?????? ??????... "+fragment.getClass().getSimpleName());
    }



    private NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            int id = item.getItemId();

            //??????????????? ????????? id??? ???????????? ?????? ???
            if (integerDeque.contains(id)) {

                //??????????????????id??? ????????? id??? ???
                if (id == R.id.home) {

                    //deque list ???????????? 0??? ?????? ?????? ???
                    if (integerDeque.size() != 0) {

                        // flag?????? true??? ??? ??????????????? ????????????????????? ??????
                        if (flag) { //flag ????????? ?????? true

                            integerDeque.addFirst(R.id.home);//???????????? ??? ???????????? ????????????

                            //flag??? false??? ?????? set
                            flag = false;

                        }

                    }
                }
                //?????? ?????? ???
                //?????????????????? ????????? id ??????
                integerDeque.remove(id);
            }
            //??????????????? ????????? ???????????? push
            integerDeque.push(id);

            //????????? ??????????????? ??????
            loadFragment(getFragment(item.getItemId()));

            return true;

        }


    };


    private Fragment getFragment(int itemId) {
        switch(itemId){
            case R.id.home  :
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
            return new HomeFragment();

            case R.id.search:
            bottomNavigationView.getMenu().getItem(1).setChecked(true);
            return new SearchFragment();

            case R.id.event:
            bottomNavigationView.getMenu().getItem(2).setChecked(true);
            return new EventFragment();

            case R.id.feed:
            bottomNavigationView.getMenu().getItem(3).setChecked(true);
            return new FeedFragment();

            case R.id.mypage:

                //???????????? ????????? role??? ??????????????? (?????????????????????)
                if(role.equals("admin")){
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);
                    return new MypageAdminFragment();

                }else{ //?????? ????????? ???
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);
                    return new MypageFragment();

                }


        }
        //????????????????????? ????????? ???????????? set
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        return new HomeFragment();
    }


    @Override
    public void onBackPressed() {

        //?????? ????????? ????????? pop??????.
        integerDeque.pop();

        if(!integerDeque.isEmpty()){
            //??? ???????????? ?????? ?????? ?????????
            //?????????????????? ?????? ???.
            loadFragment(getFragment(integerDeque.peek())); // ?????? ????????? ????????? ???????????? ??? ??? ?????? ??????
            //loadFragment(getFragment(R.id.home)); //?????? ????????? ????????? ?????? ???.
        }else {
            //??????????????? ???????????? ???
            //Main Activity??? ??????.
            finish();
        }

    }







    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);


        mBound = false;
    }









}