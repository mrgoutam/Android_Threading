package com.stark.threading;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import static com.stark.threading.ExampleHandlerThread.EXAMPLE_TASK;

public class HandlerThreadActivity extends AppCompatActivity {
    private static final String TAG = "HandlerThreadActivity";

    private ExampleHandlerThread handlerThread = new ExampleHandlerThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_thread);

        handlerThread.start();
    }

    public void doWork(View view) {
        Message message = Message.obtain();
        message.what = EXAMPLE_TASK;
        message.arg1 = 23;
        message.obj = "Object String";
        //message.setData();

        handlerThread.getHandler().sendMessage(message);
        //handlerThread.getHandler().sendEmptyMessage(1);

        //handlerThread.getHandler().post(new ExampleRunnable1());
        //handlerThread.getHandler().post(new ExampleRunnable1());
        //handlerThread.getHandler().postAtFrontOfQueue(new ExampleRunnable2());
    }

    public void removeMessages(View view) {
        handlerThread.getHandler().removeMessages(EXAMPLE_TASK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //to avoid memory leak
        handlerThread.quit();
    }

    static class ExampleRunnable1 implements Runnable{
        @Override
        public void run() {
            for (int i = 0; i <4 ; i++) {
                Log.d(TAG, "Runnable1: "+i);
                SystemClock.sleep(1000);
            }
        }
    }

    static class ExampleRunnable2 implements Runnable{
        @Override
        public void run() {
            for (int i = 0; i <4 ; i++) {
                Log.d(TAG, "Runnable2: "+i);
                SystemClock.sleep(1000);
            }
        }
    }
}

/*
========================Theory=============================================
we will learn how to use the HandlerThread class on Android, which is a subclass of the normal Java Thread that initiates a
MessageQueue and a Looper in it’s run method. This way, it keeps running until we call quit on it and accepts new work packages
in form of Messages and Runnables.
To get work into a HandlerThread, we need a Handler, which we can initialize either in the onLooperPrepared callback in a
HandlerThread subclass, or by passing the HandlerThread’s Looper explicitly with getLooper.
The Handler has different methods available to send and create Messages and Runnables, like post, postDelayed, postAtTime,
postAtFrontOfQueue, send, sendDelayed, sendAtTime, sendAtFrontOfQueue, sendEmptyMessage, sendEmptyMessageDelayed,
sendEmptyMessageAtTime and different obtain variations that take different combinations of arguments. With setTarget,
we can specify the Handler that a Message should be dispatched to, and with sendToTarget send it to the corresponding
MessageQueue.
Messages can hold data in their what, arg1, arg2, and obj fields, and we can attach an additional data Bundle with setData.
To read this data, we have to override the Handler’s handleMessage method.
When we instantiate a HandlerThread, we should specify a thread priority with the Process.THREAD_PRIORITY constants,
either over the constructor or by calling Process.setThreadPriority.
With quit and quitSafely we can leave the MessageQueue of a HandlerThread. But we can also remove Messages and Runnables
from its MessageQueue without quitting the Looper, by calling removeMessages, removeCallbacks or removeCallbacksAndMessages,
which can also take an object token argument to identify the Message more specifically.
========================END================================================
 */