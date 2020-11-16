package com.stark.threading.service_background;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.stark.threading.R;

public class JobIntentServiceActivity extends AppCompatActivity {
    private EditText editTextInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_intent_service);
        editTextInput = findViewById(R.id.edit_text_input);
    }
    public void enqueueWork(View v) {
        String input = editTextInput.getText().toString();
        Intent serviceIntent = new Intent(this, ExampleJobIntentService.class);
        serviceIntent.putExtra("inputExtra", input);
        ExampleJobIntentService.enqueueWork(this, serviceIntent);
    }
}

/*
The JobIntentService combines 2 different types of services: the IntentService and the JobService.

Since Android Oreo (API 26), background services can't keep running while the app itself is in the background. Instead,
the system will kill them after around 1 minute or throw an IllegalStateException if we try to call startService from
the background. Since IntentService is a subclass of Service, it is affected by these background execution limits.
The recommended alternative for the IntentService is the JobIntentService, which starts a normal IntentService on API
25 and lower, but on Android Oreo and higher it uses the JobScheduler to schedule a JobService with setOverrideDeadline(0) instead.

The same as in the IntentService's onHandleIntent method, all the incoming intents are executed sequentially on a background
thread in the JobIntentService's onHandleWork method, and we can send data to it in form of intent extras. When the service is
first created, it runs through onCreate, and when it finishes executing all the work, it automatically stops itself and onDestroy
is called. We don't have to acquire a wake lock manually in the JobIntentService, since the superclass takes care of this. For this
reason, we have to add the WAKE_LOCK permission into the manifest file.

JobServices are more likely to get deferred or interrupted under memory pressure, in doze mode or when they reach a time limit,
but the system can restart them at a later time. In onStopCurrentWork, we can decide if we want to cancel a job and drop the
current and all following intents if the system interrupts it, or if we want to reschedule the job with the last intent. In
both cases, we should stop the currently running work by checking if isStopped returns true, because otherwise, the system
will ultimately kill the service.
We start the JobIntentService with the static enqueueWork method, where we have to pass a context, the JobIntentService
class we want to start, a unique jobId and the service intent. The system will then, depending on the API level, either
start an IntentService immediately, or use the JobScheduler to enqueue a job, which requires the BIND_JOB_SERVICE permission,
that we have to add to the service tag in the AndroidManifest.xml file.
 */