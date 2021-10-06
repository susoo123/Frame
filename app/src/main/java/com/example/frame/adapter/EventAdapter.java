package com.example.frame.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.frame.R;
import com.example.frame.etc.DataEvent;
import com.example.frame.etc.DataFeed;
import com.example.frame.etc.OnPostListener;
import com.example.frame.etc.SessionManager;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<DataEvent> eventList = new ArrayList<>();
    Context context;
    private Activity activity;
    private RecyclerViewClickListener clickListener;//리사이클러뷰 클릭 이벤트 - 2. 리사이클러뷰클릭 리스너 변수 만들기
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    AlertDialog dialog;
    SessionManager sessionManager;
    private OnPostListener onPostListener;


//    public EventAdapter(Context context, Activity activity, ArrayList eventList, EventAdapter.RecyclerViewClickListener clickListener){
//        this.context = context;
//        this.activity = activity;
//        this.eventList = eventList;
//        this.clickListener = clickListener;
//
//    }

    public EventAdapter(Context context, ArrayList eventList, EventAdapter.RecyclerViewClickListener clickListener){
        this.context = context;
        this.activity = activity;
        this.eventList = eventList;
        this.clickListener = clickListener;

    }





    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent,false);
        EventViewHolder viewHolder = new EventViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        String contents =eventList.get(position).getEvent_contents();
        String title = eventList.get(position).getEvent_title();
        String poster = eventList.get(position).getPoster();
        String start_date = eventList.get(position).getEvent_start_date();
        String end_date = eventList.get(position).getEvent_end_date();



        Glide.with(holder.itemView.getContext())
                .load(poster)
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
                }).into(holder.event_img);

        //9번
        holder.setData(title,end_date);
    }

    @Override
    public int getItemCount() {

        return eventList.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView event_img;
        TextView event_title,event_start,event_end;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            event_img = itemView.findViewById(R.id.event_img);
            event_title = itemView.findViewById(R.id.event_title);
            //event_start = itemView.findViewById(R.id.event_start);
            event_end = itemView.findViewById(R.id.event_end);

            itemView.setOnClickListener(this); //이게 중요함!!

        }

        public void setData(String title,String end) {

            event_title.setText(title);
           // event_start.setText(start);
            event_end.setText(end);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);

    }
}
