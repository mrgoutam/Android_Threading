package com.stark.threading.service_background;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stark.threading.R;

public class ServicesAndBackgroundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_and_background);
    }

    public void jobService(View view) {
        startActivity(new Intent(this, JobServiceActivity.class));
    }

    public void foregroundService(View view) {
        startActivity(new Intent(this, ForegroundServiceActivity.class));
    }

    public void intentService(View view) {
        startActivity(new Intent(this, IntentServiceActivity.class));
    }
}