package com.example.frame.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.frame.EditFeedActivity;
import com.example.frame.MainActivity;
import com.example.frame.R;
import com.example.frame.etc.DataFeed;
import com.example.frame.etc.DataTicket;
import com.example.frame.etc.OnPostListener;
import com.example.frame.etc.SessionManager;
import com.example.frame.fragment.FeedFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//리사이클러뷰 클릭 이벤트 - 1. 리사이클러뷰클릭 리스너 인터페이스 만들기
//리사이클러뷰 클릭 이벤트 - 2. 리사이클러뷰클릭 리스너 변수 만들기
//리사이클러뷰 클릭 이벤트 - 3. 뷰홀더에 implements View.OnClickListener 달기 (그리고 빨간 줄 없애줌...메소드 implements)
//리사이클러뷰 클릭 이벤트 - 4. 뷰홀더에 내에 클릭리스너 set
//리사이클러뷰 클릭 이벤트 - 5. onClick메소드 내에 리스너 set
//리사이클러뷰 클릭 이벤트 - 6.어댑터 생성자에 넣기
//리사이클러뷰 클릭 이벤트 - 7.어댑터 올릴 액티비티나 프래그먼트로 가서 어댑터 선언한 곳에 listener 넣기
//리사이클러뷰 클릭 이벤트 - 8. 프래그먼트(또는 액티비티)에서 사용할 클릭리스너 메소드 만들기

public class TicketBoxAdapter extends RecyclerView.Adapter<TicketBoxAdapter.MyViewHolder> {

    private ArrayList<DataTicket> ticketArrayList = new ArrayList<>();
    Context context;
    private Activity activity;

    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;

    SessionManager sessionManager;
    String feed_uid;
   // private int checkedPosition = 0; //-1: no default selection , 0: 1st item selected



    //리사이클러뷰 클릭 이벤트 - . 6.어댑터 생성자에 넣기
    public TicketBoxAdapter(Context context, Activity activity, ArrayList ticketArrayList) {
        this.context= context;
        this.activity = activity;
        this.ticketArrayList= ticketArrayList;
        //this.clickListener = clickListener;
    }





    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);


        return viewHolder;

    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String ticket_title =ticketArrayList.get(position).getEvent_title_ticket();
        String ticket_start_date = ticketArrayList.get(position).getEvent_start_date_ticket();
        String ticket_end_date = ticketArrayList.get(position).getEvent_end_date_ticket();
        ArrayList imgList = ticketArrayList.get(position).getTicket_img_ticket();


        //feed_uid = feedList.get(position).getFeed_uid();
        Log.e("soo","imgList 확인1 : " + imgList );

        sessionManager = new SessionManager(context);
        HashMap<String,String> user = sessionManager.getUserDetail();
        String user_id = user.get(sessionManager.ID);

//        if(user.get(sessionManager.ID).equals(ticketArrayList.get(position).getFeed_user_id())){ //쉐어드에 저장된 userId와 볼리로 받은 userId가 같으면
//            holder.btn_feed_option.setVisibility(View.VISIBLE);
//        }else {
//            holder.btn_feed_option.setVisibility(View.GONE);
//        }

        //리사이클러뷰 어댑터 set
        ImageSliderInFeedAdapter imageSliderInFeedAdapter = new ImageSliderInFeedAdapter(context,imgList);

        holder.imageRecyclerview.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        holder.imageRecyclerview.setAdapter(imageSliderInFeedAdapter);

        //9번
        holder.setData(ticket_title,ticket_start_date,ticket_end_date);

        //holder.bind(feedList.get(position));


    }

    @Override
    public int getItemCount() {
        return ticketArrayList.size();
    }

    //리사이클러뷰 클릭 이벤트 - 3. 뷰홀더에 implements View.OnClickListener 달기
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ticket_title,ticket_start_date,ticket_end_date ;
        RecyclerView imageRecyclerview;
        //private LinearLayout layoutIndicator;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            //리사이클러뷰 연결
            imageRecyclerview =itemView.findViewById(R.id.ticket_img_rv);



            ticket_title = itemView.findViewById(R.id.ticket_title);
            ticket_start_date = itemView.findViewById(R.id.ticket_start_date);
            ticket_end_date = itemView.findViewById(R.id.ticket_end_date);


            //리사이클러뷰 클릭 이벤트 - 4. 뷰홀더에 내에 클릭리스너 set
            //itemView.setOnClickListener(this);

        }

        public void setData(String ticket_title1, String ticket_start_date1, String ticket_end_date1) {

            ticket_title.setText(ticket_title1);
            ticket_start_date.setText(ticket_start_date1);
            ticket_end_date.setText(ticket_end_date1);
        }

//        //리사이클러뷰 클릭 이벤트 - 3-1. 뷰홀더에 implements View.OnClickListener 달고 매서드 implements하면 얘가 나옴.
//        @Override
//        public void onClick(View view) {
//
//            //리사이클러뷰 클릭 이벤트 - 5. onClick메소드 내에 리스너 set
//            clickListener.onClick(view, getAdapterPosition());
//
//
//        }

        //리사이클러뷰 아이템 클릭
//        void bind(final DataFeed dataFeed){
//            if(checkedPosition == -1){
//
//            }else {
//                if(checkedPosition == getAdapterPosition()){ //아이템이 선택되면
//                    //피드 uid를 서버로 보낸다.
//                    Log.e("확인","아이템 포지션2: "+checkedPosition);
//
//                }else {
//
//                }
//            }

//            //리사이클러뷰 아이템을 클릭하면
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //이미지뷰가 보임
//                    if(checkedPosition != getAdapterPosition()){
//                        notifyItemChanged(checkedPosition);
//                        checkedPosition = getAdapterPosition();
//                        Log.e("확인","아이템 포지션: "+checkedPosition);
//                    }
//                }
//            });


//        }
//    }

//    public DataFeed getSelected(){
//        if(checkedPosition != -1){
//            return feedList.get(checkedPosition);
//        }
//        return null;
//    }
//
//    //리사이클러뷰 클릭 이벤트 - 1. 리사이클러뷰클릭 리스너 인터페이스 만들기
//    public interface RecyclerViewClickListener{
//        void onClick(View v, int position);
//
//    }
//
//    public void setOnPostListener(OnPostListener onPostListener){
//        this.onPostListener = onPostListener;
//    }





    }








}