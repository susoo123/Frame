package com.example.frame.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.frame.MainActivity;
import com.example.frame.R;
import com.example.frame.adapter.HomePosterAdapter;

import com.example.frame.etc.HomePosterItem;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    TextView tv_goEvent_home;

    //뷰페이저2 변수 선언
    private ViewPager2 viewPager2;


    public HomeFragment() {
        // Required empty public constructor

    }

    public static HomeFragment newInstance() {//프래그먼트 간 이동을 위해 필요
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        //변수 선언 후 레이아웃이랑 연결하는 것 잊지 말기!!
        tv_goEvent_home = view.findViewById(R.id.tv_goEvent_home);

        //이벤트 제목 누르면 이벤트 프래그먼트로 이동
        tv_goEvent_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // getActivity()로 MainActivity의 replaceFragment를 불러옵니다.
                ((MainActivity)getActivity()).replaceFragment(EventFragment.newInstance());    // 새로 불러올 Fragment의 Instance를 Main으로 전달


            }
        });




        //뷰페이지이미지슬라이더(프래그먼트1레이아웃에 뷰페이저2로 선언되어 있음.) 연결.
        viewPager2 = view.findViewById(R.id.rv_home_poster_big_container);

        //드로어블로 부터 이미지 리스트 준비 [슬라이더 아이템]은 그냥 이름
        List<HomePosterItem> homePosterItemArrayList = new ArrayList<>();
        homePosterItemArrayList.add(new HomePosterItem(R.drawable.app_logo)); //내가 뷰페이저2에 띄울 이미지들. 드로어블에 들어가 있어야함.
        homePosterItemArrayList.add(new HomePosterItem(R.drawable.poster));
        homePosterItemArrayList.add(new HomePosterItem(R.drawable.app_logo));


        Handler handler = new Handler();


        //뷰페이저2에 어뎁터를 셋한다. 어떤 어댑터냐하면 선언해준 자바클래스 슬라이더 어댑터.
        viewPager2.setAdapter(new HomePosterAdapter(homePosterItemArrayList, viewPager2));


        //이건 이미지에 변형 효과 주려고 짠 코드. 스와이프될 때 변형효과를 줌.
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);

            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable); //위에 선언한 핸들러 가져오고, 리무브콜백안에 아래 선언한 러너블 넣어줌.
                handler.postDelayed(runnable, 2000);//2초마다 딜레이
            }
        });

        return view;

    }

    //이미지 뷰 자동으로 넘어가는 애니메이션
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() +1 );
        }
    };




}