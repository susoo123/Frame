package com.example.frame.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.frame.EditProfileActivity;
import com.example.frame.EventManageActivity;
import com.example.frame.R;
import com.example.frame.etc.SessionManager;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MypageAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MypageAdminFragment extends Fragment {
    TextView  btn_go_edit_profile;
    CardView mypage_img,btn_management_event;
    ImageView profile_img_iv;
    TextView profile_name;

    public MypageAdminFragment() {
        // Required empty public constructor
    }

    public static MypageAdminFragment newInstance() {
        MypageAdminFragment fragment = new MypageAdminFragment();

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
       View view = inflater.inflate(R.layout.fragment_admin_mypage, container, false);
       btn_go_edit_profile = view.findViewById(R.id.btn_go_edit_profile);
       mypage_img = view.findViewById(R.id.mypage_img);
       profile_img_iv = view.findViewById(R.id.profile_img_iv);
       profile_name = view.findViewById(R.id.textView);
        btn_management_event = view.findViewById(R.id.btn_management_event);

        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String,String> user = sessionManager.getUserDetail();
        String profile_img = user.get(sessionManager.PROFILE_IMG_PATH);
        String name = user.get(sessionManager.NAME);

        Glide.with(getContext())
                .load(profile_img)
                .centerCrop()
                .into(profile_img_iv);

        profile_name.setText(name);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.mypage_img :
                    case R.id.btn_go_edit_profile :
                        startActivity(new Intent(getActivity(), EditProfileActivity.class));
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        ft.detach(getParentFragment()).attach(getParentFragment()).commit();
                        break;

                    case R.id.btn_management_event:
                        startActivity(new Intent(getActivity(), EventManageActivity.class));
                        break;

                }
            }
        };


        //버튼 클릭 이벤트

        mypage_img.setOnClickListener(clickListener);
        btn_go_edit_profile.setOnClickListener(clickListener);
        btn_management_event.setOnClickListener(clickListener);


        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();


        //Toast.makeText(getActivity(), "onDetach()", Toast.LENGTH_SHORT).show();
    }

}