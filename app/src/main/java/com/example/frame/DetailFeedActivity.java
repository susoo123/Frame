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
import java.util.zip.Inflater;

public class DetailFeedActivity extends AppCompatActivity {
    private ImageButton btn_feed_option;
    private TextView item_feed_username,item_feed_contents,item_feed_time;
    private ImageView item_feed_profile_image;
    String username, user_id;
    AlertDialog dialog;
    private static String URL_read_feed_detail = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_feed_detail.php";
    private static String URL_read_comments = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_comments.php";
    private static String URL_add_comment = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/add_comment.php";

    private FloatingActionButton btn_add_feed;
    private RecyclerView item_feed_image_rv, rv_comment;
    public  String UserID,UserName, feed_writer, feed_contents, feed_img, feed_id_i, feed_time, feed_profile_img, feed_id, feed_user_id, feed_uid;
    private String comment_id,comment_text,comment_date,comment_img,comment_name,comment;
    RecyclerView.Adapter adapter;
    private ArrayList<DataFeed> feedList = new ArrayList<>();
    private JSONArray imagejArray = new JSONArray();
    private ArrayList<DataFeedImg> imgDataArray;
    private ArrayList<DataComment> dataComments = new ArrayList<>();
    private EditText et_comment; //EditText ?????????
    private Button btn_send_comment;
    private Context context;

    Handler handler;
    String user_name, user_img,del_state;
    String textData;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_feed);

        item_feed_username = findViewById(R.id.item_feed_username);
        item_feed_contents = findViewById(R.id.item_feed_contents);
        item_feed_time = findViewById(R.id.item_feed_time);
        item_feed_profile_image = findViewById(R.id.item_feed_profile_image);
        et_comment = findViewById(R.id.et_comment_text); //??? ?????????


        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetail();

        UserName = user.get(sessionManager.NAME);
        UserID = user.get(sessionManager.ID); //????????? ????????? ?????????

        Intent intent = getIntent();
        feed_id_i = intent.getExtras().getString("feed_id");
        Log.e("????????????", feed_id_i);
        sendRequest();
        sendRequest2();

        init_rv();


        et_comment.getText();
        Log.e("????????????", String.valueOf(et_comment.getText()));

        btn_send_comment = findViewById(R.id.btn_send_comment);
        btn_send_comment.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Log.e("????????????", String.valueOf(et_comment.getText()));
              if(String.valueOf(et_comment.getText()) != null){

                  String comment_text2 = String.valueOf(et_comment.getText());

                  Log.e("????????????", String.valueOf(et_comment.getText()));
                  Log.e("writeComment()// comment_text", "comment_text ?????? : " + comment_text2);
                  writeComment();

//                  Comments comment = new Comments();
//                  comment.writeComment(comment_text2,getApplicationContext(),feed_id_i);



                  final Handler mhandler = new Handler();
                  Thread mthread = new Thread(new Runnable(){
                      @Override
                      public void run() {
                          mhandler.post(new Runnable(){
                              @Override
                              public void run() {
                                  et_comment.setText(""); //????????? ??????
                                  //????????? ???????????? ?????? ??????
                                  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                  imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);

                              }
                          });
                      }
                  });
                  mthread.start();




              }else{ //null ????????? ??????
                  Log.d("???????????????", "???????????");
              }


          }

        });



//        Bundle extras = getIntent().getExtras();
//        if(extras !=null){
//            username = extras.getString("userName");
//
//        }
//        item_feed_username.setText(username);


        //?????? ?????? ?????? ??????!! ?????? ????????????, ????????????
        btn_feed_option = findViewById(R.id.btn_feed_option);
        btn_feed_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.menu_feed, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.edit) {
                            Toast.makeText(DetailFeedActivity.this, "?????? 1 ??????", Toast.LENGTH_SHORT).show();
                        } else if (menuItem.getItemId() == R.id.del) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DetailFeedActivity.this);
                            dialog = builder.setMessage("????????? ?????????????????????????")
                                    .setNegativeButton("???", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            sendDelMessage();
                                        }
                                    })
                                    .setPositiveButton("??????", null)
                                    .create();

                            dialog.show();
                            Toast.makeText(DetailFeedActivity.this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                        }
                        return false;
                    }
                });


                popupMenu.show();
            }
        });


    }

    public void sendDelMessage() {
        String url = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/del_feed.php";
        String feed_id;


        StringRequest smpr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String post = jsonObject.getString("upload");

                    if (post.equals("1")) {

                        Toast.makeText(DetailFeedActivity.this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DetailFeedActivity.this, MainActivity.class));

                    } else {
                        Toast.makeText(DetailFeedActivity.this, "?????? ?????? ??????!!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }




        });

        //????????? ??????
        //smpr.setParams("feed_id", feed_id); //?????? ?????? ???????????? ????????? ????????? del_status ?????? ?????? ?????????!!
        //smpr.setParams("feed_uid", feed_uid);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(smpr);
    }

    private void init_rv(){// ?????? ?????? ???????????? ???????????? ????????? ??????( ?????????????????? )
        //?????????????????? ??????
        //item_feed_image_rv =findViewById(R.id.item_feed_image_rv);
        rv_comment = findViewById(R.id.rv_comment);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager layoutManagerComment = new LinearLayoutManager(this);

        //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//?????????????????? ?????????????????? ??????
        layoutManager.setOrientation(layoutManagerComment.VERTICAL);

        //item_feed_image_rv.setLayoutManager(layoutManager);
        rv_comment.setLayoutManager(layoutManagerComment);

    }


    //????????? ?????? ?????????
    public void sendRequest() {
        String url = URL_read_feed_detail;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("feed_detail"); //"feed"?????? jsonArray??? php?????? ??????

                            //feed ????????? ??????
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                feed_writer = object.getString("name");
                                feed_contents = object.getString("feed_contents");
                                feed_img = object.getString("profile_img");

                                //feed_img = object.getString("imageArray");
                                imagejArray = object.getJSONArray("imageArray"); //json????????? ?????? ??? ????????? ?????????(?????? jsonArray)

                                //jsonArray??? ArrayList??? ?????? ??????
                                if(imagejArray !=null){
                                    imgDataArray = new ArrayList<>();
                                    for (int j = 0; j < imagejArray.length(); j++){
                                        // imgDataArray.add(imagejArray.getString(j));

                                        //???????????? ?????? ??????????????????(?????? ??? ????????? ???????????? ??????) model class??? ?????????.
                                        imgDataArray.add(new DataFeedImg(imagejArray.getString(j)));
                                    }

                                }



                                feed_time = object.getString("feed_date");
                                feed_profile_img = object.getString("profile_img");
                                feed_user_id = object.getString("feed_user_id");
                                feed_id = object.getString("feed_id");


                                item_feed_username.setText(feed_writer);
                                item_feed_contents.setText(feed_contents);
                                Glide.with(getApplicationContext()) //?????? ????????? Context??? ?????? ??????
                                        .load(feed_img) //URL, URI ?????? ???????????? ????????? ??????
                                        .centerCrop()
                                        .into(item_feed_profile_image); //????????? ???????????? ?????? ??????(ex. ImageView)


                                Log.d("soo", "php?????? json?????? ?????? array ???: " + jsonArray);
                                Log.d("soo", "php?????? json?????? ?????? imageArray ???: " + imagejArray);
                                Log.d("soo", "imgDataArray ???: " + imgDataArray);

                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Date date = fm.parse(feed_time);
                                Log.d("soo", "date ???: " + date);
                                beforeTime(date);
                                Log.d("soo", "beforeTime ???: " +  beforeTime(date));
                                item_feed_time.setText(beforeTime(date));

                                //ImageSliderInFeedAdapter imageSliderInFeedAdapter = new ImageSliderInFeedAdapter(getApplicationContext(),imgDataArray);
                                //item_feed_image_rv.setAdapter(imageSliderInFeedAdapter);
                                //imageSliderInFeedAdapter.notifyDataSetChanged();
                                ////feed_uid = feed_id ???!!
                                feedList.add(new DataFeed(feed_writer,feed_contents,imgDataArray,beforeTime(date),feed_profile_img,feed_user_id,feed_id));

                            }






                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Log.d("soo1", "??????1 -> " + e.getMessage());
                        }

                        Log.d("soo1", "?????? -> " + response);
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

                params.put("feed_id_i",feed_id_i);
                Log.d("feed_id_i ????????? ?????? ??????", feed_id_i);



                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue ????????? ??????
        AppHelper.requestQueue.add(request);

    }




    //????????? ?????? ?????????
    public void sendRequest2() {

        String url = URL_read_comments;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("comment"); //"feed"?????? jsonArray??? php?????? ??????

                            //feed ????????? ??????
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                feed_id = object.getString("feed_id");
                                comment_id = object.getString("comment_id");
                                comment = object.getString("comments");
                                comment_name = object.getString("comment_name");

                                feed_time = object.getString("comment_date");
                                comment_img = object.getString("comment_img");




                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Date date = fm.parse(feed_time);
                                Log.d("soo", "date ???: " + date);
                                beforeTime(date);
                                Log.d("soo", "beforeTime ???: " +  beforeTime(date));
                                item_feed_time.setText(beforeTime(date));

                                CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(),dataComments);
                                rv_comment.setAdapter(commentAdapter);
                                commentAdapter.notifyDataSetChanged();
                                //feed_uid = feed_id ???!!
                                dataComments.add(new DataComment(comment_img,comment_name,comment,beforeTime(date),comment_id));

                            }



                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Log.d("soo1", "??????1 -> " + e.getMessage());
                        }

                        Log.d("soo1", "?????? -> " + response);
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

                params.put("feed_id_ii",feed_id_i);
                Log.d("feed_id_ii ????????? ?????? ??????", feed_id_i);

                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue ????????? ??????
        AppHelper.requestQueue.add(request);


    }


    //??? ??? ??? ?????? ?????? ??????????????? ?????????
    public String beforeTime(Date date){

        Calendar c = Calendar.getInstance();

        long now = c.getTimeInMillis(); //?????? ????????? ?????? ???????????? ?????????.
        long dateM = date.getTime(); // ?????? ????????? ????????? ????????????.
        long gap = now - dateM; // ?????? ?????? - ?????? ?????? ??????


        String ret = "";

//        ???       ???   ???
//        1000    60  60
        gap = (long)(gap/1000); // ?????????????????????
        long hour = gap/3600; // ???????????? ????????? ??????/3600(3600= 1????????? ?????? ????????????)
        gap = gap%3600; // 3600?????? ????????? ??? ?????????

        long min = gap/60; // 1?????? 60???
        long sec = gap%60; // ?????? 60(1?????? 60?????????)?????? ????????? ??? ??? ???????????? ?????? ???!!

        if(hour > 24){ // ?????? 24?????? ?????? (?????? ?????????)
            ret = new SimpleDateFormat("MM???dd???").format(date);
        }
        else if(hour > 0){ //?????? 0?????? ?????? (??? ???(hour)??? ??????????????? ?????? )
            ret = hour+"?????? ???";
        }
        else if(min > 0){
            ret = min+"??? ???";
        }
        else if(sec > 0){
            ret = "??????";
        }
        else{
            ret = new SimpleDateFormat("HH:mm").format(date);
        }
        return ret;

    }

    public void writeComment(){


        String url = URL_add_comment;

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                //String success = jsonObject.getString("success");
                                JSONArray jsonArray = jsonObject.getJSONArray("comment2"); //"feed"?????? jsonArray??? php?????? ??????

//                                //feed ????????? ??????
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
                                    Glide.with(getApplicationContext()) //?????? ????????? Context??? ?????? ??????
                                            .load(user_img) //URL, URI ?????? ???????????? ????????? ??????
                                            .centerCrop()
                                            .into(item_feed_profile_image); //????????? ???????????? ?????? ??????(ex. ImageView)

                                    SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                    Date date = fm.parse(comment_date);
                                    Log.d("soo", "date ???: " + date);
                                    beforeTime(date);
                                    Log.d("soo", "beforeTime ???: " +  beforeTime(date));
                                    item_feed_time.setText(beforeTime(date));
                                    dataComments.add(new DataComment(comment_img,comment_name,comment,beforeTime(date),comment_id));
                                    Log.e("dataComments ??????", "dataComments : " + dataComments);

                                }

                                //????????? ?????? ?????? ?????????
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
                                Log.d("soo1", "??????1 -> " + e.getMessage());
                            }

                            Log.d("soo1", "?????? -> " + response);
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
                    params.put("comment_text", String.valueOf(et_comment.getText()));
                    params.put("user_id", UserID);


                    return params;

                }
            };

            request.setShouldCache(false);
            AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue ????????? ??????
            AppHelper.requestQueue.add(request);
        }

    }



