package com.example.frame.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.frame.R;
import com.example.frame.etc.HomePosterItem;

import java.util.List;

public class HomePosterAdapter extends RecyclerView.Adapter<HomePosterAdapter.HomePosterHolder> {

    private List<HomePosterItem> homePosterItemArrayList;
    private ViewPager2 viewPager2;
    private CardView cardView;
    private TextView title,place;


    public HomePosterAdapter(List<HomePosterItem> homePosterItemArrayList, ViewPager2 viewPager2) {
        this.homePosterItemArrayList = homePosterItemArrayList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public HomePosterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_poster_container,parent,false);

        HomePosterHolder holder = new HomePosterHolder(cardView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomePosterAdapter.HomePosterHolder holder, int position) {

        holder.setImage(homePosterItemArrayList.get(position));

        //포스터 이미지 자동 넘김 애니메이션
        if(position == homePosterItemArrayList.size()-2){
            viewPager2.post(runnable); // 아래 runnable 선언되어 있음.
        }

    }

    @Override
    public int getItemCount() {
        return homePosterItemArrayList.size();
    }

    public class HomePosterHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        public HomePosterHolder(@NonNull CardView itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.home_poster_img_item);//레이아웃 내의 컴포넌트와 연결
            title = itemView.findViewById(R.id.title_poster);
            place = itemView.findViewById(R.id.place_poster);
        }

        public void setImage(HomePosterItem homePosterItem) {
            imageView.setImageResource(homePosterItem.getImage());
            title.setText(homePosterItem.getTitle());
            place.setText(homePosterItem.getPlace());
        }
    }

    //포스터 이미지 자동 넘김 애니메이션
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            homePosterItemArrayList.addAll(homePosterItemArrayList);
            notifyDataSetChanged();
        }
    };
}
