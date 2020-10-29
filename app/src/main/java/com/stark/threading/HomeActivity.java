package com.stark.threading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void basicThread(View view) {
        startActivity(new Intent(HomeActivity.this, BasicThreadActivity.class));
    }

    public void looperMsgQHanThread(View view) {
        startActivity(new Intent(HomeActivity.this, LooperMsgQHandlerActivity.class));
    }

    public void handlerThread(View view) {
        startActivity(new Intent(HomeActivity.this, HandlerThreadActivity.class));
    }
}