package com.example.frame.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frame.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BottomEventSheetFragment extends BottomSheetDialogFragment {



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
        return inflater.inflate(R.layout.fragment_bottom_event_sheet, container, false);
    }
}