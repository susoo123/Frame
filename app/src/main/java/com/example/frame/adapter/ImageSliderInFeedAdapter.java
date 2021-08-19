package com.example.frame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frame.R;
import com.example.frame.etc.DataFeed;
import com.example.frame.etc.DataFeedImg;

import java.util.ArrayList;

public class ImageSliderInFeedAdapter extends RecyclerView.Adapter<ImageSliderInFeedAdapter.MyViewHolder> {
    private Context context;

    private ArrayList<DataFeed> feedList = new ArrayList<>();
    private ArrayList<DataFeedImg> sliderImage;
//    private String[] sliderImage;

    public ImageSliderInFeedAdapter(Context context, ArrayList<DataFeedImg> sliderImage) {
        this.context = context;
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public ImageSliderInFeedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_images_in_feed_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DataFeedImg dataFeedImg = sliderImage.get(position);

        String url ="http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/";
        String feed_img = url+dataFeedImg.getFeedImg();

        holder.bindSliderImage(feed_img);

//        for(int i =0; i < imgList.size(); i++) {
//            String feed_img = imgList.get(i);
//            String imgURL = url + feed_img;
//
//            holder.bindSliderImage(imgURL);
//        }

    }

    @Override
    public int getItemCount() {
        return (null != sliderImage ? sliderImage.size() : 0);
        //return sliderImage.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.image_vp2_in_item_feed);

        }
        public void bindSliderImage(String imageURL) {
            Glide.with(context)
                    .load(imageURL)
                    .into(mImageView);
        }
    }
}
