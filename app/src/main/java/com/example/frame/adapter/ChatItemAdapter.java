package com.example.frame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.frame.R;
import com.example.frame.etc.DataChatItem;
import com.example.frame.etc.ViewType;

import java.util.ArrayList;

public class ChatItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<DataChatItem> myDataList = null;

    public ChatItemAdapter(ArrayList<DataChatItem> dataList) {
        myDataList = dataList;
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
            else
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
                ((LeftViewHolder) viewHolder).content.setText(myDataList.get(position).getItem_chat_content());
                ((LeftViewHolder) viewHolder).time.setText(myDataList.get(position).getItem_chat_time());
            }
            else
            {
                ((RightViewHolder) viewHolder).content.setText(myDataList.get(position).getItem_chat_content());
                ((RightViewHolder) viewHolder).time.setText(myDataList.get(position).getItem_chat_time());
            }
        }

        @Override
        public int getItemCount()
        {
            return myDataList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return myDataList.get(position).getViewType();
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
            TextView time;

            LeftViewHolder(View itemView)
            {
                super(itemView);

                content = itemView.findViewById(R.id.content);
                name = itemView.findViewById(R.id.name);
                time = itemView.findViewById(R.id.time);
            }
        }

        public class RightViewHolder extends RecyclerView.ViewHolder{
            TextView content;
            TextView time;

            RightViewHolder(View itemView)
            {
                super(itemView);

                content = itemView.findViewById(R.id.content);
                time = itemView.findViewById(R.id.time);
            }
        }

    }

