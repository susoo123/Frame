package com.example.frame.fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;

import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.frame.DetailEventActivity;
import com.example.frame.LoginActivity;
import com.example.frame.R;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataEvent;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class BottomEventSheetFragment extends BottomSheetDialogFragment {
    private Button btn_bottom_event;
    private static String URL_after_click_event_btn ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/after_click_event_btn.php";
    private ArrayList<DataEvent> eventList = new ArrayList<>();
    private String user_id,event_id;
    private AlertDialog dialog;



    public BottomEventSheetFragment() {
        // Required empty public constructor
    }


    public static BottomEventSheetFragment newInstance() {
        BottomEventSheetFragment fragment = new BottomEventSheetFragment();

        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        if (getArguments() != null) {
//
//        }
//    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_event_sheet, container, false);

        btn_bottom_event = view.findViewById(R.id.btn_bottom_event);

        //????????????????????? ????????? ?????????
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("LOGIN",Context.MODE_PRIVATE);
        user_id = mSharedPreferences.getString("ID", "");

        //???????????? ?????? ?????????
        btn_bottom_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //RequestActivity?????? ????????? ?????? ??????
                Bundle bundle = getArguments();
                //?????? ?????? ????????? ????????????
                event_id = bundle.getString("event_id");
                //String img = bundle.getString("img");

                Log.d("????????? ????????????????????????", "??????????????????.");
                Log.d("???????????? ????????? ?????? ?????????", user_id);
                Log.d("????????? ?????? event_id ", event_id);


                sendRequest();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                dialog = builder.setMessage("??????????????????.")
                        .setPositiveButton("??????",null )
                        .create();
                dialog.show();
            }
        });


        return view;
    }






    //?????? ???????????? ???????????? ????????? ?????? ????????? ?????????
    private void sendRequest() {
        String url = URL_after_click_event_btn;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            //JSONArray jsonArray = jsonObject.getJSONArray("result");



                        } catch (JSONException e) {
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
                params.put("event_id",event_id);

                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue ????????? ??????
        AppHelper.requestQueue.add(request);

    }

}