package com.example.frame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.FeedAdapter;
import com.example.frame.adapter.MyFeedAdapter;
import com.example.frame.adapter.TicketBoxAdapter;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataFeed;
import com.example.frame.etc.DataFeedImg;
import com.example.frame.etc.DataTicket;
import com.example.frame.etc.SessionManager;
import com.example.frame.fragment.NoFeedFragment;
import com.example.frame.fragment.NoTicketFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyFeedActivity extends AppCompatActivity {

    private static String URL_read_myfeed = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_myfeed.php";
    private FloatingActionButton btn_add_feed;
    RecyclerView recyclerView;
    private String user_id, feed_writer, feed_contents, feed_img, feed_time, feed_profile_img, feed_id, feed_user_id,feed_uid;
    RecyclerView.Adapter adapter;
    private ArrayList<DataFeed> feedList = new ArrayList<>();
    private JSONArray imagejArray = new JSONArray();
    private ArrayList<DataFeedImg> imgDataArray;
    private FeedAdapter.RecyclerViewClickListener clickListener;
    private RelativeLayout feed_container;
    NoFeedFragment noFeedFrag;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.fragment_feed);
        recyclerView = findViewById(R.id.rv_feed);
        feed_container = findViewById(R.id.feed_container);
        btn_add_feed = findViewById(R.id.btn_add_feed);


    ////////????????? ?????? ??? ?????????????????? ?????????

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String,String> user = sessionManager.getUserDetail();

         user_id = user.get(sessionManager.ID);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_add_feed :
//                        startActivity(new Intent(getActivity(), AddFeedActivity.class));
                        startActivity(new Intent(getApplicationContext(), AddFeedActivity2.class));
                        break;


                }
            }
        };

        btn_add_feed.setOnClickListener(clickListener);


        sendRequest();
        init_rv_search();


        Log.d("???????????????", "ticketArrayList ??????1. ");




    }

    private void init_rv_search(){
        //?????????????????? ??????

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//?????????????????? ????????? ??????
        recyclerView.setLayoutManager(layoutManager);

    }


    //????????? ?????? ?????????
    public void sendRequest() {
        String url = URL_read_myfeed;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("myfeed"); //"feed"?????? jsonArray??? php?????? ??????

                            //feed ????????? ??????
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                feed_writer = object.getString("name");
                                feed_contents = object.getString("feed_contents");

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

                                Log.d("soo", "php?????? json?????? ?????? array ???: " + jsonArray);
                                Log.d("soo", "php?????? json?????? ?????? imageArray ???: " + imagejArray);
                                Log.d("soo", "imgDataArray ???: " + imgDataArray);

                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Date date = fm.parse(feed_time);
                                Log.d("soo", "date ???: " + date);
                                beforeTime(date);
                                Log.d("soo", "beforeTime ???: " +  beforeTime(date));

                                //feed_uid = feed_id ???!!
                                feedList.add(new DataFeed(feed_writer,feed_contents,imgDataArray,beforeTime(date),feed_profile_img,feed_user_id,feed_id));

                            }


                            if(feedList.isEmpty()) {
                                ///////????????? ????????? ?????????????????? ?????????.
                                Log.d("???????????????", "ticketArrayList??? ???????????????. ");
                                noFeedFrag = new NoFeedFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.feed_container, noFeedFrag).commit();
                            }else {
                                Log.d("???????????????", "ticketArrayList is not null. ");
                                init_rv_search();

                                setOnClickListener(); //???????????? ??????????????????????????? ???????????? ??????(?????????????????? ????????? ?????????)
                                adapter = new FeedAdapter(getApplicationContext(), MyFeedActivity.this, feedList, clickListener);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                noFeedFrag = new NoFeedFragment();
                                getSupportFragmentManager().beginTransaction().remove(noFeedFrag).commit();
                            }


                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
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
                params.put("user_id",user_id);

                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext()); // requestQueue ????????? ??????
        AppHelper.requestQueue.add(request);

    }

    //?????????????????? ?????? ????????? - 8. ????????????????????? ????????? ??????????????? ????????? ?????????
    private void setOnClickListener() {
        clickListener = new FeedAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                //Intent intent = new Intent(getActivity(), DetailFeedActivity.class);
                //intent.putExtra("userName",feedList.get(position).getWriter());
//                intent.putExtra("feed_id",feed_id);



                Intent intent = new Intent(getApplicationContext(), DetailFeedActivity.class);
                intent.putExtra("feed_id",feedList.get(position).getFeed_uid());

                Log.d("feed_id ??????!!", feedList.get(position).getFeed_uid());
                startActivity(intent);
            }
        };
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













}
