package com.tari9bro.coloringb;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.io.OutputStream;
import java.util.Locale;

import com.tari9bro.coloringb.util.Library;
import com.tari9bro.coloringb.util.Settings;


// TODO store global state differently see https://stackoverflow.com/questions/708012/how-to-declare-global-variables-in-android#708317
/**
 * Application of the coloring app.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // general exception handler, enables us to get error logs by email
        final Thread.UncaughtExceptionHandler oldDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();

                // collect error information
                StringBuilder sb = new StringBuilder();

                // header
                sb.append("An unexpected exception occurred!\n\nPlease send an error report to the developers!\n\n");

                // app version
                String version;
                try {
                    version = App.this.getPackageManager().getPackageInfo("com.tari9bro.coloringb", 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    version = "undefined";
                }
                sb.append(String.format("App version: %s\n", version));

                // device model
                String model = Build.MODEL;
                if (!model.startsWith(Build.MANUFACTURER)) {
                    model = Build.MANUFACTURER + " " + model;
                }
                sb.append(String.format("Device model: %s\n", model));

                // SDK
                sb.append(String.format(Locale.US, "Android SDK level: %d\n", Build.VERSION.SDK_INT));

                // thread name
                sb.append(String.format("Thread: %s\n\n", thread.getName()));

                // exception name
                sb.append("Stacktrace:\n");

                // exception trace
                sb.append(Log.getStackTraceString(ex));

                Context c = App.this.getApplicationContext();
                try {
                    OutputStream os = c.openFileOutput(getResources().getString(Integer.parseInt("error_log.txt")), Context.MODE_PRIVATE);
                    Settings.writeText(os, sb.toString());
                } catch (Exception e) {
                    // we could not write the error, nothing we can do there
                }

                // continue with old handling
                if (oldDefaultUncaughtExceptionHandler != null) {
                    oldDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
                } else {
                    System.exit(-1);
                }
            }
        });

        // set up the coloring library
        Library.initialize(this);
    }
}
