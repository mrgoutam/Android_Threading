package com.stark.threading;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

public class ExampleHandlerThread extends HandlerThread {
    public static final int EXAMPLE_TASK = 1;
    private static final String TAG = "ExampleHandlerThread";
    private Handler handler;

    public ExampleHandlerThread() {
        super("ExampleHandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case EXAMPLE_TASK:
                        Log.d(TAG, "handleMessage: arg1: "+msg.arg1+" Obj: "+msg.obj);
                        for (int i = 0; i <4 ; i++) {
                            Log.d(TAG, "handleMessage: "+i);
                            SystemClock.sleep(1000);
                        }
                        break;
                }
            }
        };
    }

    public Handler getHandler() {
        return handler;
    }
}
