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
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

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

    //서비스 구현 코드
    MyService mService;
    boolean mBound = false;
    boolean cBound = false;
    Socket socket;
    String read;
    Intent intent;



    //바텀네비게이션 변수 초기화
    BottomNavigationView bottomNavigationView;
    Deque<Integer> integerDeque = new ArrayDeque<>(5);
    boolean flag = true;




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

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//
//                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//                });

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceIntent);
        else startService(serviceIntent);


        //서비스 이용!! 소켓 연결!!
//        intent = new Intent(this, SocketService.class);
//        bindService(intent, connection, Context.BIND_AUTO_CREATE);
//        startService(intent);
//        Log.e("MA", "소켓 연결 성공");


        // 화면 전환 프래그먼트 선언 및 초기 화면 설정
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_layout, HomeFragment.newInstance()).commit();


        HashMap<String, String> user = sessionManager.getUserDetail();
        String mName = user.get(sessionManager.NAME);
        String mEmail = user.get(sessionManager.EMAIL);
        role = user.get(sessionManager.ROLE);


//        name.setText(mName);


        //바텀네비게이션 변수 할당
        bottomNavigationView = findViewById(R.id.bottomNavigationview);

        //덱에 홈프래그먼트를 추가
        integerDeque.push(R.id.home);

        //홈프래그 로드
        loadFragment(new HomeFragment());

        //디폴트 프래그먼트를 홈으로 set
        bottomNavigationView.setSelectedItemId(R.id.home);

        //바텀네비게이션 리스너 달기 (끝)
        bottomNavigationView.setOnItemSelectedListener(navListener);


//        Fragment searchFragment = SearchFragment.newInstance();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_layout,searchFragment,"search_fragment");
//        transaction.commit();

        //카카오 로그인 데이터 받기
//        Intent intent= getIntent();
//        StrProfileImg = intent.getStringExtra("profileImg");
//        StrEmail = intent.getStringExtra("email");
//
//        TextView tv_email = findViewById(R.id.tv_email);
//        ImageView iv_profileimg = findViewById(R.id.iv_profileimg);
//        btn_logout = findViewById(R.id.btn_logout);
//
//        Log.d("soo","이미지 경로 받은 값 : "+StrProfileImg);
//
//        tv_email.setText(StrEmail);
//
//        //프로필 이미지 사진 set
//        //Glide.with(this).load(StrProfileImg).into(iv_profileimg);


        //로그아웃 실행
//        btn_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sessionManager.logout();
//                Toast.makeText(MainActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
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
        fragmentTransaction.replace(R.id.fragment_layout, fragment).commit();      // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
    }

//    private void send_FCM(){
//        Log.d(TAG+ " send_FCM", "실행");
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG+ " send_FCM",response );
//
//
//            }//  public void onResponse(String response)
//        };
//        //fcm_request getUserInformation = new fcm_request(token,responseListener);
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        queue.add(getUserInformation);
//    }



    @Override
    protected void onNewIntent(Intent intent) {
        processCommand(intent);    //이거 호출
        super.onNewIntent(intent);
    }

    //채팅 액티비티로 read 데이터 보내는 메서드
    private void processCommand(Intent intent) {
        if (intent != null) {
            String read = intent.getStringExtra("read");
            Log.e("MA", "read데이터 mainActivity에 왔음. " + read);

            try {

                try { //채팅 데이터 받기
                    JSONArray ja = new JSONArray(read);
                    Log.d("CA", "getAlreadyData() 안의 db jsonArray 값: " + ja.toString());
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
                // }//while 문 close
            } catch (Exception e) {
                e.printStackTrace();
            }
            // }

        }
        ;


//            Intent mIntent = new Intent(getApplicationContext(), ChatActivity.class);
//            mIntent.putExtra("chatTextData", read);
//            startActivity(mIntent);

        //room에 저장한다.


    }







    //프래그먼트 로드
    private void loadFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_layout,fragment,fragment.getClass().getSimpleName())
                .commit();
        //System.out.println("이거 확인 해라... "+fragment.getClass().getSimpleName());
    }



    private NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            int id = item.getItemId();

            //덱리스트가 선택된 id를 포함하고 있을 때
            if (integerDeque.contains(id)) {

                //홈프래그먼트id가 선택된 id일 때
                if (id == R.id.home) {

                    //deque list 사이즈가 0과 같지 않을 때
                    if (integerDeque.size() != 0) {

                        // flag값을 true일 때 덱리스트에 홈프래그먼트를 추가
                        if (flag) { //flag 디폴트 값은 true

                            integerDeque.addFirst(R.id.home);//첫번째에 홈 프래그를 추가하고

                            //flag를 false로 다시 set
                            flag = false;

                        }

                    }
                }
                //홈이 아닐 때
                //덱리스트에서 선택된 id 제거
                integerDeque.remove(id);
            }
            //덱리스트에 선택된 아이디를 push
            integerDeque.push(id);

            //선택한 프래그먼트 로드
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

                //쉐어드에 저장된 role이 어드민일때 (어드민계정일때)
                if(role.equals("admin")){
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);
                    return new MypageAdminFragment();

                }else{ //유저 계정일 때
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);
                    return new MypageFragment();

                }


        }
        //홈프래그컨트를 체크된 디폴트로 set
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        return new HomeFragment();
    }


    @Override
    public void onBackPressed() {

        //이전 프래그 먼트를 pop한다.
        integerDeque.pop();

        if(!integerDeque.isEmpty()){
            //덱 리스트가 비어 있지 않으면
            //프래그먼트를 로드 함.
            loadFragment(getFragment(integerDeque.peek())); // 얘는 이전에 눌렀던 프래그로 갈 수 있게 하고
            //loadFragment(getFragment(R.id.home)); //얘는 무조건 홈으로 가게 함.
        }else {
            //덱리스트가 비어있을 때
            //Main Activity를 끝냄.
            finish();
        }

    }







    @Override
    protected void onStop() {
        super.onStop();
//        unbindService(connection);
//        mBound = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);


        mBound = false;
    }


    public class fcm_request extends StringRequest {
        // 서버 URL 설정 ( PHP 파일 연동 )
        final static private String URL = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/sendCommentFCMRequest.php"; //내 서버 명
        private Map<String, String> map; // Key, Value로 저장 됨


        public fcm_request(String Token,Response.Listener<String> listener) {
            super(Method.POST, URL, listener, null);

            map = new HashMap<>();
            map.put("Token",Token);



        }
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return map;
        }
    }






}