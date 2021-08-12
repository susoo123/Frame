package com.example.frame.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private ArrayList<DataFeed> feedList = new ArrayList<>();
    Context context;

    public FeedAdapter(Context context, ArrayList feedList) {
        this.context= context;
        this.feedList= feedList;
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
        String feed_img = feedList.get(position).getFeed_img();
        String feed_time = feedList.get(position).getDate();
        String user_img = feedList.get(position).getUserImg();

        Glide.with(holder.itemView.getContext())
                .load(feed_img)
                .override(800)
                .thumbnail(0.1f)
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
                }).into(holder.item_feed_image);

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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView item_feed_image,item_feed_profile_image;
        TextView item_feed_contents,item_feed_username,item_feed_time ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_feed_image = itemView.findViewById(R.id.item_feed_image);
            item_feed_contents = itemView.findViewById(R.id.item_feed_contents);
            item_feed_username = itemView.findViewById(R.id.item_feed_username);
            item_feed_time = itemView.findViewById(R.id.feed_time);
            item_feed_profile_image = itemView.findViewById(R.id.item_feed_profile_image);

        }

        public void setData(String contents, String username, String time) {

            item_feed_contents.setText(contents);
            item_feed_username.setText(username);
            item_feed_time.setText(time);
        }

    }


}
