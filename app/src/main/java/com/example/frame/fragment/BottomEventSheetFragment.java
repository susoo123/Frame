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

        //프래그먼트에서 쉐어드 사용법
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("LOGIN",Context.MODE_PRIVATE);
        user_id = mSharedPreferences.getString("ID", "");

        //응모하기 버튼 클릭시
        btn_bottom_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //RequestActivity에서 전달한 번들 저장
                Bundle bundle = getArguments();
                //번들 안의 텍스트 불러오기
                event_id = bundle.getString("event_id");
                //String img = bundle.getString("img");

                Log.d("이벤트 응모하기버튼클릭", "응모했습니다.");
                Log.d("쉐어드에 저장된 유저 아이디", user_id);
                Log.d("번들로 받은 event_id ", event_id);


                sendRequest();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                dialog = builder.setMessage("응모했습니다.")
                        .setPositiveButton("확인",null )
                        .create();
                dialog.show();
            }
        });


        return view;
    }






    //볼리 통신으로 쉐어드에 저장된 유저 아이디 보내기
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
                params.put("event_id",event_id);

                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }

}