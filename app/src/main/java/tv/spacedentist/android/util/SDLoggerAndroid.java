package tv.spacedentist.android.util;

import android.util.Log;

public class SDLoggerAndroid implements SDLogger {

    public void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }
}
