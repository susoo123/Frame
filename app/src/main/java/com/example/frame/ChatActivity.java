package com.example.frame;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.ChatItemAdapter;
import com.example.frame.adapter.SearchAdapter;
import com.example.frame.etc.DataChat;
import com.example.frame.etc.DataChatItem;
import com.example.frame.etc.DataModel;
import com.example.frame.etc.SessionManager;
import com.example.frame.etc.ViewType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//소켓통신(클라이언트)
//1. 클라이언트가 ip/포트번호로 지정된 서버에 연결 시작
//2. OutputStream 사용해 서버에 데이터 전송
//3. InputStream을 사용해 서버에서 데이터 읽기
//4. 연결 종료


public class ChatActivity extends AppCompatActivity {
    private Handler mHandler;
//    InetAddress serverAddr;
    Socket socket;
    PrintWriter sendWriter;
    private String ip = "52.79.204.252"; //aws 퍼플릭 ip
    private int port = 6137;
    private static String URL_create_chat ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/create_chat.php";

    TextView textView;
    String UserID,user_id,UserName;
    private String chat_date,chat_id,user_id_chat,chat_text,name,receiver;
    Button connectbutton;
    Button chatbutton;
    RecyclerView chatView;
    EditText inputChat;
    String sendmsg;
    private ChatItemAdapter adapter;

    private ArrayList<DataChat> dataList = new ArrayList<>();
    private ArrayList<DataChatItem> dataChat = new ArrayList<>();
    ArrayList readArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mHandler = new Handler();
        textView = (TextView) findViewById(R.id.chat_user_name);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);




        inputChat = (EditText) findViewById(R.id.message);
        Intent intent = getIntent();
        user_id = intent.getExtras().getString("user_id");
        //username = intent.getStringExtra("username");


        chatbutton = (Button) findViewById(R.id.chatbutton);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String,String> user = sessionManager.getUserDetail();

        UserName = user.get(sessionManager.NAME);
        UserID = user.get(sessionManager.ID);
        textView.setText("1:1 문의사항");
        Log.d("UserID", UserID);


        //채팅 리사이클러뷰
        chatView = findViewById(R.id.chatRView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatView.setLayoutManager(layoutManager);
        ChatItemAdapter chatItemAdapter = new ChatItemAdapter(dataChat);
        chatView.setAdapter(chatItemAdapter);



        new Thread() {
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

                    //getInputStream(서버에서 데이터 읽기 위해 소켓에서 가져와야할 객체)
                    //->InputStreamReader->BufferedReader
                    InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    //BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                    List<DataChatItem> dataChatItemArrayList = new ArrayList<> ();
                    JSONArray chatJsonArray = new JSONArray();

                    while(true){
                        String read = reader.readLine();

                        try{
                            JSONArray ja = new JSONArray(read);
                            Log.d("soo", "ja 값: " + ja.toString());
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject object = ja.getJSONObject(i);
                                chat_id = object.getString("chat_id");
                                user_id_chat = object.getString("user_id_chat");
                                chat_text = object.getString("chat_text");
                                chat_date = object.getString("chat_date");
                                name = object.getString("name");
                                receiver = object.getString("receiver");

                                if(name.equals(UserName)) { //user가 쓴 채팅이면
                                    dataChat.add(new DataChatItem(chat_text, name, chat_date,receiver, 2));
                                }else if(user_id_chat.equals("50") && receiver.equals(UserID)) { //관리자가 쓴 채팅이면
                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, receiver,1));
                                }
//                                }else {
//                                    dataChat.add(new DataChatItem(chat_text, name, chat_date, 1));
//                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace ( );
                        }


                        System.out.println("Chat msg: "+read);

                        if(read!=null){//인풋이 있으면 (null 아니면)
                            mHandler.post(new msgUpdate(read)); //
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } }}.start();


        //채팅 버튼을 누르면 서버로 데이터가 보내짐. (message가 EditText임)
        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = inputChat.getText().toString();


                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            //메세지창에 뜨는 메세지

                            //서버에 데이터 보내기
                            // PrintWirte에 OutputStream을 래핑하여 다음과 같이 데이터를 텍스트 형식으로 보낼 수 있음.
                            sendWriter = new PrintWriter(socket.getOutputStream());//소켓에서 getOutputStream객체를 가져옴 (서버에 데이터 보내기 위함)
                            sendWriter.println(UserID +"=>"+ sendmsg);
                            sendWriter.flush();
                            inputChat.setText("");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(inputChat.getWindowToken(),0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    class msgUpdate implements Runnable{
        private String msg;
        public msgUpdate(String str) {this.msg=str;}

        @Override
        public void run() {
            adapter = new ChatItemAdapter(dataChat);
            chatView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //chatView.setText(chatView.getText().toString()+msg+"\n");
        }
    }



//    @Override
//    protected void onStop() {
////        super.onStop();
////        try {
////            sendWriter.close();
////            socket.close();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }




//
    }



//    private Socket clientSocket;
//    private PrintWriter out;
//    private BufferedReader in;
//    private String response;
//
//    public void startConnection(String ip, int port) throws IOException {
//        clientSocket = new Socket(ip, port);
//        out = new PrintWriter(clientSocket.getOutputStream(), true);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//    }
//
//    public String sendMessage(String msg) throws IOException {
//        out.println(msg);
//        String resp = in.readLine();
//        return resp;
//    }
//
//    public void stopConnection() throws IOException {
//        in.close();
//        out.close();
//        clientSocket.close();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
////        mHandler = new Handler();
////        textView = (TextView) findViewById(R.id.chat_user_name);
////        chatView = (TextView) findViewById(R.id.chatView);
////        message = (EditText) findViewById(R.id.message);
////        Intent intent = getIntent();
////        UserID = intent.getStringExtra("username");
////        textView.setText(UserID);
////        chatbutton = (Button) findViewById(R.id.chatbutton);
//
//        ChatActivity client = new ChatActivity();
//
//        new Thread(){
//             @Override
//             public void run(){
//                 try{
//                     client.startConnection("52.79.204.252", 6137);
//                     response = client.sendMessage("hello server");
//
//                 }catch(IOException e){
//                     e.printStackTrace();
//                 }
//             }
//          } .start();
//
//
//    }
//
//
////    @Test
////    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws IOException {
////        testActivity client = new testActivity();
////        client.startConnection("52.79.204.252", 6137);
////        String response = client.sendMessage("hello server");
////        assertEquals("hello client", response);
////    }
//



//}

