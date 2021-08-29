package com.example.frame.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frame.AddEventActivity;
import com.example.frame.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class EventFragment extends Fragment {
    FloatingActionButton btn_add_event;

    public EventFragment() {
        // Required empty public constructor
    }


    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();

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
       View view = inflater.inflate(R.layout.fragment_event, container, false);
       btn_add_event = view.findViewById(R.id.btn_add_event);
       btn_add_event.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getActivity(), AddEventActivity.class));
           }
       });


        return view;
    }
}