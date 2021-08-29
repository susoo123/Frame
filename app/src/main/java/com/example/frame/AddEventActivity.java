package com.example.frame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;
import java.util.TimeZone;

public class AddEventActivity extends AppCompatActivity {
    Button btn_pick_date;
    private TextView tv_event_date_start, tv_event_date_end;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        //btn_pick_date=findViewById(R.id.btn_pick_date);
        tv_event_date_start = findViewById(R.id.event_date_start);
        tv_event_date_end = findViewById(R.id.event_date_end);

//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        calendar.clear();
//
//        long today = MaterialDatePicker.todayInUtcMilliseconds();
//        calendar.setTimeInMillis(today);
//
//        calendar.set(Calendar.MONTH, Calendar.JANUARY);
//        long january = calendar.getTimeInMillis();
//
//        calendar.set(Calendar.MONTH, Calendar.MARCH);
//        long march = calendar.getTimeInMillis();
//
//        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
//        long december = calendar.getTimeInMillis();





        //builder.setSelection(today);





        tv_event_date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickStartDate();
            }
        });

        tv_event_date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickEndDate();
            }
        });

    }



    private void pickStartDate() {

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("이벤트 날짜 선택");

        final MaterialDatePicker materialDatePicker = builder.build();

        materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
        //날짜 하나 선택
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                tv_event_date_start.setText(materialDatePicker.getHeaderText());

            }
        });

    }
    private void pickEndDate() {

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("이벤트 날짜 선택");

        final MaterialDatePicker materialDatePicker = builder.build();

        materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
        //날짜 하나 선택
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                tv_event_date_end.setText(materialDatePicker.getHeaderText());

            }
        });

    }



}
