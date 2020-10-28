package com.stark.threading;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
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
        looperThread.handler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <5 ; i++) {
                    Log.d(TAG, "run: "+i);
                    SystemClock.sleep(1000);
                }
            }
        });
    }

    public void taskB(View view) {

    }
}




/*
CASE I:
We are going to create a looper thread called ExampleLooperThread.java.
Now it is not a looper but just a thread
public void run() {
    for (int i = 0; i <5 ; i++) {
        Log.d(TAG, "run: "+i);
        SystemClock.sleep(1000);
    }
    Log.d(TAG, "End of run()");
}
when we start the thread by startThread it works fine every other buttons are working during this running thread.
But after termination of this thread if we try to click the startThread button app crashed. Because we try to
start same thread again.

In other words our thread looks like this
                   Thread
                   start()
                     |
                     |
              run()  |  []
                     |
                     |
                   terminate

We want to turn into this
                   Thread
                   start()
                     |  []  <- Message Queue
                     |  []   /'''\
              run()  |  []  |     |
                     |  []  |     | Looper
                     |  []   \.../
                   terminate


CASE II:
 Now we have a created a Handler in ExampleLooperThread.java

 public Handler handler;
 public void run() {
    handler = new Handler();
    for (int i = 0; i <5 ; i++) {
        Log.d(TAG, "run: "+i);
        SystemClock.sleep(1000);
    }
    Log.d(TAG, "End of run()");
}

Now if we click on startThread, app crashes. Because "Can't create handler inside that has not called Looper.prepare()"
This is because a handler can only work for a thread it has a looper and message queue. And we have not set up such a looper
and message queue for our background thread yet.

CASE III:
It is very simple. Inside run() method, put Looper.prepare() at the first line.
This adds a looper to this background thread and automatically creates a message queue. Those two are tied together.
After handler initialization (handler = new Handler()) write Looper.loop(). Below order is important

    Looper.prepare();
    handler = new Handler();
    Looper.loop();

code for run() inside ExampleLooperThread.java
   public void run() {
      Looper.prepare();
      handler = new Handler();
      Looper.loop(); // this is a infinite for loop
      Log.d(TAG, "End of run()");
   }

code for taskA() method in LooperMsgQHandlerActivity.java
    public void taskA(View view) {
        looperThread.handler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <5 ; i++) {
                    Log.d(TAG, "run: "+i);
                    SystemClock.sleep(1000);
                }
            }
        });
    }

after click startThread, if we click taskA
-it works fine.
-Other UI components are also responsive
if we click taskA twice, it executes taskA method two times.
Means after finish of taskA if we click taskA again it executes taskA.
handler puts the task in the messageQ and dispatched by Looper.


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