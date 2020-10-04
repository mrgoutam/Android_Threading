package com.stark.threading;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button buttonStartThread;

    /*
    - At the beginning of the MainActivity(), this get executed on the mainThread. So, this handler only work with the message
      queue of MainThread. Which means that we can use it to get our work to the mainThread
    */
    private Handler mainHandler = new Handler();

    /*
    - volatile: Which basically make sure that all our thread always access the most up to date version of this variable
     and not a cached version
    */
    private volatile boolean stopThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStartThread = findViewById(R.id.button_start_thread);
    }

    public void startThread(View view) {
        /*
        - Step: 12
         */
        stopThread = false;

        // Step: 1
        // freezing the ui thread for 10 seconds
        // we can't do any other task(clicking radio button) when ui thread is busy.
        // button stays in pressed stays
        // solution is that we have to put long running operation in background thread
        /*for (int i = 0; i <10 ; i++) {
            Log.d(TAG, "startThread: "+i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        /*
        - Step: 2
        - There is two way of creating thread
        - 1. extends Thread class and override the run() method
        - 2. implements Runnable interface and then passing this Runnable object to a new Thread
        - Thread and Runnable are core java classes. They are not limited to Android.
        - But there are some android specific classes like Handler and Looper which make running this thread and communication
          between different threads more convenient and this classes on the other hand build a basis for higher abstraction classes
          like AsyncTask, HandlerThread or ThreadPoolExecutor
         */

        /*
        - Step: 3
        - creating Thread by extending thread class
        - It's working fine
         */
        /*ExampleThread thread = new ExampleThread(10);
        thread.start();*/

        /*
        - Step: 4
        - creating Thread by implements Runnable
        - It's working fine
         */
        ExampleRunnable exampleRunnable = new ExampleRunnable(10);
        new Thread(exampleRunnable).start();

    }

    public void stopThread(View view) {
        stopThread = true;
    }

    class ExampleThread extends Thread {
        int seconds;

        ExampleThread(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < seconds; i++) {
                Log.d(TAG, "startThread: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ExampleRunnable implements Runnable {
        int seconds;

        ExampleRunnable(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < seconds; i++) {

                /*
                 - Step 11
                 - If you want to stop a thread
                 */
                if (stopThread)
                    return;
                if (i == 5) {

                    /*
                    - Step: 6
                    - updating the button text when i = 5
                    - app crashes
                    - It's says "CalledFromWrongThreadException: Only the original thread that created a view
                      hierarchy can touch its views.
                    - Since this UI widget are not thread safe, we cannot access them from another thread than the UIThread.
                      This is why we get this crash. This concept is same for TextView etc. We can only access them from UI Thread.
                    - To execute this setText() method we need MainActivity and create a handler variable hare.
                     */
                    //buttonStartThread.setText("50%");

                    /*
                    - Step: 7
                    - This will work because the handler post it to UI Thread
                    - Runnable itself doesn't start a new thread, basically it's just a package of work to be done.
                    */
                    /*mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            buttonStartThread.setText("50%");
                        }
                    });*/

                    /*
                    - Step: 8
                    - Instead of creating handler(mainHandler) on UI Thread, we can also create it here and just pass the looper of
                      UI Thread.
                    */

                    /*
                    - Step: 8.1
                    - This handler to post this runnable this won't work.
                    - Can't create handler inside thread Thread[Thread-3,5,main] that has not called Looper.prepare()
                    - Because a handler needs a looper and a message queue to work with to feed with packages of work. We
                      didn't create such looper.
                    - The handler we created here is tied to this background thread. Instead we want to associate
                      this handler with the mainThread.
                     */
                    /*Handler threadHandler = new Handler();
                    threadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            buttonStartThread.setText("50%");
                        }
                    });*/

                    /*
                    - Step 8.2
                    - Attaching main thread looper to the handler
                    - This will work fine
                    - But this is not convenient to create handler like this
                     */
                    /*Handler threadHandler = new Handler(Looper.getMainLooper());
                    threadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            buttonStartThread.setText("50%");
                        }
                    });*/

                    /*
                    - Step: 9
                    - This will also work fine
                     */
                    /*buttonStartThread.post(new Runnable() {
                        @Override
                        public void run() {
                            buttonStartThread.setText("50%");
                        }
                    });*/

                    /*
                    - Step: 10
                    - we can use runOnUiThread() which is an activity method
                     */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttonStartThread.setText("50%");
                        }
                    });
                }


                Log.d(TAG, "startThread: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

/*
There are two way of starting the background thread:
1. Extending the Thread class and override the run method
or
2. Implementing the runnable interface and passing this runnable
 object to a new thread.
 Handler and Looper: those make running these thread and
 communicates between different threads more convenient.
Asynctask, HandlerThread, ThreadPoolExecutor are the heigher
extraction classes. will discuss later.
HOW HANDLER WORKS?-Later
SOMETHING ABOUT UI THREAD:
After app started the ui thread just not terminated even if it does not have any work to do. instead it wait for new input like click a button
and do something or perform any other works. The mechanism which keeps alive the ui thread is called Message Queue. Intead just one piece of
works it has whole lot of work to execute. one after another. Then there something called looper which loops through the message queue. and dispatches
messages sequencially. and this is literally a infinite for loop. and unless we create a purpose we dont leave this loop. There something called
handler which is responsible for    getting the messages of works into the message queue and it is used to get work from background thread to main
thread.
 */