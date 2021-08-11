package com.example.frame;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.zip.Inflater;

public class DetailFeedActivity extends AppCompatActivity {
    private ImageButton btn_feed_option;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_feed);



        btn_feed_option = findViewById(R.id.btn_feed_option);


        btn_feed_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.menu_feed, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.edit) {
                            Toast.makeText(DetailFeedActivity.this, "메뉴 1 클릭", Toast.LENGTH_SHORT).show();
                        } else if (menuItem.getItemId() == R.id.del) {
                            Toast.makeText(DetailFeedActivity.this, "메뉴 2 클릭", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });


                popupMenu.show();
            }
        });



    }
}