package com.example.frame.fragment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.frame.ChatActivity2;
import com.example.frame.EditProfileActivity;
import com.example.frame.MainActivity;
import com.example.frame.MyFeedActivity;
import com.example.frame.R;
import com.example.frame.TicketBoxActivity;
import com.example.frame.adapter.ChatItemAdapter;
import com.example.frame.adapter.EventAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataChatItem;
import com.example.frame.etc.DataEvent;
import com.example.frame.etc.SessionManager;
import com.example.frame.ChatActivity;
import com.google.gson.JsonParser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MypageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MypageFragment extends Fragment {
    TextView  btn_go_edit_profile;
    CardView mypage_img;
    ImageView profile_img_iv;
    TextView profile_name;
    Button btn_myfeed, test_btn;
    private String ip = "52.79.204.252"; //aws 퍼플릭 ip
    private int port = 6137;
    TextView winner_num, register_num;
    Handler handler = new Handler();
    private String chat_date, chat_id, user_id_chat, chat_text, name, receiver, type;

    private static String URL_read_win_num ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_win_num.php";
    private static String URL_read_register_num ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_register_num.php";


    private static String URL_read_chat = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_chat.php";


    private CardView btn_qna,btn_myticket;
    private String name_user, user_id;
    private Handler mHandler;
    RecyclerView chatView;

    private ArrayList<DataChatItem> dataChat = new ArrayList<>();

    public MypageFragment() {
        // Required empty public constructor
    }

    public static MypageFragment newInstance() {
        MypageFragment fragment = new MypageFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_mypage, container, false);
       btn_go_edit_profile = view.findViewById(R.id.btn_go_edit_profile);
       mypage_img = view.findViewById(R.id.mypage_img);
       profile_img_iv = view.findViewById(R.id.profile_img_iv);
       profile_name = view.findViewById(R.id.textView);
       btn_qna = view.findViewById(R.id.btn_qna);
       btn_myticket = view.findViewById(R.id.btn_myticket);
        btn_myfeed = view.findViewById(R.id.btn_myfeed);
        test_btn = view.findViewById(R.id.test_btn);
        winner_num = view.findViewById(R.id.winner_num);
        //RequestActivity에서 전달한 번들 저장
        Bundle bundle = this.getArguments();
        //번들 안의 텍스트 불러오기
        Log.e("MF", "bundle2 :" + bundle);
        String read ="123";

        //채팅 관련 리사이클러뷰
//        chatView = view.findViewById(R.id.chatRView);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        chatView.setLayoutManager(layoutManager);


        //sendRequest();

        register_num = view.findViewById(R.id.register_num);
        //sendRequest2();


        Thread thread = new Thread(){
            @Override
            public void run(){
                try{

                    sendRequest();
                    sendRequest2();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();



        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String,String> user = sessionManager.getUserDetail();
        String profile_img = user.get(sessionManager.PROFILE_IMG_PATH);
        name_user = user.get(sessionManager.NAME);
        user_id = user.get(sessionManager.ID);

        Glide.with(getContext())
                .load(profile_img)
                .centerCrop()
                .into(profile_img_iv);

        profile_name.setText(name_user);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.mypage_img :
                    case R.id.btn_go_edit_profile :
                        startActivity(new Intent(getActivity(), EditProfileActivity.class));
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        ft.detach(getParentFragment()).attach(getParentFragment()).commit();
                        break;

                    case R.id.btn_qna:

                        Intent intent = new Intent(getActivity(), ChatActivity2.class);
                        intent.putExtra("username", name_user );
                        Log.d("디버그태그", name_user);
                        startActivity(intent);
                        //gettingChat();

                        break;

                    case R.id.btn_myticket:
                        //startActivity(new Intent(getActivity(), TicketBoxActivity.class));
                        Intent intent2 = new Intent(getActivity(), TicketBoxActivity.class);
                        intent2.putExtra("user_id", user_id );
                        Log.d("디버그태그", "user_id 확인 : " + user_id);
                        startActivity(intent2);
                        break;

                    case R.id.btn_myfeed:
                        //startActivity(new Intent(getActivity(), TicketBoxActivity.class));
                        Intent intent3 = new Intent(getActivity(), MyFeedActivity.class);
                        startActivity(intent3);
                        break;

//                    case R.id.test_btn:
//                        //startActivity(new Intent(getActivity(), TicketBoxActivity.class));
//                        Intent intent4 = new Intent(getActivity(), SocketService.class);
//                        startActivity(intent4);
//                        break;



                }
            }
        };
        //버튼 클릭 이벤트
        mypage_img.setOnClickListener(clickListener);
        btn_go_edit_profile.setOnClickListener(clickListener);
        btn_qna.setOnClickListener(clickListener);
        btn_myticket.setOnClickListener(clickListener);
        btn_myfeed.setOnClickListener(clickListener);
       // test_btn.setOnClickListener(clickListener);


        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();


        //Toast.makeText(getActivity(), "onDetach()", Toast.LENGTH_SHORT).show();
    }

    public void gettingChat(){

        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetail();

        String UserName = user.get(sessionManager.NAME);
        String UserID = user.get(sessionManager.ID); //접속한 유저의 아이디


        String url = URL_read_chat;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("chat"); //"feed"라는 jsonArray를 php에서 받음

//                                //feed 어레이 풀기
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

//                                Date date = fm.parse(chat_date);
//                                Log.d("soo", "date 값: " + date);
//                                beforeTime(date);
//                                Log.d("soo", "beforeTime 값: " +  beforeTime(date));

                            }
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

                            System.out.println("MyFragdataChat : " + dataChat);
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


                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue 초기화 필수
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
            ChatItemAdapter chatItemAdapter = new ChatItemAdapter(getContext(),dataChatItemArrayList);
            chatView.setAdapter(chatItemAdapter);
            chatView.scrollToPosition(dataChatItemArrayList.size() - 1);


        }
    }



    //당첨자
    private void sendRequest() {
        String url = URL_read_win_num;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject object = jsonArray.getJSONObject(0);
                            //jsonArray.get(0);
                            Log.d("디버그태그", "jsonArray.get(0)확인: "+jsonArray.get(0));

                            String num = object.getString("win_num");
                            winner_num.setText(num);

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
                params.put("user_id",user_id);

                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }








    //응모한 수
    private void sendRequest2() {
        String url = URL_read_register_num;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject object = jsonArray.getJSONObject(0);

                            String num = object.getString("register_num");
                            register_num.setText(num);

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
                params.put("user_id",user_id);

                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }







}