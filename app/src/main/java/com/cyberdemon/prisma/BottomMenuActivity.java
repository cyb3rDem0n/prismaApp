package com.cyberdemon.prisma;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class BottomMenuActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_info:
                    mTextMessage.setText(R.string.title_info);
                    return true;
                case R.id.navigation_project:
                    mTextMessage.setText(R.string.title_project);
                    return true;
                case R.id.navigation_news:
                    mTextMessage.setText(R.string.title_news);
                    return true;
                case R.id.navigation_maps:
                    mTextMessage.setText(R.string.title_activity_maps);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_menu);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
