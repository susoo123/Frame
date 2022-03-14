package com.example.frame.adapter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frame.R;
import com.example.frame.etc.DataRegister;

import java.util.ArrayList;

public class EventRegisterAdapter extends RecyclerView.Adapter<EventRegisterAdapter.MyViewHolder> {

    //1. adapter class with implementation of it's methods (다중선택리사이클러뷰)
    private Context context;
    private ArrayList<DataRegister> entry;
    private Cursor cursor;
    String winner_id;

    //5. (다중선택리사이클러뷰)
    public EventRegisterAdapter(Context context, ArrayList<DataRegister> entry) {
        this.context = context;
        this.entry = entry;
    }

    public void setEntry(ArrayList<DataRegister> entry){
        this.entry = new ArrayList<>();
        this.entry = entry;
       // notifyDataSetChanged();
    }

//    public void setCursor(Cursor cursor) {
//        this.cursor = cursor;
//    }

    //6. (다중선택리사이클러뷰)
    @NonNull
    @Override
    public EventRegisterAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_register_list,
                parent,false); //리사이클러뷰에 들어갈 item 레이아웃 정의


        return new MyViewHolder(view);

//        Context context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//
//        View v = inflater.inflate(R.layout.item_event_register_list, parent, false);
//        MyViewHolder viewHolder = new MyViewHolder(v, context);
//        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventRegisterAdapter.MyViewHolder holder, int position) {

        holder.bind(entry.get(position));
//        cursor.moveToPosition(position);

       // String title = eventList.get(position).getEvent_title();

    }

    @Override
    public int getItemCount() {
        return entry.size();
    }
    //6//
//    @Override
//    public int getItemCount() {
//        if (cursor == null) return 0;
//        return cursor.getCount();
//    }


    //2- viewHolder class 만들기 (다중선택리사이클러뷰)
    public class MyViewHolder extends RecyclerView.ViewHolder{

       // private final Context context;
        private TextView user_name, user_email,user_id;
        private ImageView img_checked,img_checked2;
        private RelativeLayout background;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            //this.context = context;
            
            user_id = itemView.findViewById(R.id.tv_user_id_register);
            user_name = itemView.findViewById(R.id.tv_user_name_register);
            user_email = itemView.findViewById(R.id.tv_user_email_register);
            img_checked2 = itemView.findViewById(R.id.img_checked2);
            background = itemView.findViewById(R.id.background_cv);
          
        }



        //getting the selected items
        void bind(final DataRegister data){
            img_checked2.setVisibility(data.isChecked() ? View.VISIBLE : View.GONE);
            user_id.setText(data.getUser_id());
            user_name.setText(data.getUser_name());
            user_email.setText(data.getUser_email());
            background.setBackgroundColor(Color.TRANSPARENT);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.setChecked(!data.isChecked());

                    //리사이클러뷰 아이템 클릭했을 때 체크 표시 나오게
                    img_checked2.setVisibility(data.isChecked() ? View.VISIBLE : View.GONE);

                    //리사이클러뷰 아이템 클릭했을 때 배경 색 변하도록!
                    background.setBackgroundColor(data.isChecked() ? Color.parseColor("#F7E598") : Color.TRANSPARENT);
                //    Log.e("ERadpater", "entry :  " + String.valueOf(data));
//                   if(data.isChecked()){
//                       ArrayList arrayList = new ArrayList();
//                       arrayList.add(img_checked2);
//
//                   }


//                    addClick(context);
//                    int position = getAdapterPosition();
//                    cursor.moveToPosition(position);
//                    Log.e("ERadpater", "addClick position:  " + position);
//                    int count = getClicksCount(context);
//                    Log.e("ERadpater", "addClick count:  " + count);

                    if(getSelected().size() >=0) {
                        //getting a list of item selected

                        for (int i = 0; i < getSelected().size(); i++) {

                            ArrayList arrayList = new ArrayList();
                            winner_id = getSelected().get(i).getUser_id();
                            arrayList.add(winner_id);
                            Log.e("ERAdapter", "arrayList 2: " + arrayList.size());

                        }
                    }
                }
            });
        }


    }

    //3. getting all items selected (다중선택리사이클러뷰)
    public ArrayList<DataRegister> getAll(){
        return entry;
    }


    // 4. getting selected when btn clicked (다중선택리사이클러뷰)
    public ArrayList<DataRegister> getSelected() {
        ArrayList<DataRegister> selected = new ArrayList<>();
        for (int i = 0; i < entry.size(); i++) {
            if (entry.get(i).isChecked()) {
                selected.add(entry.get(i));
                //Log.e("ERadpater", "entry :  " + String.valueOf(entry.size()));
            }

        }
        return selected;
    }

//    private void addClick(Context mContext) {
//        int click = getClicksCount(mContext)+1;
//        SharedPreferences preferences = mContext.getSharedPreferences("click_count", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt("count", click);
//        editor.apply();
//    }
//
//    private int getClicksCount(Context mContext){
//        SharedPreferences preferences = mContext.getSharedPreferences("click_count",Context.MODE_PRIVATE);
//        return preferences.getInt("count",0);
////    }
}
