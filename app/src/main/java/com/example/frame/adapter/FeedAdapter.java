package com.example.frame.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.frame.R;
import com.example.frame.etc.DataFeed;
import com.example.frame.etc.DataModel;

import java.util.ArrayList;
import java.util.zip.Inflater;


//리사이클러뷰 클릭 이벤트 - 1. 리사이클러뷰클릭 리스너 인터페이스 만들기
//리사이클러뷰 클릭 이벤트 - 2. 리사이클러뷰클릭 리스너 변수 만들기
//리사이클러뷰 클릭 이벤트 - 3. 뷰홀더에 implements View.OnClickListener 달기 (그리고 빨간 줄 없애줌...메소드 implements)
//리사이클러뷰 클릭 이벤트 - 4. 뷰홀더에 내에 클릭리스너 set
//리사이클러뷰 클릭 이벤트 - 5. onClick메소드 내에 리스너 set
//리사이클러뷰 클릭 이벤트 - 6.어댑터 생성자에 넣기
//리사이클러뷰 클릭 이벤트 - 7.어댑터 올릴 액티비티나 프래그먼트로 가서 어댑터 선언한 곳에 listener 넣기
//리사이클러뷰 클릭 이벤트 - 8. 프래그먼트(또는 액티비티)에서 사용할 클릭리스너 메소드 만들기

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private ArrayList<DataFeed> feedList = new ArrayList<>();
    Context context;
    private RecyclerViewClickListener clickListener;//리사이클러뷰 클릭 이벤트 - 2. 리사이클러뷰클릭 리스너 변수 만들기
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;



    //리사이클러뷰 클릭 이벤트 - . 6.어댑터 생성자에 넣기
    public FeedAdapter(Context context, ArrayList feedList, RecyclerViewClickListener clickListener) {
        this.context= context;
        this.feedList= feedList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent,false);



        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String contents =feedList.get(position).getContents();
        String writer = feedList.get(position).getWriter();
//        String feed_img = feedList.get(position).getFeed_img();
        String feed_time = feedList.get(position).getDate();
        String user_img = feedList.get(position).getUserImg();
        ArrayList imgList = feedList.get(position).getDataFeedImgList();

        String url ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/";

        //리사이클러뷰 어댑터 set
        ImageSliderInFeedAdapter imageSliderInFeedAdapter = new ImageSliderInFeedAdapter(context,imgList);
        //holder.imageRecyclerview.setHasFixedSize(true);
        // holder.imageRecyclerview.addItemDecoration(new LinePagerIndicatorDecoration()); //indicator 달기
        holder.imageRecyclerview.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        holder.imageRecyclerview.setAdapter(imageSliderInFeedAdapter);

//        for(int i =0; i < imgList.size(); i++) {
//            String feed_img = imgList.get(i);
//            String imgURL = url + feed_img;
//
//
//            Glide.with(holder.itemView.getContext())
//                    .load(imgURL)
//                    .override(800)
//                    .thumbnail(0.1f)
//                    .error(R.drawable.app_logo)
//                    .listener(new RequestListener<Drawable>() {
//                        @Override
//                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                            return false;
//                        }
//                    }).into(holder.item_feed_image);
//
//        }

        Glide.with(holder.itemView.getContext())
                .load(user_img)
                .override(100,100)
                .thumbnail(0.1f)
                .centerCrop()
                .error(R.drawable.app_logo)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.item_feed_profile_image);

        //9번
        holder.setData(contents,writer,feed_time);



    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    //리사이클러뷰 클릭 이벤트 - 3. 뷰홀더에 implements View.OnClickListener 달기
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_feed_image,item_feed_profile_image;
        TextView item_feed_contents,item_feed_username,item_feed_time ;
        private ImageButton btn_feed_option_container;
        RecyclerView imageRecyclerview;
        //private LinearLayout layoutIndicator;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //item_feed_image = itemView.findViewById(R.id.item_feed_image);

            //리사이클러뷰 연결
            imageRecyclerview =itemView.findViewById(R.id.item_feed_image_rv);


            item_feed_contents = itemView.findViewById(R.id.item_feed_contents);
            item_feed_username = itemView.findViewById(R.id.item_feed_username);
            item_feed_time = itemView.findViewById(R.id.feed_time);
            item_feed_profile_image = itemView.findViewById(R.id.item_feed_profile_image);

            btn_feed_option_container = itemView.findViewById(R.id.btn_feed_option_container);

            //리사이클러뷰 클릭 이벤트 - 4. 뷰홀더에 내에 클릭리스너 set
            itemView.setOnClickListener(this);

        }

        public void setData(String contents, String username, String time) {

            item_feed_contents.setText(contents);
            item_feed_username.setText(username);
            item_feed_time.setText(time);
        }

        //리사이클러뷰 클릭 이벤트 - 3-1. 뷰홀더에 implements View.OnClickListener 달고 매서드 implements하면 얘가 나옴.
        @Override
        public void onClick(View view) {

            //리사이클러뷰 클릭 이벤트 - 5. onClick메소드 내에 리스너 set
            clickListener.onClick(view, getAdapterPosition());


        }
    }

    //리사이클러뷰 클릭 이벤트 - 1. 리사이클러뷰클릭 리스너 인터페이스 만들기
    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }




}
