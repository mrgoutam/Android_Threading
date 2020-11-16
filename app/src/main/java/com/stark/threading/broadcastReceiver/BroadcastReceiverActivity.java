package com.stark.threading.broadcastReceiver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stark.threading.R;

public class BroadcastReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_receiver);
        setTitle("Broadcast Receiver");
    }

    public void staticBCReceiver(View view) {
        startActivity(new Intent(this, StaticBroadcastReceiverActivity.class));
    }
}
