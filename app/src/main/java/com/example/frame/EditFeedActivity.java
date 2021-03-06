package com.example.frame;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;

import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.frame.adapter.AddFeedImgAdapter;
import com.example.frame.etc.DataFeedImg;
import com.example.frame.etc.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


//?????? ???????????? ????????????
public class EditFeedActivity extends AppCompatActivity {

    //????????? ?????? id??? ????????????
    //?????? ????????? ????????????
    //??????id??? ???????????? ?????? ????????????
    private static String URL_display_feed = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/display_feed.php";

    private EditText feed_contents;
    private ImageButton btn_feed_camera;
    private TextView btn_upload_feed;
    private ImageView feed_img;

    RecyclerView recyclerView;
    AddFeedImgAdapter adapter;
    private ArrayList<String> feedImgArrayList = new ArrayList<>(); //pathList

    private  ArrayList<Uri> uriList = new ArrayList<>();
    private JSONArray imagejArray = new JSONArray(); //db??? ???????????? ?????? ????????? ?????????
    private ArrayList imgDataArray;
    private ArrayList imgDataArray2; //uri????????? ????????? ?????? ?????????
    private ArrayList removeImgArray = new ArrayList<>();


    private String feed_id;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);
        feed_contents = findViewById(R.id.feed_contents_et);
        btn_feed_camera = findViewById(R.id.feed_camera_ib);
        btn_upload_feed = findViewById(R.id.btn_upload_feed);
        //feed_img = findViewById(R.id.feed_img_iv);
        recyclerView = findViewById(R.id.rv_add_feed_img);

        //????????????????????? intent??? ?????? ?????? ???????????? feed_id??? ????????????, ????????? ?????? ??????.
        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            feed_id = extras.getString("feed_id");
        }

        //?????? ?????? ????????????
        displayFeed(feed_id);



        //?????? ????????? ?????? ????????? ??????
        btn_upload_feed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                sendFeedInfo();

            }
        });



        btn_feed_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(EditFeedActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                //?????? ?????? ?????? ?????? ?????? ??????!!
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // EXTRA_ALLOW_MULTIPLE??? true??? ?????? ???????????? ?????? ?????? ?????? ??????
                                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //???????????? uri??? ?????????.

                                launcher.launch(intent);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

            }
        });

    }




    //????????? ????????? ??????
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {

                    Intent data = result.getData(); //??????????????? ???????????? ?????????
                    Uri imgUri = data.getData(); //????????? uri


                    if(data == null){//??????????????? ????????? ???????????? ?????? ???

                        Toast.makeText(getApplicationContext(),"???????????? ???????????? ???????????????", Toast.LENGTH_SHORT).show();

                    }else {//???????????? ???????????? ????????? ??????


                        ClipData clipData = data.getClipData();
                        if (clipData == null) {
                            // getClipData()??? EXTRA_ALLOW_MULTIPLE??? ????????? ?????? ?????????????????? ???????????? ?????? ?????????
                            // null??? ??????????????? ?????? ??? ????????? ???????????? ?????? ????????? ?????????.
                            // ?????? ????????? ???????????? ?????? ??????????????? getClipdata()??? ?????? => getData()??? ???????????? ???

                            Log.d("soo", "?????? ?????? ?????? : " + String.valueOf(data.getData()));

                            uriList.add(imgUri);
                            //imgDataArray.add(imgUri);

                            adapter = new AddFeedImgAdapter(getApplicationContext(), imgDataArray);

                            recyclerView.setAdapter(adapter);   // ????????????????????? ????????? ??????
                            recyclerView.setLayoutManager(new LinearLayoutManager(EditFeedActivity.this, LinearLayoutManager.HORIZONTAL, true));     // ?????????????????? ?????? ????????? ??????



                        } else {//???????????? ????????? ????????? ??????
                            // ClipData clipData = data.getClipData();
                            Log.d("soo", "????????? ?????? ?????? : " + String.valueOf(clipData.getItemCount()));

                            if (clipData.getItemCount() > 5) {//????????? 5??? ????????? ??????

                                Toast.makeText(getApplicationContext(), "????????? 5????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();

                            } else {//????????? ????????? 1??? ?????? 5??? ???????????????
                                Log.d("soo", "?????? ?????? ?????? ??????");

                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Uri imageUri = clipData.getItemAt(i).getUri();//????????? ??????????????? uri ??? ?????????

                                    try {

                                        //uri ???  list ??? ?????????
                                        uriList.add(imageUri);
                                        Log.e("soo", "uriList2 : " + uriList);

                                        String path = getPathFromUri(imageUri);

                                        feedImgArrayList.add(path);
                                        imgDataArray.add(path);//????????????????????? ???????????? ?????????.
                                        Log.e("soo", "feedImgArrayList2 : " + feedImgArrayList);//?????? ?????? ??????
                                        Log.e("soo", "imgDataArray2 : " + imgDataArray); //??????????????? ????????????.. ?????? ?????? ?????? ????????? ?????? ??????.
                                        Log.e("soo", "imagejArray2 : " + imagejArray); //?????? ????????? ???????????? ?????? ??????



                                    } catch (Exception e) {

                                        Log.e("soo", "File select error!", e);
                                    }
                                }

                                adapter = new AddFeedImgAdapter(getApplicationContext(), imgDataArray);
                                recyclerView.setAdapter(adapter);   // ????????????????????? ????????? ??????
                                recyclerView.setLayoutManager(new LinearLayoutManager(EditFeedActivity.this, LinearLayoutManager.HORIZONTAL, true));     // ?????????????????? ?????? ????????? ??????


                            }
                        }
                    }


                }
            });






    //????????? ?????? ????????????(????????????????????? ????????? ????????? ????????????)
    public String getPathFromUri(Uri uri){

        //ContentResolver = content provider??? ????????? ?????? ???????????? ????????? ???????????? ?????????.
        //content provider ??? ?????????????????? ???????????? data??? ???????????? ??????????????? ???.
        //content provider??? ??????????????? ???????????? ?????? ??????????????? ????????? ???????????? ???????????? ???????????? ????????? ????????? content Resolver??? ?????????.
        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();

        return path;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendFeedInfo(){
        String url = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/update_feed.php";

        final String contents = this.feed_contents.getText().toString().trim();//?????? ?????? ??????


        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String post = jsonObject.getString("upload");

                    if(post.equals("1")){

                        Toast.makeText(EditFeedActivity.this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditFeedActivity.this, MainActivity.class));

                    }else{
                        Toast.makeText(EditFeedActivity.this, "?????? ?????? ??????!!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }




        }); //SimpleMultiPartRequest ???.


        //????????? ??????
        //smpr.addStringParam("user_id", user_id); //??????????????? ????????? ???????????? ????????? id
        smpr.addStringParam("contents", contents); //????????? ?????? ??????
        smpr.addStringParam("feed_id", feed_id); //?????? ????????????

        if(feedImgArrayList != null){
            smpr.addStringParam("cntImage", String.valueOf(feedImgArrayList.size()));//????????? ?????? ?????????
            //????????? ?????? ??????
            if(feedImgArrayList.size() > 0 ){ //???????????? ????????????
                for(int i = 0; i < feedImgArrayList.size(); i++){
                    smpr.addFile("image" + i, feedImgArrayList.get(i)); //????????? ??????????????? ?????????.
                    Log.d("soo","?????? ??? path ?????? : " + feedImgArrayList.get(i)); //?????? ?????? ????????? feedImgArrayList??? ?????????
                }
            }
        }

        String str_url ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/profile_image/";
        //contains(url)
        String dataImg = String.join(",",imgDataArray);

//        for(int j = 0; j < imgDataArray.size(); j++) {
//               String data = (String) imgDataArray.get(j);
//
//            if (data.contains(str_url)) {
//                smpr.addStringParam("urlStrArrayList", dataImg);
//                Log.d("soo", "?????? ??? urlArrayList ?????? : " + dataImg);
//            }
//        }
//
//            }else {
//
//                if(feedImgArrayList != null){
//                    smpr.addStringParam("cntImage", String.valueOf(feedImgArrayList.size()));//????????? ?????? ?????????
//                    //????????? ?????? ??????
//                    if(feedImgArrayList.size() > 0 ){ //???????????? ????????????
//                        for(int i = 0; i < feedImgArrayList.size(); i++){
//                            smpr.addFile("image" + i, feedImgArrayList.get(i)); //????????? ??????????????? ?????????.
//                            Log.d("soo","?????? ??? path ?????? : " + feedImgArrayList.get(i)); //?????? ?????? ????????? feedImgArrayList??? ?????????
//                        }
//                    }
//                }
//
//            }
//
//
//
//        }

        if(imgDataArray != null) {

            for(int j = 0; j < imgDataArray.size(); j++) {
                String data = (String) imgDataArray.get(j);

                if (data.contains(str_url)) {
                    smpr.addStringParam("urlStrArrayList", data);
                    //Log.e("soo", "?????? ??? urlArrayList ?????? : " + dataImg);
                    Log.e("soo", "?????? ??? urlArrayList ?????? : " + data);
                }

            }
            //smpr.addStringParam("urlStrArrayList", data);

            smpr.addStringParam("imgDataArray", String.join(",",imgDataArray)); //????????? ???...
            //smpr.addStringParam("imgDataArray2", String.valueOf(imgDataArray2));
            Log.d("soo","?????? ??? imgDataArray ?????? : " + String.join(",",imgDataArray));
        }

//        if(imgDataArray2 != null) {
//            smpr.addStringParam("imgDataArray2", String.join(",",imgDataArray2));
//            //smpr.addStringParam("imgDataArray2", String.valueOf(imgDataArray2));
//            Log.d("soo","?????? ??? imgDataArray2 ?????? : " + String.join(",",imgDataArray2));
//        }




        //???????????? request ????????? ?????? ?????? ?????? RequestQueue??? ????????????,
        //RequestQueue??? ????????? ????????? ????????? ????????? ????????? ????????? ?????? ??????.
        //???????????? RequestQueue?????? Request??? ????????? ResponseListener??? ????????? ????????????.
        //volley??????: ???????????? ????????? ????????? ????????? UI????????? ?????? ???????????? ?????? ????????? ??????.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(smpr);
    }


    //?????? ????????? ????????????, ?????????????????????
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 102:
                Snackbar.make(findViewById(R.id.rootId),"??????",Snackbar.LENGTH_LONG).show();
                adapter.RemoveItem(item.getGroupId()); //??????????????? ???????????? ????????? ???????????? ?????????.
                //adapter.notifyDataSetChanged();
                removeImgArray.add(item.getGroupId());
                Log.e("soo","????????? ????????? ?????????: "+removeImgArray);


                return true;

        }

        return super.onContextItemSelected(item);

    }



    //?????? ???????????? ??????id ????????? ?????? ?????? ?????? ???????????? ?????????
    private void displayFeed(String feed_id3) {

        //????????? ????????? content??? ??????????????? ???????????? ???...

        //????????? ????????? ?????? ????????????
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL_display_feed,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("feed_display");

                            if(success.equals("1")){
                                for (int i = 0; i < jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String db_contents = object.getString("feed_contents").trim();
                                    //????????? ????????? ???????????????..
                                    String feed_id = object.getString("feed_id").trim();

                                    imagejArray = object.getJSONArray("imageArray"); //json????????? ?????? ??? ????????? ?????????(?????? jsonArray)


                                    //jsonArray??? ArrayList??? ?????? ??????
                                    if(imagejArray !=null) {
                                        imgDataArray = new ArrayList<Uri>();
                                        for (int j = 0; j < imagejArray.length(); j++) {

                                            imgDataArray.add(imagejArray.getString(j));

                                        }
                                        Log.e("???????????????","imgDataArray : "+imgDataArray);
                                    }

//                                    // ????????? ???????????? ?????? ????????? ?????? ????????? arraylist ??????????????????
//                                    if(imagejArray !=null) {
//                                        imgDataArray2 = new ArrayList<Uri>();
//                                        for (int j = 0; j < imagejArray.length(); j++) {
//
//                                            imgDataArray2.add(imagejArray.getString(j));
//                                            Log.e("???????????????","imgDataArray2 : "+imgDataArray2);
//                                        }
//                                    }



                                    adapter = new AddFeedImgAdapter(getApplicationContext(), imgDataArray);
                                    recyclerView.setAdapter(adapter);   // ????????????????????? ????????? ??????
                                    recyclerView.setLayoutManager(new LinearLayoutManager(EditFeedActivity.this, LinearLayoutManager.HORIZONTAL, true));     // ?????????????????? ?????? ????????? ??????

                                    feed_contents.setText(db_contents);

                                }



                            }else {

                                Log.d("soo","?????? ??????/ ?????? ???????????? ??????.");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(EditFeedActivity.this, "?????? ????????????  ??????" + e.toString(),Toast.LENGTH_SHORT).show();
                            Log.d("soo","?????? ????????????  ??????: " +e.toString());

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }




                })


        {

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("feed_id",feed_id3);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }








}











