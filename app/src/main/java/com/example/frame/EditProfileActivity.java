package com.example.frame;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.volley.AuthFailureError;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;


import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.example.frame.etc.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import com.example.frame.etc.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class EditProfileActivity extends AppCompatActivity {

private TextInputEditText email,name,birth,phone_num;
private TextView btn_edit_profile;
//private FloatingActionButton btn_watching_photo;
private ImageButton btn_watching_photo;
private static String URL_Edit_profile = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/edit_profile.php";
private static String URL_read_profile = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_profile.php";
//private static String URL_read_profile = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/read_profile.php";

SessionManager sessionManager;

private Bitmap bitmap;
CircleImageView profile_image;
ImageView photo;
String encodeImageString;
SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btn_watching_photo = findViewById(R.id.btn_photo_profile);
        //profile_image.findViewById(R.id.imageView3);
        photo = findViewById(R.id.imageView33);

        //??????????????? ????????? ?????????????????? ?????? ??????
        email = findViewById(R.id.til_profile_email);
        name = findViewById(R.id.til_name);
        birth = findViewById(R.id.til_date);
        phone_num = findViewById(R.id.til_phone);
        btn_edit_profile =findViewById(R.id.btn_edit_profile);



        //?????? ?????? ?????????

        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Edit_profile();
                //upload_img();
            }
        });



        sessionManager = new SessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetail();
        String sEmail = user.get(sessionManager.EMAIL);


        //????????? ???????????? ?????? ???????????? ?????? ???????
        btn_watching_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(EditProfileActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
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

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(EditProfileActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
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



        //????????? ????????? ?????? ????????????
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL_read_profile,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("profile");

                            if(success.equals("1")){
                                for (int i = 0; i < jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String db_name = object.getString("name").trim();
                                    String db_email = object.getString("email").trim();
                                    String db_birth = object.getString("birth").trim();
                                    String db_phone_num = object.getString("phone_num").trim();
                                    String db_profile_img = object.getString("profile_img").trim();

                                   // Log.e("soo","???????????? ??? ????????? ?????? ??????: " +db_profile_img);

                                    sharedPreferences = getSharedPreferences("LOGIN", 0);
                                    editor = sharedPreferences.edit();
                                    editor.putString("PROFILE_IMG_PATH",db_profile_img);
                                    editor.putString("NAME",db_name);
                                    editor.commit();

                                    Glide.with(EditProfileActivity.this)
                                            .load(db_profile_img)
                                            .centerCrop()
                                            .into(photo);


                                    email.setText(db_email);
                                    email.setEnabled(false); //????????? ??? ??????

                                    name.setText(db_name);
                                    birth.setText(db_birth);
                                    phone_num.setText(db_phone_num);
                                }



                            }else {

                                Log.d("soo","????????? ??????/ ????????? ???????????? ??????.");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(EditProfileActivity.this, "????????? ??????" + e.toString(),Toast.LENGTH_SHORT).show();
                            Log.d("soo","????????? ??????: " +e.toString());

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

                params.put("email",sEmail);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }



    //????????? ????????? ??????
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Intent intent = result.getData();
                        Uri filepath = intent.getData();

//                      imageview.setImageURI(uri);

                        Glide.with(EditProfileActivity.this)
                                .load(filepath)
                                .centerCrop()
                                .into(photo);

                        try{
                            InputStream inputStream = getContentResolver().openInputStream(filepath);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            photo.setImageBitmap(bitmap);
                            encodeBitmapImage(bitmap);

                        }catch (Exception e){

                        }



                    }
                }
            });



    //????????? ?????????
    private void encodeBitmapImage(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImageString = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT);

    }



    //??? ?????? ???????????? ?????????
    private void Edit_profile() {

        //????????? ?????? ?????????????????? editText??? ?????? ??????????????? ??????????????? ?????????.
        final String email = this.email.getText().toString().trim();
        final String name = this.name.getText().toString().trim();
        final String birth = this.birth.getText().toString().trim();
        final String phone_num = this.phone_num.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Edit_profile,
                new Response.Listener<String>() { //?????? ??????
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("user_info");

                            if(success.equals("1")){
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String db_name = object.getString("name").trim();
                                    String db_profile_img = object.getString("profile_img").trim();

                                    sharedPreferences = getSharedPreferences("LOGIN", 0);
                                    editor = sharedPreferences.edit();
                                    editor.putString("PROFILE_IMG_PATH",db_profile_img);
                                    editor.putString("NAME",db_name);
                                    editor.commit();

                                    Toast.makeText(EditProfileActivity.this, "??? ?????? ?????? ??????",Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                                }

                            }else {

                                Log.d("soo","????????? ??????/ ????????? ???????????? ??????.");
                            }
//                            JSONObject jsonObject = new JSONObject(response);
//                            //??????????????? ????????? success ????????? ??????.
//                            String success = jsonObject.getString("success");
//                            if(success.equals("1")){
//                                Toast.makeText(EditProfileActivity.this, "??? ?????? ?????? ??????",Toast.LENGTH_SHORT).show();
//
//                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                            Toast.makeText(EditProfileActivity.this, "???????????? ?????? ??????" + e.toString(),Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }



                })
        {

            //@org.jetbrains.annotations.Nullable
            @Override //??????????????? ????????? ????????? ???????????? ??????
            protected Map<String, String> getParams() {

                //????????? ????????? ????????? ??? ????????? ?????? ??????.
                Map<String,String> params = new HashMap<>();

                params.put("name",name);
                params.put("email",email);
                params.put("birth",birth);
                params.put("phone_num",phone_num);
                params.put("upload",encodeImageString);

                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);    //????????? ??????


    }











}