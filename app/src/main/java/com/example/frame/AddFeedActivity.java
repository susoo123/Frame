package com.example.frame;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RequestQueue;

import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
//import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.adapter.AddFeedImgAdapter;
import com.example.frame.etc.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import com.android.volley.AuthFailureError;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;

public class AddFeedActivity extends AppCompatActivity {

    private static String URL_create_feed = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/create_feed.php";
    private static String URL_create_feed2 = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/create_feed2.php";

    private EditText feed_contents;
    private ImageButton btn_feed_camera;
    private TextView btn_upload_feed;
    private ImageView feed_img;
    SessionManager sessionManager;
    Bitmap bitmap;
    private String encodeImageString;
    RecyclerView recyclerView;
    AddFeedImgAdapter adapter;
    private ArrayList<Uri> feedImgArrayList = new ArrayList<Uri>();
    private Dialog dialog;
    ProgressDialog progressDialog;
    private ArrayList<Uri> urlList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);
        feed_contents = findViewById(R.id.feed_contents_et);
        btn_feed_camera = findViewById(R.id.feed_camera_ib);
        btn_upload_feed = findViewById(R.id.btn_upload_feed);
        //feed_img = findViewById(R.id.feed_img_iv);
        recyclerView = findViewById(R.id.rv_add_feed_img);


        //?????? ????????? ?????? ????????? ??????
        btn_upload_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });


        btn_feed_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(AddFeedActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");


                                //?????? ?????? ?????? ?????? ?????? ??????!!
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // true??? ?????? ???????????? ?????? ?????? ?????? ??????
                                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //???????????? uri??? ?????????.
                                intent.setAction(Intent.ACTION_GET_CONTENT);//?????? ????????? ????????? ???
                                launcher.launch(intent);

                                //startActivityForResult()??? deprecated .
                                //startActivityForResult(intent, GET_GALLERY_IMAGE); //GET_GALLERY_IMAGE???

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
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Uri filepath = data.getData();

                        if (data == null) {
                            Toast.makeText(getApplicationContext(), "????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (data.getClipData() == null) {
                                Log.e("single choice: ", String.valueOf(data.getData()));
                                filepath = data.getData();
                                feedImgArrayList.add(filepath);

                                adapter = new AddFeedImgAdapter(getApplicationContext(), feedImgArrayList);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(AddFeedActivity.this, LinearLayoutManager.HORIZONTAL, true));

                            } else {
                                ClipData clipData = data.getClipData();
                                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                                if (clipData.getItemCount() > 5) {   // ????????? ???????????? 5??? ????????? ??????
                                    Toast.makeText(getApplicationContext(), "????????? 5????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                                } else {   // ????????? ???????????? 1??? ?????? 5??? ????????? ??????
                                    Log.e("soo", "multiple choice");

                                    for (int i = 0; i < clipData.getItemCount(); i++) {
                                        filepath = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.
                                        try {
                                            feedImgArrayList.add(filepath);  //uri??? list??? ?????????.

                                        } catch (Exception e) {
                                            Log.e("soo", "File select error", e);
                                        }
                                    }

                                    adapter = new AddFeedImgAdapter(getApplicationContext(), feedImgArrayList);
                                    recyclerView.setAdapter(adapter);   // ????????????????????? ????????? ??????
                                    recyclerView.setLayoutManager(new LinearLayoutManager(AddFeedActivity.this, LinearLayoutManager.HORIZONTAL, true));     // ?????????????????? ?????? ????????? ??????

                                }

                            }
                        }


//                        adapter = new AddFeedImgAdapter(AddFeedActivity.this,feedImgArrayList);
//                        recyclerView.setAdapter(adapter);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(AddFeedActivity.this,LinearLayoutManager.HORIZONTAL,true));
//
                        //feedImgArrayList.add(filepath);


                        try {
                            InputStream inputStream = getContentResolver().openInputStream(filepath);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            feed_img.setImageBitmap(bitmap);
                            encodeBitmapImage(bitmap);

                        } catch (Exception e) {

                        }


                    }
                }
            });


    //????????? ?????????
    private void encodeBitmapImage(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImageString = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);

    }


//    private void upload_feed() {
//        //????????? ?????? ?????????????????? editText??? ?????? ??????????????? ??????????????? ?????????.
//        final String contents = this.feed_contents.getText().toString().trim();
//        sessionManager = new SessionManager(this);
//        HashMap<String,String> user = sessionManager.getUserDetail();
//        String user_id = user.get(sessionManager.ID);
//
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_create_feed,
//                new Response.Listener<String>() { //?????? ??????
//                    @Override
//                    public void onResponse(String response) {
//                        try{
//                            JSONObject jsonObject = new JSONObject(response);
//                            String success = jsonObject.getString("success");
//
//                            if(success.equals("1")){
//
//                                Toast.makeText(AddFeedActivity.this, "?????? ????????? ??????",Toast.LENGTH_SHORT).show();
//
//                                startActivity(new Intent(AddFeedActivity.this, MainActivity.class));
//
//                            }else {
//
//                                Log.d("soo","?????? ??????1");
//
//                            }
//
//
//                        } catch (JSONException e) {
//
//                            e.printStackTrace();
//                            Toast.makeText(AddFeedActivity.this, "?????? ??????2" + e.toString(),Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                },
//                new Response.ErrorListener() {//?????? ??????
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast.makeText(AddFeedActivity.this, "??????" + error.toString(),Toast.LENGTH_SHORT).show();
//
//                    }
//                })
//        {
//
//
//            @Override //??????????????? ????????? ????????? ???????????? ??????
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                //????????? ????????? ????????? ??? ????????? ?????? ??????.
//                Map<String,String> params = new HashMap<>();
//
//                //????????? ??????????????? ???  = ?????? ??????, ?????? ?????????, ??????id
//                params.put("contents",contents);
//               // params.put("feed_img",encodeImageString);
//                params.put("user_id",user_id); //???????????? ???????????? ?????? id
//
//                return params;
//            }
//        };
//
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);    //????????? ??????
//
//    }


    // ???????????? ????????? ??????????????? ?????????
    private void uploadPost() {
        // ????????? ?????? ?????????
        //String post = feed_contents.getText().toString();
        final String contents = this.feed_contents.getText().toString().trim();
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String user_id = user.get(sessionManager.ID);

        // urlList??? json ????????? ??????
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < urlList.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("url", urlList.get(i));
//                jsonObject.put("urlTitle", urlTile.get(i));
//                jsonObject.put("urlImage", urlImage.get(i));
//                jsonObject.put("urlDescription", urlDescription.get(i));
                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // ?????? ?????? ?????? ?????? ??????[????????? String?????? ??????]
        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, URL_create_feed2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        // ????????? ??????
                        Toast.makeText(AddFeedActivity.this, "????????? ????????????????????????.", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        // ????????? ??????
                        Toast.makeText(AddFeedActivity.this, "?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }




        }
        );

        // ?????? ????????? ?????? ???????????? ??????
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("contents", contents);
//                map.put("user_id", user_id);
//                map.put("url", jsonArray.toString()); // json ????????? ???????????? ??????
//                map.put("cntImage", String.valueOf(feedImgArrayList.size())); // ????????? ?????? ??????
//
//                return map;
//            }

        smpr.addStringParam("contents", contents);
        smpr.addStringParam("user_id", user_id);
        smpr.addStringParam("url", jsonArray.toString());
        smpr.addStringParam("cntImage", String.valueOf(feedImgArrayList.size()));


        //????????? ?????? ?????? (pathList??? ????????? ????????? ?????? uri string ?????????)
        for (
                int i = 0; i < feedImgArrayList.size(); i++) {
            // uri ?????? ?????? ?????????
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(this, Uri.parse(String.valueOf(feedImgArrayList.get(i))), proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String abUri = cursor.getString(column_index);
            cursor.close();
            // ????????? ?????? ??????
            smpr.addFile("image" + i, abUri);
        }


        // ????????? ????????? ????????? ?????? ??????
//        RequestQueue requestQueue= Volley.newRequestQueue(this);
//        requestQueue.add(smpr);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(smpr);
    }

}





