package com.example.frame.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frame.R;
import com.example.frame.etc.DataRegister;

import java.util.ArrayList;

//이벤트 당첨자 띄우는 어댑터
public class EventWinnerListAdapter extends RecyclerView.Adapter<EventWinnerListAdapter.MyViewHolder> {

    //1. adapter class with implementation of it's methods (다중선택리사이클러뷰)
    private Context context;
    private ArrayList<DataRegister> entry;

    //5. (다중선택리사이클러뷰)
    public EventWinnerListAdapter(Context context, ArrayList<DataRegister> entry) {
        this.context = context;
        this.entry = entry;
    }

    public void setEntry(ArrayList<DataRegister> entry){
        this.entry = new ArrayList<>();
        this.entry = entry;
       // notifyDataSetChanged();
    }


    //6. (다중선택리사이클러뷰)
    @NonNull
    @Override
    public EventWinnerListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_register_list,
                parent,false); //리사이클러뷰에 들어갈 item 레이아웃 정의


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventWinnerListAdapter.MyViewHolder holder, int position) {

        holder.bind(entry.get(position));

       // String title = eventList.get(position).getEvent_title();

    }

    @Override
    public int getItemCount() {
        return entry.size();
    }
    //6//


    //2- viewHolder class 만들기 (다중선택리사이클러뷰)
    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView user_name, user_email,user_id;
        private ImageView img_checked,img_checked2;
        private RelativeLayout background;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

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
            }

        }
        return selected;
    }

}
