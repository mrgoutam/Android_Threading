package com.stark.threading;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

public class ExampleLooperThread extends Thread {
    private static final String TAG = "ExampleLooperThread";

    public Handler handler;
    @Override
    public void run() {
        Looper.prepare();

        handler = new Handler();

        Looper.loop(); // this is a infinite for loop

        Log.d(TAG, "End of run()");
    }
}
