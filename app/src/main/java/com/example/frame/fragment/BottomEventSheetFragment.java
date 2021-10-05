package com.example.frame.fragment;
import android.util.Log;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.frame.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BottomEventSheetFragment extends BottomSheetDialogFragment {
    private Button btn_bottom_event;



    public BottomEventSheetFragment() {
        // Required empty public constructor
    }


    public static BottomEventSheetFragment newInstance() {
        BottomEventSheetFragment fragment = new BottomEventSheetFragment();

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
        View view = inflater.inflate(R.layout.fragment_bottom_event_sheet, container, false);

        btn_bottom_event = view.findViewById(R.id.btn_bottom_event);

        btn_bottom_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("디버그태그", "응모했습니다.");
            }
        });


        return view;
    }
}