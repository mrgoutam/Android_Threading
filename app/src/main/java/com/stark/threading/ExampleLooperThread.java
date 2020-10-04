package com.stark.threading;

import android.os.SystemClock;
import android.util.Log;

public class ExampleLooperThread extends Thread {
    private static final String TAG = "ExampleLooperThread";
//https://www.youtube.com/watch?v=TN-CGfzvBhc&list=PLrnPJCHvNZuD52mtV8NvazNYIyIVPVZRa&index=2
    // 7:36
    @Override
    public void run() {
        for (int i = 0; i <5 ; i++) {
            Log.d(TAG, "run: "+i);

            /*
            - Unlike Thread.sleep, we don't need try-catch because it is internally built
             */
            SystemClock.sleep(1000);
        }
        Log.d(TAG, "End of run");
    }
}
