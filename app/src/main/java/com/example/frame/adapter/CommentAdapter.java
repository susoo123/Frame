package com.example.frame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frame.R;
import com.example.frame.etc.DataComment;
import com.example.frame.etc.DataFeedImg;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DataComment> dataComments;

    public CommentAdapter(Context context, ArrayList<DataComment> dataComments) {
        this.context = context;
        this.dataComments = dataComments;
    }


    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);

        return new CommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {

        DataComment dataComment = dataComments.get(position);

        String comment_img = dataComment.getComment_img();
        String comment_name = dataComment.getComment_name();
        String comment_text = dataComment.getComment_text();
        String comment_date = dataComment.getComment_date();


        holder.bindComment(comment_img,comment_name,comment_text,comment_date);
    }

    @Override
    public int getItemCount() {
        return dataComments.size();
    }

    public void notifyItemInserted(ArrayList<DataComment> dataComments) {
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView comment_name;
        private TextView comment_text;
        private TextView comment_date;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.comment_img);
            comment_name = itemView.findViewById(R.id.tv_comment_name);
            comment_text = itemView.findViewById(R.id.tv_comment_text);
            comment_date = itemView.findViewById(R.id.tv_comment_date);
        }
        public void bindComment(String imageURL,String name, String text, String date) {
            Glide.with(context)
                    .load(imageURL)
                    .into(mImageView);

            comment_name.setText(name);
            comment_text.setText(text);
            comment_date.setText(date);

        }
    }
}
