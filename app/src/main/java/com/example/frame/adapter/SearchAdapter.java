package com.example.frame.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.frame.Detail_exhibition;
import com.example.frame.EditProfileActivity;
import com.example.frame.R;
import com.example.frame.etc.DataModel;
import com.example.frame.etc.DataResult;
import com.example.frame.etc.Utils;
//import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    //어댑터에 필요한 6가지
    //1.on createViewHolder 2. bindViewHolder 3. getItemCount() 4. viewholder 클래스 새로 설정 5.리스트 설정
    //6. 어댑터 생성자


    //5번.
    private List<DataModel> list;
    //private List<DataResult> list;
//    private OnItemClickListener onItemClickListener;
    private ItemClickListener clickListener;
    private Activity activity;
    private CardView item_searchFrag;
    private Context context;

//    public SearchAdapter(List<DataResult> list, Context context) {
//        this.list = list;
//        this.context = context;
//    }

    //6번.
//    public SearchAdapter(List<DataModel> list, ItemClickListener clickListener){
//        this.list = list;
//        this.clickListener = clickListener;
//
//    }

    public SearchAdapter(List<DataModel> list, Activity activity,Context context){
        this.list = list;
        this.activity = activity;
        this.context =context;

    }


    //1번  리사이클러뷰 아이템 레이아웃 설정
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        //7번 아이템 레아아웃 설정
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_search_row, parent,false);

       return new MyViewHolder(view);
    }


    //2번
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //8번
        String resource =list.get(position).getRv_img1();
        String title = list.get(position).getTitle();
        String place = list.get(position).getPlace();


        Glide.with(holder.itemView.getContext())
                .load(resource)
                .override(100,100)
                .thumbnail(0.1f)
                .timeout(6000)
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
                }).into(holder.poster);

        //9번
        holder.setData(title,place);




       // holder.titleTextView.setText(list.get(position).getTitle());

//        item_searchFrag = view.findViewById(R.id.item_searchFrag);
////        item_searchFrag.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent intent = new Intent(getActivity(), Detail_exhibition.class);
////                startActivity(intent);
////            }
////        });

//        리사이클러뷰 아이템 클릭했을 때 주고 싶은 이벤트!!
//        holder.item_searchFrag.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickListener.onItemClick(list.get(position));
//
//                startActivity(new Intent(getActivity(), 이동하고 싶은 액티비티.class));
//
//
//
//
//            }
//        });
    }


    //3번
    @Override
    public int getItemCount() {
        //10번
        return list.size();

    }



    //4번 RecyclerView.ViewHolder를 상속받아야함.
    class MyViewHolder extends RecyclerView.ViewHolder {

        //11번 내가 xml에 만든 것에 기초해서 변수 설정하기
         ImageView poster;
         TextView title2;
         TextView place2;
         ProgressBar progressBar;
        // OnItemClickListener onItemClickListener;


        //상속 받으면 얘가 생김
        public MyViewHolder(View itemView){
            super(itemView);

            //12번 아이템xml과 연결해주기
            poster = itemView.findViewById(R.id.rv_img1);
            title2 = itemView.findViewById(R.id.tv_title_search_rv);
            place2 = itemView.findViewById(R.id.tv_place_search_rv);



        }




        //13번 데이터 메소드 만들기
        public void setData(String title, String place) {

            title2.setText(title);
            place2.setText(place);
        }
    }

    public interface ItemClickListener {

        public void onItemClick(DataModel dataModel);
    }



}
