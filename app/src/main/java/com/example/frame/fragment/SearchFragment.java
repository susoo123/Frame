package com.example.frame.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;


//import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.frame.R;
import com.example.frame.adapter.SearchAdapter;

import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchFragment extends Fragment implements SearchAdapter.ItemClickListener {
    private ArrayList<DataModel> list = new ArrayList<>();

    private static String URL_api = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/apiService.php";
    String art_title;
    String art_place;
    String art_thumbnail;
    SearchAdapter adapter;
    RecyclerView recyclerView;
    CardView item_searchFrag;
    SearchView search_view;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        if(AppHelper.requestQueue != null) {
            AppHelper.requestQueue = Volley.newRequestQueue(getActivity());
        } //RequestQueue 생성


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        sendRequest();
        init_rv_search(view);

        //검색 기능 관련
        search_view = view.findViewById(R.id.search_view);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return false;
            }
        });




        return view;
    }

    private void init_rv_search(View view){
        //리사이클러뷰 관련
        recyclerView = view.findViewById(R.id.rv_search);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);//리사이클러뷰 가로로만드는 코드
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setNestedScrollingEnabled(false);
//        adapter = new SearchAdapter(getContext(),list);
//        recyclerView.setAdapter(adapter);

    }



    @Override
    public void onItemClick(DataModel dataModel) {
        Fragment fragment = Detail_ExhibitionFragment.newInstance(dataModel.getTitle());

        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        //transaction.replace(R.id.fragment_layout,fragment,"detail_fragment");

        transaction.hide(getActivity().getSupportFragmentManager().findFragmentByTag("SearchFragment"));
        transaction.add(R.id.fragment_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void sendRequest() {
        String url = URL_api;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Log.d("soo", "php에서 json으로 받은 Array 값: " + jsonArray);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                art_title = object.getString("title");
                                art_place = object.getString("place");
                                art_thumbnail = object.getString("thumbnail");

                                Log.d("soo", "php에서 json으로 받은 array 값: " + jsonArray);
                                Log.d("soo", "String title 값: " + art_title);
                                Log.d("soo", "String place 값: " + art_place);
                                Log.d("soo", "String thumbnail 값: " + art_thumbnail);

                                //art_thumbnail = "http://www.culture.go.kr/upload/rdf/21/03/rdf_2021033110325407198.jpg";
                                list.add(new DataModel(art_thumbnail,art_title, art_place));
                            }


                            adapter = new SearchAdapter(getContext(),list);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("soo1", "응답 -> " + response);
                    }


                    },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){

                            Log.d("soo1", "에러 -> " + error.getMessage());

                        }
                        }
                            ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                return params;

            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);

    }


}
