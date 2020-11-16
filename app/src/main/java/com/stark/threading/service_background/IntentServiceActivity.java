package com.stark.threading.service_background;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.stark.threading.R;

public class IntentServiceActivity extends AppCompatActivity {
    private EditText editTextInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_service);
        editTextInput = findViewById(R.id.edit_text_input);
    }
    public void startService(View v) {
        String input = editTextInput.getText().toString();
        Intent serviceIntent = new Intent(this, ExampleIntentService.class);
        serviceIntent.putExtra("inputExtra", input);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
}

/*
========================Theory=============================================
An IntentService runs in the background independently from an activity and handles all the incoming work on a HandlerThread,
so we don't have to take care of creating our own background thread in order to not block the UI thread.

Since IntentService is a subclass of Service and therefore affected by the Android Oreo background execution limits,
the recommended approach is to use the JobIntentService instead, which uses JobScheduler to enqueue a job on API 26 and higher.
In this video we will use a different approach and run our IntentService as a foreground service on Android Oreo and higher,
but as a normal background service on API 25 and lower.

For this, we create a class that extends IntentService, where we provide a default constructor and override onCreate, onHandleIntent
and onDestroy. We don't have to implement onBind, since the superclass provides a default implementation that returns null. When
the Service is created, onCreate is triggered. Here, we display a persistent notification (after creating a notification channel
in the Application class) and call startForeground if the SDK_INT is VERSION_CODES.O or higher to promote the service to a foreground
service. In onHandleIntent, all the incoming intents are executed on a worker thread sequentially, one after another. When an intent
is processed, the next one is started automatically. Here we can do long-running work synchronously without blocking the main thread.
We can send data to the service in form of intent extras and when the last intent is finished, the service stops itself and onDestroy
is triggered. This means that we don't have to call stopSelf or stopService.

In order to keep the CPU running even after the screen is turned off, we can acquire a partial wake lock in onCreate with help of
the PowerManager's newWakeLock method. In onDestroy we release this wake lock. Optionally, we can pass a long value for the timeout
in milliseconds, which takes care of releasing the wake lock in case anything went wrong and release is not called. This avoids that
our wake lock drains the user's battery.
With setIntentRedelivery, we can define what should happen if the system kills the service (for example in low memory situations).
A boolean value of false is the equivalent of returning START_NON_STICKY from onStartCommand in a normal service, and true equals
START_REDELIVER_INTENT.
Lastly, we have to request the WAKE_LOCK and FOREGROUND_SERVICE (Android Pie) permissions in the Manifest, register our service and
Application class there and call startService or ContextCompat.startForegroundService in our MainActivity to start the service.




========================End================================================
 */