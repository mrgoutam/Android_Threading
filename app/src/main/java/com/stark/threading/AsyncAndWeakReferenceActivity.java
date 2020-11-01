package com.stark.threading;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class AsyncAndWeakReferenceActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_and_weak_reference);
        progressBar = findViewById(R.id.progress_bar);
    }

    public void startAsyncTask(View view) {
        ExampleAsyscTask asyscTask = new ExampleAsyscTask(this);
        asyscTask.execute(10);
    }

    private static class ExampleAsyscTask extends AsyncTask<Integer, Integer, String>{
        private WeakReference<AsyncAndWeakReferenceActivity> asyncAndWeakReferenceActivityWeakReference;
        ExampleAsyscTask(AsyncAndWeakReferenceActivity asyncAndWeakReferenceActivity){
            asyncAndWeakReferenceActivityWeakReference =
                    new WeakReference<AsyncAndWeakReferenceActivity>(asyncAndWeakReferenceActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AsyncAndWeakReferenceActivity activity = asyncAndWeakReferenceActivityWeakReference.get();
            if (activity == null || activity.isFinishing()){
                return;
            }
            activity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            for (int i = 0; i < integers[0]; i++) {
                publishProgress((i*100/integers[0]));
                SystemClock.sleep(1000);
            }
            return "Finished!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            AsyncAndWeakReferenceActivity activity = asyncAndWeakReferenceActivityWeakReference.get();
            if (activity == null || activity.isFinishing()){
                return;
            }
            activity.progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AsyncAndWeakReferenceActivity activity = asyncAndWeakReferenceActivityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            activity.progressBar.setProgress(0);
            activity.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}

/*
========================Theory of AsyncTask=============================================
INTRODUCTION:
AsyncTask is an abstract class which means that the class extending it need to implement the below mentioned methods and
also since the class is generic type the class extending it should specify these 3 generic types

private class SomeTask extends AsyncTask<Params, Progress, Result>

AsyncTask's Generic Types:
1. Params -> The type of parameters sent to background thread to be used upon execution.
2. Progress -> The progress which is used to publish the progress on the main UI thread.
3. Result -> The result which is obtained after the background thread has finished execution.

USAGE:
It should generally be used for short operations which should ideally be finished in a matter of few seconds.

METHODS:
1. onPreExecute() --> This method is executed just before the background thread is created.
    It runs on the main UI thread.
2. doInBackground(Params...) --> This method is invoked on the background thread immediately after onPreExecute() has finished
    runs on background thread. This method can also call publishProgress(Progress...) method to publish progress on the main UI
    thread which is received by the on ProgressUpdate(Progress...) method.
    It runs on the background thread.
3. onProgressUpdate(Progress...) --> This method is invoked on the main UI thread just after publishProgress(Progress...) is
    invoked on used to display/update the progress on the main UI.
4. onPostExecute(Result) --> This method is invoked on the main UI thread just after the background thread has finished execution
    and has returned the result to this method. This result can be used to do anything the developer wants to do with the result.
    It runs on the main UI thread.
    ........................................................
So, |      Method Name        |        Thread Name         |
    ........................................................
    | onPreExecute            | Main UI Thread             |
    | doInBackground          | Background Thread          |
    | onProgressUpdate        | Main UI Thread             |
    | onPostExecute           | Main UI Thread             |
    ''''''''''''''''''''''''''''''''''''''''''''''''''''''''
Execution Order:

 onPreExecute() ---------------------------------------------------------------------------->
 onProgressUpdate(Progress...)   <----  publishProgress(Progress...)   <----  doInBackground(Params...)
 onPostExecute(Result)  <--------------------------------------------------------------------

Why AsyncTask?
If our app deals with the data loading from internet or doing any other task which can take time and you are using the main
thread to execute the operations of that task then it introduces a lot of load on the main UI thread and we cannot perform
any other operation. The UI will freeze.

Therefore, AsyncTask came into existence so that the load of doing such things is reduces on the main UI thread and pushed to
background thread. When the task is completed the result will published to the main UI thread by calling onPostExecute().

Why should AsyncTask be used ideally for small operations?
As you may have seen that 3 methods in AsyncTask run on main UI thread and only two (including publishProgress) method run on
the background thread. This type of working can block the main UI thread even though the task to be performed is being done in
the background. Let me explain...
    If your app is loading something from the internet through a network request where the area in which the smartphone is
    being used has a slow internet connection due to any reason and your user presses the refresh button then another
    AsyncTask will be created and the methods of that AsyncTask would also run on the main thread but they won’t be able
    to run it since there is already an AsyncTask running and the onPreExecute() method of the second AsyncTask will not
    be invoked on the main thread until the previous AsyncTask is done with it’s work and onPostExecute() is called, hence
    blocking all other methods in the application used to respond to user clicks or any other functionality, hence freezing
    the app.
If you want to perform long operations in the background then you can use other classes like Executor, ThreadPoolExecutor and
FutureTask.
========================End================================================
 */
/*
https://medium.com/google-developer-experts/finally-understanding-how-references-work-in-android-and-java-26a0d9c92f83
========================Theory of Weak Reference===========================
A Reference is the direction of an object that is annotated, so you can access it.

Java has by default 4 types of references:
    1. Strong
    2. Soft
    3. Weak
    4. Phantom

Strong Reference:
    Strong References are the ordinary references in java. Anytime we create a new object, a strong reference is by default
    created. For example, when we do
            MyObject object = new MyObject();
    A new object MyObject is created, and a strong reference to it is stored in object. This object is strongly reachable -
    that means it can be reached through a chain of strong references. That will prevent the Garbage collector of picking it up
    and destroy it, which is what we mostly want. But now, let's see an example where this can play against us.

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new MyAsyncTask().execute();
    }

    private class MyAsyncTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            return doSomeStuff();
        }

        private Object doSomeStuff() {
            //do something to get result
            return new MyObject();
        }
    }
}

    The AsyncTask will be created and executed together with the Activity onCreate() method. But here we have a problem:
    the inner class needs to be accessing the outside class during its entire lifetime.

    What happens when the Activity is destroyed? The AsyncTask is holding a reference to the Activity, and the Activity
    cannot be collected by the GC. This is what we called a memory leak.

    The memory leak actually happens not only when the Activity is destroyed per-se, but as well when it is forcibly
    destroyed by the system due to a change in the configuration or more memory is needed, etc. If the AsyncTask is
    complex (i.e., keeps references to Views in the Activity, etc) it could even lead to crashes, since the view references
    are null.

    So how can prevent this problem from ever happening again? Let´s explain the other type of references:

Weak Reference:
    a weak reference is a reference not strong enough to keep the object in memory. If we try to determine if the object
    is strongly referenced and it happened to be through WeakReferences, the object will be garbage-collected

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MyAsyncTask(this).execute();
    }
    private static class MyAsyncTask extends AsyncTask {
        private WeakReference<MainActivity> mainActivity;

        public MyAsyncTask(MainActivity mainActivity) {
            this.mainActivity = new WeakReference<>(mainActivity);
        }
        @Override
        protected Object doInBackground(Object[] params) {
            return doSomeStuff();
        }
        private Object doSomeStuff() {
            //do something to get result
            return new Object();
        }
        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
            if (mainActivity.get() != null){
                //adapt contents
            }
        }
    }
}

    the Activity within the inner class is now referenced as follows:

    private WeakReference<MainActivity> mainActivity;

    What will happen here? When the Activity stops existing, since it is hold through the means of a WeakReference,
    it can be Garbage Collected. Therefore no memory leaks will happen.

Soft Reference:
    Think of a SoftReference as a stronger WeakReference. Whereas a WeakReference will be collected immediately,
    a SoftReference will beg to the GC to stay in memory unless there is no other option. If the object is a SoftReference,
    GC will decide what to do based on the ecosystem conditions”. This makes a SoftReference very useful for the
    implementation of a cache: as long as the memory is plenty, we do not have to worry of manually removing objects.

Phantom Reference:
    ?
========================End================================================
 */