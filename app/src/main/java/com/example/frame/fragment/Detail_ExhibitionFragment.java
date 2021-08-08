package com.example.frame.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.frame.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Detail_ExhibitionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Detail_ExhibitionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private String mParam1;


    public Detail_ExhibitionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Detail_ExhibitionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Detail_ExhibitionFragment newInstance(String param1) {
        Detail_ExhibitionFragment fragment = new Detail_ExhibitionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail__exhibition, container, false);

        TextView titletv = view.findViewById(R.id.titletv);
        titletv.setText(mParam1);
        return view;
    }
}