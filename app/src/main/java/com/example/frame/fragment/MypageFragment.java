package com.example.frame.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frame.EditProfileActivity;
import com.example.frame.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MypageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MypageFragment extends Fragment {
    TextView  btn_go_edit_profile;
    CardView mypage_img;

    public MypageFragment() {
        // Required empty public constructor
    }

    public static MypageFragment newInstance() {
        MypageFragment fragment = new MypageFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_mypage, container, false);
       btn_go_edit_profile = view.findViewById(R.id.btn_go_edit_profile);
       mypage_img = view.findViewById(R.id.mypage_img);


       View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.mypage_img :
                    case R.id.btn_go_edit_profile :
                        startActivity(new Intent(getActivity(), EditProfileActivity.class));
                        break;

                }
            }
        };

       //버튼 클릭 이벤트
        mypage_img.setOnClickListener(clickListener);
        btn_go_edit_profile.setOnClickListener(clickListener);

        return view;
    }



}