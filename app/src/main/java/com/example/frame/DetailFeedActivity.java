package com.example.frame;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.etc.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.zip.Inflater;

public class DetailFeedActivity extends AppCompatActivity {
    private ImageButton btn_feed_option;
    private TextView item_feed_username;
    String username;
    AlertDialog dialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_feed);

        item_feed_username = findViewById(R.id.item_feed_username);

        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            username = extras.getString("userName");
        }
        item_feed_username.setText(username);




        //피드 옵션 메뉴 설정!! 피드 수정하기, 삭제하기
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
                            Toast.makeText(DetailFeedActivity.this, "메뉴 1 클릭", Toast.LENGTH_SHORT).show();
                        } else if (menuItem.getItemId() == R.id.del) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DetailFeedActivity.this);
                            dialog = builder.setMessage("피드를 삭제하시겠습니까?")
                                    .setNegativeButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            sendDelMessage();
                                        }
                                    })
                                    .setPositiveButton("취소",null )
                                    .create();

                            dialog.show();
                            Toast.makeText(DetailFeedActivity.this, "피드를 삭제했습니다.", Toast.LENGTH_SHORT).show();

                        }
                        return false;
                    }
                });


                popupMenu.show();
            }
        });



    }

    public void sendDelMessage(){
        String url = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/del_feed.php";
        String feed_id ;


        StringRequest smpr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String post = jsonObject.getString("upload");

                    if(post.equals("1")){

                        Toast.makeText(DetailFeedActivity.this, "피드 삭제 완료", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DetailFeedActivity.this, MainActivity.class));

                    }else{
                        Toast.makeText(DetailFeedActivity.this, "피드 삭제 실패!!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("soo", "전송 실패!!");
            }
        });

        //데이터 추가
        //smpr.setParams("feed_id", feed_id); //피드 고유 아이디를 보내서 서버에 del_status 삭제 상태 되도록!!
        //smpr.setParams("feed_uid", feed_uid);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(smpr);
    }




}