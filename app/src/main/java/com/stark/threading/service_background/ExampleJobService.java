package com.stark.threading.service_background;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExampleJobService extends JobService {
    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;


    /*
    - this job service by default runs under UI thread
    - So we are responsible to starting our background thread and doing our work
    - when we do our background thread we will make the return type as true
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    if (jobCancelled){
                        return;
                    }
                    Log.d(TAG, "run: " + i);
                    SystemClock.sleep(1000);
                }
                Log.d(TAG, "Job Finished");
                jobFinished(params, false);
            }
        }).start();
    }


    /*
    - here boolean indicates if we want to reschedule our job or not. If our work is not important
      then we can return false. But if we want to retry it later then we have to return true.
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
