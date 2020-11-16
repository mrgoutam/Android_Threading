package com.stark.threading.broadcastReceiver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.stark.threading.R;

public class DynamicBroadcastReceiverActivity extends AppCompatActivity {
    ExampleDynamicBroadcastReceiver exampleDynamicBroadcastReceiver = new ExampleDynamicBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_broadcast_receiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(exampleDynamicBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(exampleDynamicBroadcastReceiver);
    }
}

/*
========================Theory=============================================
In part 2 of the BroadcastReceiver tutorial, we will learn how to register a BroadcastReceiver dynamically.
Instead of putting the receiver into the Manifest file, we register it in the lifecycle of our application.
This way the BroadcastReceiver lives as long as the context where we register it in. This could be the application
context itself, which would keep our BroadcastReceiver running as long as our app is running, but also for example
the activity context or only as long as our app is in the foreground.
For this we have to call the registerReceiver method in any of the lifecycle callback methods (onCreate, onStart,
onResume) and unregisterReceiver in their appropriate counterpart (onDestroy, onStop, onPause).
When we register our BroadcastReceiver, we also have to pass an IntentFilter, which specifies the action we are
listening for.
Within the onReceive method of the BroadcastReceiver, we can also obtain data that is sent as an extra over
the broadcast intent.

========================End================================================
 */