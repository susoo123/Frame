package com.example.frame.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.frame.R;
import com.example.frame.etc.DataFeedImg;

import java.util.ArrayList;

public class AddFeedImgAdapter extends RecyclerView.Adapter<AddFeedImgAdapter.MyViewHolder> {
   private Context context;
   private ArrayList<Uri> arrayListFeedImg = new ArrayList<>();



   public AddFeedImgAdapter(Context context, ArrayList arrayList){
       this.context = context;
       this.arrayListFeedImg = arrayList;

   }




    @NonNull
    @Override
    public AddFeedImgAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       //리사이클러뷰에 들어갈 아이템xml파일 inflate
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_feed_img, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFeedImgAdapter.MyViewHolder holder, int position) {
        //리사이클러뷰 안에 들어갈 데이터 바인딩
       // Uri feed_img = arrayListFeedImg.get(position);
        String feed_img = String.valueOf(arrayListFeedImg.get(position));
        holder.setData(feed_img);

    }

    @Override
    public int getItemCount() {

       return arrayListFeedImg.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        ImageView item_feed_img;
        CardView cardView;

       //아이템 xml에 만들어둔 컴포넌트 연결
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_feed_img = itemView.findViewById(R.id.item_feed_img);
            cardView = itemView.findViewById(R.id.cardView3);
            cardView.setOnCreateContextMenuListener(this);
        }

        public void setData(String feed_img) {

            Glide.with(itemView.getContext())
                    .load(feed_img)
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
                    }).into(item_feed_img);

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(getAdapterPosition(),102,1,"삭제");

        }
    }

    public void RemoveItem(int position){

        arrayListFeedImg.remove(position);
        notifyDataSetChanged();
    }
}

//  menu.setHeaderTitle("Select Any One");
//            menu.add(getAdapterPosition(),101,0,"Add to wishlist");