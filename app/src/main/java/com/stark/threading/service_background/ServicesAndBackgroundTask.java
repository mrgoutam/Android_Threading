package com.stark.threading.service_background;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.stark.threading.R;

public class ServicesAndBackgroundTask extends AppCompatActivity {
    private static final String TAG = "ExampleJobService";
    public static final int JOB_ID = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_and_background_task);
        setTitle("Services and Background Task");
    }

    public void scheduleJob(View view) {
        ComponentName componentName = new ComponentName(this, ExampleJobService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                    .setRequiresCharging(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPersisted(true)
                    .setPeriodic(15*60*1000)
                    .build();

            JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = jobScheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS){
                Log.d(TAG, "Job Scheduled");
            }else {
                Log.d(TAG, "Job Scheduling failed");
            }
        }
    }

    public void cancelJob(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.cancel(JOB_ID);
            Log.d(TAG, "Job cancelled");
        }

    }
}

/*
======================Theory=============================================
Since Android Oreo, idle apps can't keep background services running anymore. So if you need to do operations even if your
app is not running in the foreground, you should use a JobScheduler instead.

We will set up such a JobScheduler by creating a class that extends JobService and then scheduling it with the JOB_SCHEDULER_SERVICE.
In the JobService class, we override onStartJob and onStopJob. In onStartJob we will start a background thread to do some long running
(fake) operations. In onStopJob we cancel our work because the system will release the wakelock when the criteria for our job are not
met anymore (for example we required an un-metered network and the user disables WiFi). In this case we will reschedule our task to try
again later.

When we schedule our job, we pass a JobInfo object to the JobScheduler, which will define under which circumstances we want the system
to execute our job. Here we can set criteria like setRequiresDeviceCharging, setRequiredNetworkType, setPeriodic and more. We can even
make it survive device reboots with setPersisted.

Lastly we register our JobService in the manifest with the android.permission.BIND_JOB_SERVICE permission and the system will then start
our service at the appropriate time, even if our app is not running.

                                                                |-------------> onStartJob ------------->
                                                                |                                       |
                                                                |                                       |
    Your App ---------------->  Schedule ----------------> System <-----------  Job Finished  <----- Your Service
                                                                |                                       |
                                                                |                                       |
                                                                |------------->  onStopJob  ----------->


Min SDK required is 21.


======================End================================================
 */
