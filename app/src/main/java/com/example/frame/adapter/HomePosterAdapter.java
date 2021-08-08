package com.example.frame.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.frame.R;
import com.example.frame.etc.HomePosterItem;

import java.util.List;

public class HomePosterAdapter extends RecyclerView.Adapter<HomePosterAdapter.HomePosterHolder> {

    private List<HomePosterItem> homePosterItemArrayList;
    private ViewPager2 viewPager2;


    public HomePosterAdapter(List<HomePosterItem> homePosterItemArrayList, ViewPager2 viewPager2) {
        this.homePosterItemArrayList = homePosterItemArrayList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public HomePosterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomePosterHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_home_poster_container,parent,false //내가 연결할 아이템xml이름 넣기
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull HomePosterAdapter.HomePosterHolder holder, int position) {

        holder.setImage(homePosterItemArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return homePosterItemArrayList.size();
    }

    public class HomePosterHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        public HomePosterHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.home_poster_img_item);//레이아웃 내의 컴포넌트와 연결
        }

        public void setImage(HomePosterItem homePosterItem) {
            imageView.setImageResource(homePosterItem.getImage());
        }
    }
}
