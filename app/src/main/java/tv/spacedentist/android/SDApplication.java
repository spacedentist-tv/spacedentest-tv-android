package tv.spacedentist.android;

import android.app.Application;

import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.chromecast.SDMediaRouteSelector;
import tv.spacedentist.android.chromecast.SDMediaRouter;

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

        SDMediaRouter mediaRouter = new SDMediaRouter(getApplicationContext());
        SDMediaRouteSelector mediaRouteSelector = new SDMediaRouteSelector();
        mChromecastManager = new SDChromecastManager(mediaRouter, mediaRouteSelector);
    }

    public SDChromecastManager getChromecastManager() {
        return mChromecastManager;
    }
}
