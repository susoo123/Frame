package com.example.frame.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.frame.Detail_exhibition;
import com.example.frame.EditProfileActivity;
import com.example.frame.LoginActivity;
import com.example.frame.MainActivity;
import com.example.frame.R;
import com.example.frame.adapter.SearchAdapter;

import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataModel;
import com.example.frame.etc.DataResult;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements SearchAdapter.ItemClickListener{
    private ArrayList<DataModel> list = new ArrayList<>();
    private static String URL_api = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/apiService.php";
    String art_title;
    String art_place;
    SearchAdapter adapter;
    RecyclerView recyclerView;
    CardView item_searchFrag;

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

//        //gson
//        Gson gson = new Gson();
//
//        DataModel data = new DataModel(art_title,art_place);
//        String dataTitle = gson.fromJson(art_title,data);


        init_rv_search(view);
        //buildListData();

//        item_searchFrag = view.findViewById(R.id.item_searchFrag);
//        item_searchFrag.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), Detail_exhibition.class);
//                startActivity(intent);
//            }
//        });
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

//

        //LoadJson();

    }



//    public void LoadJson(){
//        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

//        Call<DataResult> call;
//        call = apiInterface.getArtList();
//
//        call.enqueue(new Callback<DataResult>() {
//            @Override
//            public void onResponse(Call<DataResult> call, Response<DataResult> response) {
//
//                //adapter = new SearchAdapter(list,getContext());
//                recyclerView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onFailure(Call<DataResult> call, Throwable t) {
//
//            }
//        });
//    }

    private void buildListData(){

        //리사이클러뷰 데이터 관련


        Log.d("soo1","buildListData String title 값: "+art_title);
        Log.d("soo1","buildListData String place 값: "+art_place);


//        list.add(new DataModel(R.drawable.app_logo,"전시회제목3","전시회 장소"));
//        list.add(new DataModel(R.drawable.app_logo,"전시회제목4","전시회 장소"));
//        list.add(new DataModel(R.drawable.app_logo,"전시회제목5","전시회 장소"));
//        list.add(new DataModel(R.drawable.app_logo,"전시회제목6","전시회 장소"));
//        list.add(new DataModel(R.drawable.app_logo,"전시회제목7","전시회 장소"));
//        list.add(new DataModel(R.drawable.app_logo,"전시회제목8","전시회 장소"));
//        list.add(new DataModel(R.drawable.app_logo,"전시회제목9","전시회 장소"));
//        list.add(new DataModel(R.drawable.app_logo,"전시회제목10","전시회 장소"));




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
                                String art_thumbnail = object.getString("thumbnail");

                                Log.d("soo", "php에서 json으로 받은 array 값: " + jsonArray);
                                Log.d("soo", "String title 값: " + art_title);
                                Log.d("soo", "String place 값: " + art_place);
                                Log.d("soo", "String thumbnail 값: " + art_thumbnail);

                                list.add(new DataModel(R.drawable.app_logo, art_title, art_place));
                            }


                            adapter = new SearchAdapter(list,null);
                            recyclerView.setAdapter(adapter);
                            adapter .notifyDataSetChanged();

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




//        StringRequest request = new StringRequest(
//                Request.Method.GET,
//                url,
//                new Response.Listener<String>() { //응답을 잘 받았을 때 이 메소드가 자동으로 호출
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            //JSONObject jsonObject = new JSONObject(response);
//                            //JsonParser jsonParser = new JsonParser();
//                            JSONArray jsonArray = new JSONArray(response);
//                            Log.d("soo","php에서 json으로 받은 Array 값: "+jsonArray);
//
//                            for(int i=0; i<jsonArray.length(); i++) {
//                                JSONObject object = jsonArray.getJSONObject(i);
//                                art_title = object.getString("title");
//                                art_place = object.getString("place");
//                                String art_thumbnail = object.getString("thumbnail");
//
//                                Log.d("soo","php에서 json으로 받은 array 값: "+jsonArray);
//                                Log.d("soo","String title 값: "+art_title);
//                                Log.d("soo","String place 값: "+art_place);
//                                Log.d("soo","String thumbnail 값: "+art_thumbnail);
//
//                                //list.add(new DataModel(art_title,art_place));
//
//                                //list.add(new DataModel(R.drawable.app_logo,art_title,art_place));
////                                list.add(new DataModel(R.drawable.app_logo,"제목","장소"));
//                                Log.d("soo1","리스트에 넣은 이후 String title 값: "+art_title);
//                                Log.d("soo1","리스트에 넣은 이후 String place 값: "+art_place);
//                                //buildListData();
//                            }
//
//
//                            list.add(new DataModel(R.drawable.app_logo,art_title,art_place));
//
//
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//
//
//                        list.add(new DataModel(R.drawable.app_logo,"제목1","장소1"));
////
////                                //list.add(new DataModel(title));
////                                //Log.d("soo","json list 값: "+list);
////
////                            }
//                        // Log.d("soo","list 값: "+list);
//
//                        Log.d("soo1","응답 -> " + response);
//
//                    }
//                },
////                new Response.ErrorListener() { //에러 발생시 호출될 리스너 객체
////                    @Override
////                    public void onErrorResponse(VolleyError error) {
////                       Log.d("soo1","에러 -> " + error.getMessage());
////                    }
////                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String,String>();
//
//                return params;
//            }
//        };
//        request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
//        AppHelper.requestQueue = Volley.newRequestQueue(getContext()); // requestQueue 초기화 필수
//        AppHelper.requestQueue.add(request);
//
//
//
//    }
//
//
//}

