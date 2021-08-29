package com.example.frame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frame.fragment.BottomEventSheetFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DetailEventActivity extends AppCompatActivity {

    Button btn_event_enter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        btn_event_enter = findViewById(R.id.btn_event_enter);

        btn_event_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomEventSheetFragment();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
        });
    }
}
