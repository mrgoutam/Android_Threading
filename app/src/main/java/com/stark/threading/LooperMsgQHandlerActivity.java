package com.stark.threading;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class LooperMsgQHandlerActivity extends AppCompatActivity {
    private static final String TAG = "LooperMsgQHandlerActivi";
    private ExampleLooperThread looperThread = new ExampleLooperThread();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper_msg_q_handler);
    }

    public void startThread(View view) {
        looperThread.start();
    }

    public void stopThread(View view) {
    }

    public void taskA(View view) {
    }

    public void taskB(View view) {
    }
}

/*
================================THEORY====================================
                   Thread
                   start()
                     |  []  <- Message Queue
                     |  []
              run()  |  []
                     |  []
                     |  []
                   terminate

 - By default a thread start, execute some work and terminate. After the termination, we can't use the same thread again.
   We have to initiate new one.
 - This doesn't seem to be the case of UI thread and also called main thread.
========================END===============================================
*/