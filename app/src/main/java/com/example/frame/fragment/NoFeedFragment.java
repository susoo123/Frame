package com.example.frame.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.frame.R;


public class NoFeedFragment extends Fragment {





    public NoFeedFragment() {
        // Required empty public constructor

    }

    public static NoFeedFragment newInstance() {//프래그먼트 간 이동을 위해 필요
        NoFeedFragment fragment = new NoFeedFragment();
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
        View view = inflater.inflate(R.layout.fragment_no_feed, container, false);

        return view;

    }






}