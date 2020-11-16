
package com.stark.threading.broadcastReceiver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.stark.threading.R;

public class StaticBroadcastReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_broadcast_receiver);
        setTitle("Static Receivers");
    }
}

/*
========================Theory=============================================
we will learn everything about BroadcastReceivers in Android, with which we can listen to different system and application events.
This way we can execute code when the device boots up, when the internet connectivity of the phone changes, when we receive an SMS
and a lot more. We can also send our own broadcasts and schedule work for a time in the future.

In part 1 we will take a look at static, Manifest registered BroadcastReceivers, that will work even if the app itself is not running.
By giving them an IntentFilter with an appropriate action string, we can have them listen to implicit broadcasts, like different system
events. When these events happen, the onReceive method of our BroadcastReceiver will be called.
We will also learn about the background execution changes that have been made on Android Nougat and Oreo, which for example change the way
we can listen to the CONNECTIVITY_CHANGE broadcast.
========================End================================================
 */