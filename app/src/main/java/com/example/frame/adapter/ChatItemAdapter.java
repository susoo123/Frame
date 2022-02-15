package com.example.frame.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.frame.R;
import com.example.frame.etc.DataChatItem;
import com.example.frame.etc.ViewType;

import java.util.ArrayList;

public class ChatItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<DataChatItem> myDataList = null;
        private Context context;

        public ChatItemAdapter(Context context, ArrayList<DataChatItem> dataList) {
    
            this.context = context;
            this.myDataList = dataList;
        }

//    void MyAdapter(ArrayList<DataChatItem> dataList)
//        {
//            myDataList = dataList;
//        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view;
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(viewType == ViewType.CENTER_JOIN)
            {
                view = inflater.inflate(R.layout.center_join, parent, false);
                return new CenterViewHolder(view);


            }
            else if(viewType == ViewType.LEFT_CHAT)
            {
                view = inflater.inflate(R.layout.item_left_chat, parent, false);
                return new LeftViewHolder(view);
            }
            else //if (viewType == ViewType.RIGHT_CHAT) //viewType == ViewType.CENTER_JOIN
            {

                view = inflater.inflate(R.layout.item_right_chat, parent, false);
                return new RightViewHolder(view);
            }



        }


    @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
        {
            if(viewHolder instanceof CenterViewHolder)
            {
                ((CenterViewHolder) viewHolder).content.setText(myDataList.get(position).getItem_chat_content());
            }
             else if(viewHolder instanceof LeftViewHolder)
            {
                ((LeftViewHolder) viewHolder).name.setText(myDataList.get(position).getItem_chat_user());

                if(myDataList.get(position).getType().equals("1")) {
                    ((LeftViewHolder) viewHolder).content.setVisibility(View.GONE);
                    ((LeftViewHolder) viewHolder).time_text.setVisibility(View.GONE);
                    ((LeftViewHolder) viewHolder).time_img.setText(myDataList.get(position).getItem_chat_time());


                    Glide.with(viewHolder.itemView.getContext())
                            .load(myDataList.get(position).getItem_chat_content())
                            .override(200, 200)
                            .thumbnail(0.1f)
                            .centerCrop()
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(((LeftViewHolder) viewHolder).img);


                }else if(myDataList.get(position).getType().equals("0")){
                    ((LeftViewHolder) viewHolder).img.setVisibility(View.GONE);
                    ((LeftViewHolder) viewHolder).time_img.setVisibility(View.GONE);
                    ((LeftViewHolder) viewHolder).time_text.setText(myDataList.get(position).getItem_chat_time());
                    ((LeftViewHolder) viewHolder).content.setText(myDataList.get(position).getItem_chat_content());

              }


            }
            else
            {
                if(myDataList.get(position).getType().equals("1")) {
                    ((RightViewHolder) viewHolder).content.setVisibility(View.GONE);
                    ((RightViewHolder) viewHolder).time_text.setVisibility(View.GONE);
                    ((RightViewHolder) viewHolder).time_img.setText(myDataList.get(position).getItem_chat_time());


                    Glide.with(viewHolder.itemView.getContext())
                            .load(myDataList.get(position).getItem_chat_content())
                            .override(200, 200)
                            .thumbnail(0.1f)
                            .centerCrop()
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(((RightViewHolder) viewHolder).img);


                }else if(myDataList.get(position).getType().equals("0")){
                    ((RightViewHolder) viewHolder).img.setVisibility(View.GONE);
                    ((RightViewHolder) viewHolder).time_img.setVisibility(View.GONE);
                    ((RightViewHolder) viewHolder).time_text.setText(myDataList.get(position).getItem_chat_time());
                    ((RightViewHolder) viewHolder).content.setText(myDataList.get(position).getItem_chat_content());
                }
            }
        }

        @Override
        public int getItemCount()
        {
            return myDataList.size();
        }

//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }

        @Override
        public int getItemViewType(int position) {
            return myDataList.get(position).getViewType();
        }

    public void notifyItemInserted(ArrayList<DataChatItem> dataChatItemArrayList) {

    }

        public class CenterViewHolder extends RecyclerView.ViewHolder{
            TextView content;

            CenterViewHolder(View itemView)
            {
                super(itemView);

                content = itemView.findViewById(R.id.content);
            }
        }

        public class LeftViewHolder extends RecyclerView.ViewHolder{
            TextView content;
            TextView name;
            TextView time_text, time_img;
            ImageView img;

            LeftViewHolder(View itemView)
            {
                super(itemView);

                content = itemView.findViewById(R.id.content);
                name = itemView.findViewById(R.id.name);
                time_text = itemView.findViewById(R.id.time_text);
                time_img = itemView.findViewById(R.id.time_img);
                img = itemView.findViewById(R.id.chat_left_img);
            }
        }

        public class RightViewHolder extends RecyclerView.ViewHolder{
            TextView content;
            ImageView img;
            TextView time_text, time_img;

            RightViewHolder(View itemView)
            {
                super(itemView);

                content = itemView.findViewById(R.id.content);
                time_text = itemView.findViewById(R.id.time_text);
                time_img = itemView.findViewById(R.id.time_img);
                img = itemView.findViewById(R.id.chat_right_img);
            }
        }

    }

