package tv.spacedentist.android;

import android.app.Application;

import tv.spacedentist.android.chromecast.SDChromecastManager;

/**
 * We keep the global state alive here (Chromecast client connection) so that it doesn't get
 * destroyed and that we don't have to deal with lifecycle events in the activity.
 */
public class SDApplication extends Application {
    public static final String TAG = SDApplication.class.getSimpleName();

    private static SDChromecastManager mChromecastManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mChromecastManager = new SDChromecastManager(getApplicationContext());
    }

    public SDChromecastManager getChromecastManager() {
        return mChromecastManager;
    }
}
