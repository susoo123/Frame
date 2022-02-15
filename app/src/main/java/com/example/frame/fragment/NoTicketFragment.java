package com.example.frame.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.frame.R;


public class NoTicketFragment extends Fragment {





    public NoTicketFragment() {
        // Required empty public constructor

    }

    public static NoTicketFragment newInstance() {//프래그먼트 간 이동을 위해 필요
        NoTicketFragment fragment = new NoTicketFragment();
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
        View view = inflater.inflate(R.layout.fragment_no_ticket, container, false);





        return view;

    }






}