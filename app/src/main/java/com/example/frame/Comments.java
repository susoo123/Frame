package com.example.frame;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.frame.adapter.CommentAdapter;
import com.example.frame.adapter.FeedAdapter;
import com.example.frame.adapter.ImageSliderInFeedAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataComment;
import com.example.frame.etc.DataFeed;
import com.example.frame.etc.DataFeedImg;
import com.example.frame.etc.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Comments extends DetailFeedActivity {


    EditText inputComment;
    String feed_id_i;
    String UserID;

    private ArrayList<DataComment> dataComments = new ArrayList<>();
    private String comment_id,comment_date,comment_img,comment_name,comment;

    String user_name, user_img,feed_id, user_id, del_state;
    private RecyclerView item_feed_image_rv, rv_comment;
    private TextView item_feed_username,item_feed_contents,item_feed_time;
    private ImageView item_feed_profile_image;
    private String feed_writer,feed_contents;
    private static String URL_read_comments = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_comments.php";
    private static String URL_add_comment = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/add_comment.php";


    public Comments(){

    }



//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//      //  this.context = getApplicationContext();
//       // context =getApplicationContext();
//
//
//    }



//    public void writeComment(String textData2, Context context, String feed_id_i){
//
//        SessionManager sessionManager = new SessionManager(context);
//        HashMap<String, String> user = sessionManager.getUserDetail();
//        UserID = user.get(sessionManager.ID); //접속한 유저의 아이디
//
////        Intent intent = getIntent();
//      //  feed_id_i = intent.getExtras().getString("feed_id");
//
//
//        //String url = URL_add_comment;
//
//        if(textData2 != null) {
//            StringRequest request = new StringRequest(Request.Method.POST, url,
//                    new com.android.volley.Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            try {
//                                JSONObject jsonObject = new JSONObject(response);
//                                //String success = jsonObject.getString("success");
//                                JSONArray jsonArray = jsonObject.getJSONArray("comment2"); //"feed"라는 jsonArray를 php에서 받음
//
////                                //feed 어레이 풀기
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject object = jsonArray.getJSONObject(i);
//                                    feed_id = object.getString("feed_id");
//                                    comment_id = object.getString("comment_id");
//                                    comment = object.getString("comments");
//                                    comment_name = object.getString("comment_name");
//                                    comment_date = object.getString("comment_date");
//                                    comment_img = object.getString("comment_img");
//
//                                    item_feed_username.setText(feed_writer);
//                                    item_feed_contents.setText(feed_contents);
//                                    Glide.with(getApplicationContext()) //해당 환경의 Context나 객체 입력
//                                            .load(user_img) //URL, URI 등등 이미지를 받아올 경로
//                                            .centerCrop()
//                                            .into(item_feed_profile_image); //받아온 이미지를 받을 공간(ex. ImageView)
//
//                                    SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                                    Date date = fm.parse(comment_date);
//                                    Log.d("soo", "date 값: " + date);
//                                    beforeTime(date);
//                                    Log.d("soo", "beforeTime 값: " +  beforeTime(date));
//                                    item_feed_time.setText(beforeTime(date));
//                                    dataComments.add(new DataComment(comment_img,comment_name,comment,beforeTime(date),comment_id));
//                                    Log.e("dataComments 확인", "dataComments : " + dataComments);
//
//                                }
//
//                                //새롭게 적은 댓글 띄우기
//                                final Handler mhandler = new Handler();
//                                Thread mthread = new Thread(new Runnable(){
//                                    @Override
//                                    public void run() {
//                                        mhandler.post(new Runnable(){
//                                            @Override
//                                            public void run() {
//
//                                                CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(),dataComments);
//                                                rv_comment.setAdapter(commentAdapter);
//                                                commentAdapter.notifyItemInserted(dataComments);
//
//
//                                            }
//                                        });
//                                    }
//                                });
//                                mthread.start();
//
//
//
//
//                            } catch (JSONException | ParseException e) {
//                                e.printStackTrace();
//                                Log.d("soo1", "응답1 -> " + e.getMessage());
//                            }
//
//                            Log.d("soo1", "응답 -> " + response);
//                        }
//
//
//                    },
//                    new com.android.volley.Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//
//                        }
//
//
//                    }
//            ) {
//
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<>();
//
//                    params.put("feed_id_i", feed_id_i);
//                    params.put("comment_text", textData2);
//                    params.put("user_id", UserID);
//
//
//                    Log.e("확인", "textData2 : " + textData2);
//                    Log.e("확인", "feed_id_i : " + feed_id_i);
//                    Log.e("확인", "UserID : " + UserID);
//
//                    Log.d("디버그태그", "완료!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//
//
//                    return params;
//
//                }
//            };
//
//            request.setShouldCache(false);
//            AppHelper.requestQueue = Volley.newRequestQueue(context); // requestQueue 초기화 필수
//            AppHelper.requestQueue.add(request);
//        }

        public void writeComment(String textData2, Context context, String feed_id_i){


            String url = URL_add_comment;

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                //String success = jsonObject.getString("success");
                                JSONArray jsonArray = jsonObject.getJSONArray("comment2"); //"feed"라는 jsonArray를 php에서 받음

//                                //feed 어레이 풀기
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    feed_id = object.getString("feed_id");
                                    comment_id = object.getString("comment_id");
                                    comment = object.getString("comments");
                                    comment_name = object.getString("comment_name");
                                    comment_date = object.getString("comment_date");
                                    comment_img = object.getString("comment_img");

                                    item_feed_username.setText(feed_writer);
                                    item_feed_contents.setText(feed_contents);
                                    Glide.with(getApplicationContext()) //해당 환경의 Context나 객체 입력
                                            .load(user_img) //URL, URI 등등 이미지를 받아올 경로
                                            .centerCrop()
                                            .into(item_feed_profile_image); //받아온 이미지를 받을 공간(ex. ImageView)

                                    SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                    Date date = fm.parse(comment_date);
                                    Log.d("soo", "date 값: " + date);
                                    beforeTime(date);
                                    Log.d("soo", "beforeTime 값: " +  beforeTime(date));
                                    item_feed_time.setText(beforeTime(date));
                                    dataComments.add(new DataComment(comment_img,comment_name,comment,beforeTime(date),comment_id));
                                    Log.e("dataComments 확인", "dataComments : " + dataComments);

                                }

                                //새롭게 적은 댓글 띄우기
                                final Handler mhandler = new Handler();
                                Thread mthread = new Thread(new Runnable(){
                                    @Override
                                    public void run() {
                                        mhandler.post(new Runnable(){
                                            @Override
                                            public void run() {

                                                CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(),dataComments);
                                                rv_comment.setAdapter(commentAdapter);
                                                commentAdapter.notifyItemInserted(dataComments);


                                            }
                                        });
                                    }
                                });
                                mthread.start();




                            } catch (JSONException | ParseException e) {
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

                    params.put("feed_id_i", feed_id_i);
                    params.put("comment_text", textData2);
                    params.put("user_id", UserID);

                    Log.e("오류태그", "feed_id_i" +feed_id_i) ;


                    return params;

                }
            };

            request.setShouldCache(false);
            AppHelper.requestQueue = Volley.newRequestQueue(context); // requestQueue 초기화 필수
            AppHelper.requestQueue.add(request);
        }







    public void delComment(){

    }

}
