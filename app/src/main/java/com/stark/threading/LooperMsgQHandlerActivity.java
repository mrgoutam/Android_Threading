package com.stark.threading;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            looperThread.looper.quitSafely();
        }
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
================================THEORY====================================
                   Thread
                   start()
                     |
                     |
              run()  |  [] <- job
                     |
                     |
                   terminate

 - By default a thread start, execute some work and terminate. After the termination, we can't use the same thread again.
   We have to initiate new one.
 - This doesn't seem to be the case of UI thread and also called main thread. Because after our app started main doesn't
   just terminated even it doesn't have any work to do. It seems to wait for new input for example we click a button and
   do something or perform any other work on it.
 - The mechanism that keeps the thread alive is called Message Queue.

                    Thread
                   start()
                     |  []  <- Message Queue
                     |  []   /'''\
              run()  |  []  |     |
                     |  []  |     | Looper
                     |  []   \.../
                   terminate

- Instead of just one peace of work, it has lot of work to execute. one after another. And there is something called Looper
  which loops through this Message Queue and dispatches messages sequentially.
- Looper is basically just a infinite for loop. Unless we create a purpose we don't leave this loop. This way we never reach
  to the bottom(means termination).
- Handler which is responsible for getting the package of work into Message Queue. In the background thread example
  we post message into the message queue of UI Thread.
  Handler cannot only put message at the start and end instead we can also specify time or dely and this way change the
  order of messages.
- Looper loops through message queue. It tooks the message which has time now or already in past and it dispatches the piece of work
  to the handler. Handler has two responsibility. Handler not only put message in queue but also execute it and the looper
  go for a next round and handover the piece of work to handler.

- If there is no message in the queue that is to be executed right now, then this thread blocks and wait until the message
  hits the time barrier.
- When we quit this looper, we leave infinite for loop and then we execute whatever come below it and then thread terminates
  just like a normal java thread.
- Thread is java specific but Message Queue and Handler are android specific. They belong to the android framework.
  However the concept of loop is not specific to the android. Because we always need something to keep the thread active.
- We can turn a java thread into such a looper thread with a Message Queue. This is the whole concept behind the HandlerThread
  class


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

CASE IV:
Quiting the looper

    public Looper looper; //inside ExampleLooperThread.java
    public void run() {
            Looper.prepare();
            looper = Looper.myLooper();
            handler = new Handler();
            Looper.loop(); // this is a infinite for loop

            Log.d(TAG, "End of run()");
    }

    public void stopThread(View view) {
        looperThread.looper.quitSafely();
    }

on click on stopThread
- looper quits means come out from infinite loop and then "Log.d(TAG, "End of run()"); " executes
If we try to post another task through handler by click taskA we get error message but app will not crash
Error message is "IllegalStateException: Handler (android.os.Handler) {6851386} sending message to a Handler on a dead thread"
However if we try to start thread again by click startThread(), app will crash

CASE V:
14 minutes
========================END===============================================
*/