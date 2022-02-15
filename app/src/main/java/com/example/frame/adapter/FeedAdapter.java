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

//import com.android.volley.request.StringRequest;
//import com.android.volley.toolbox.StringRequest;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.frame.AddFeedActivity2;
import com.example.frame.DetailFeedActivity;
import com.example.frame.EditFeedActivity;
import com.example.frame.MainActivity;
import com.example.frame.R;
import com.example.frame.etc.AppHelper;
import com.example.frame.etc.DataFeed;
import com.example.frame.etc.DataFeedImg;
import com.example.frame.etc.DataModel;
import com.example.frame.etc.OnPostListener;
import com.example.frame.etc.SessionManager;
import com.example.frame.fragment.EventFragment;
import com.example.frame.fragment.FeedFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;


//리사이클러뷰 클릭 이벤트 - 1. 리사이클러뷰클릭 리스너 인터페이스 만들기
//리사이클러뷰 클릭 이벤트 - 2. 리사이클러뷰클릭 리스너 변수 만들기
//리사이클러뷰 클릭 이벤트 - 3. 뷰홀더에 implements View.OnClickListener 달기 (그리고 빨간 줄 없애줌...메소드 implements)
//리사이클러뷰 클릭 이벤트 - 4. 뷰홀더에 내에 클릭리스너 set
//리사이클러뷰 클릭 이벤트 - 5. onClick메소드 내에 리스너 set
//리사이클러뷰 클릭 이벤트 - 6.어댑터 생성자에 넣기
//리사이클러뷰 클릭 이벤트 - 7.어댑터 올릴 액티비티나 프래그먼트로 가서 어댑터 선언한 곳에 listener 넣기
//리사이클러뷰 클릭 이벤트 - 8. 프래그먼트(또는 액티비티)에서 사용할 클릭리스너 메소드 만들기

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyViewHolder> {

    private ArrayList<DataFeed> feedList = new ArrayList<>();
    Context context;
    private Activity activity;
    private RecyclerViewClickListener clickListener;//리사이클러뷰 클릭 이벤트 - 2. 리사이클러뷰클릭 리스너 변수 만들기
    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;
    AlertDialog dialog;
    SessionManager sessionManager;
    private OnPostListener onPostListener;
    String feed_uid;
    private int checkedPosition = 0; //-1: no default selection , 0: 1st item selected



    //리사이클러뷰 클릭 이벤트 - . 6.어댑터 생성자에 넣기
    public FeedAdapter(Context context, Activity activity, ArrayList feedList, RecyclerViewClickListener clickListener) {
        this.context= context;
        this.activity = activity;
        this.feedList= feedList;
        this.clickListener = clickListener;
    }


    public void SetDataFeed(ArrayList<DataFeed> feedList){
        this.feedList = new ArrayList<>();
        this.feedList = feedList;
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);

//        view.findViewById(R.id.btn_feed_option_container).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPopup(v,viewHolder.getAdapterPosition());
//            }
//
//
//        });

        viewHolder.btn_feed_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PopupMenu popupMenu = new PopupMenu(context, view);
                //getMenuInflater().inflate(R.menu.menu_feed, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.edit) {

                            String feed_uid2 = feedList.get(viewHolder.getAdapterPosition()).getFeed_uid();
                            Intent intent = new Intent(activity,EditFeedActivity.class);
                            intent.putExtra("feed_id",feed_uid2);

                            view.getContext().startActivity(intent);


                        } else if (menuItem.getItemId() == R.id.del) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            dialog = builder.setMessage("피드를 삭제하시겠습니까?")
                                    .setNegativeButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            sendDelMessage(viewHolder.getAdapterPosition());
//                                            ((MainActivity)view.getContext()).replaceFragment(FeedFragment.newInstance());
////
//                                            Intent intent = new Intent(activity,MainActivity.class);
//                                            view.getContext().startActivity(intent);
                                        }
                                    })
                                    .setPositiveButton("취소",null )
                                    .create();

                            dialog.show();


                        }
                        return false;
                    }
                });


                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.menu_feed, popupMenu.getMenu());
                popupMenu.show();

            }
        });



        return viewHolder;
        //return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String contents =feedList.get(position).getContents();
        String writer = feedList.get(position).getWriter();
//        String feed_img = feedList.get(position).getFeed_img();
        String feed_time = feedList.get(position).getDate();
        String user_img = feedList.get(position).getUserImg();
        ArrayList imgList = feedList.get(position).getDataFeedImgList();


        feed_uid = feedList.get(position).getFeed_uid();
        Log.e("soo","feed_uid 확인1 : " + feed_uid );

        sessionManager = new SessionManager(context);
        HashMap<String,String> user = sessionManager.getUserDetail();
        String user_id = user.get(sessionManager.ID);

        if(user.get(sessionManager.ID).equals(feedList.get(position).getFeed_user_id())){ //쉐어드에 저장된 userId와 볼리로 받은 userId가 같으면
            holder.btn_feed_option.setVisibility(View.VISIBLE);
        }else {
            holder.btn_feed_option.setVisibility(View.GONE);
        }

        //리사이클러뷰 어댑터 set
        ImageSliderInFeedAdapter imageSliderInFeedAdapter = new ImageSliderInFeedAdapter(context,imgList);


        //holder.imageRecyclerview.setHasFixedSize(true);
        // holder.imageRecyclerview.addItemDecoration(new LinePagerIndicatorDecoration()); //indicator 달기
        holder.imageRecyclerview.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        holder.imageRecyclerview.setAdapter(imageSliderInFeedAdapter);


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

        //holder.bind(feedList.get(position));


    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    //리사이클러뷰 클릭 이벤트 - 3. 뷰홀더에 implements View.OnClickListener 달기
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_feed_image,item_feed_profile_image;
        TextView item_feed_contents,item_feed_username,item_feed_time ;
        private ImageButton btn_feed_option;
        RecyclerView imageRecyclerview;
        //private LinearLayout layoutIndicator;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //item_feed_image = itemView.findViewById(R.id.item_feed_image);

            //리사이클러뷰 연결
            imageRecyclerview =itemView.findViewById(R.id.item_feed_image_rv);



            item_feed_contents = itemView.findViewById(R.id.item_feed_contents);
            item_feed_username = itemView.findViewById(R.id.item_feed_username);
            item_feed_time = itemView.findViewById(R.id.feed_time);
            item_feed_profile_image = itemView.findViewById(R.id.item_feed_profile_image);

            btn_feed_option = itemView.findViewById(R.id.btn_feed_option_container);


            //리사이클러뷰 클릭 이벤트 - 4. 뷰홀더에 내에 클릭리스너 set
            itemView.setOnClickListener(this);

        }

        public void setData(String contents, String username, String time) {

            item_feed_contents.setText(contents);
            item_feed_username.setText(username);
            item_feed_time.setText(time);
        }

        //리사이클러뷰 클릭 이벤트 - 3-1. 뷰홀더에 implements View.OnClickListener 달고 매서드 implements하면 얘가 나옴.
        @Override
        public void onClick(View view) {

            //리사이클러뷰 클릭 이벤트 - 5. onClick메소드 내에 리스너 set
            clickListener.onClick(view, getAdapterPosition());


        }

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


        }
//    }

    public DataFeed getSelected(){
        if(checkedPosition != -1){
            return feedList.get(checkedPosition);
        }
        return null;
    }

    //리사이클러뷰 클릭 이벤트 - 1. 리사이클러뷰클릭 리스너 인터페이스 만들기
    public interface RecyclerViewClickListener{
        void onClick(View v, int position);

    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }



    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.edit:
                        onPostListener.onModify(position);
                        return true;
                    case R.id.del:
                        onPostListener.onDelete(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        dialog = builder.setMessage("피드를 삭제하시겠습니까?")
                                .setNegativeButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sendDelMessage(which);
                                    }
                                })
                                .setPositiveButton("취소",null )
                                .create();

                        dialog.show();
                        Toast.makeText(context, "피드를 삭제했습니다.", Toast.LENGTH_SHORT).show();

                        return true;

                    default:
                        return false;
                }
            }
        });

        MenuInflater menuInflater = popup.getMenuInflater();
        menuInflater.inflate(R.menu.menu_feed, popup.getMenu());
        popup.show();

    }

    //피드 삭제 메서드
    private void sendDelMessage(int position){
        String url = "http://ec2-52-79-204-252.ap-northeast-2.compute.amazonaws.com/del_feed.php";
        String feed_uid2 = feedList.get(position).getFeed_uid();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                ((MainActivity)context).replaceFragment(FeedFragment.newInstance());
                                Toast.makeText(context, "피드를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "피드 삭제 실패", Toast.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("soo1", "응답 -> " + response);
                    }


                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }




                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("feed_uid",feed_uid2);
                Log.e("soo","feed_uid 확인2 : " + feed_uid2 );

                return params;

            }
        };


       RequestQueue requestQueue = Volley.newRequestQueue(context); // requestQueue 초기화 필수
       requestQueue.add(request);

    }








}