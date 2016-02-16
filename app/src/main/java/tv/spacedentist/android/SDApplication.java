package tv.spacedentist.android;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

import tv.spacedentist.android.chromecast.SDApiClientCreator;
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

        final Context context = getApplicationContext();

        SDApiClientCreator apiClientCreator = new SDApiClientCreator() {
            @Override
            public GoogleApiClient get(Cast.CastOptions castOptions,
                                       GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                                       GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
                return new GoogleApiClient.Builder(context)
                        .addApi(Cast.API, castOptions)
                        .addConnectionCallbacks(connectionCallbacks)
                        .addOnConnectionFailedListener(connectionFailedListener)
                        .build();
            }
        };

        SDMediaRouter mediaRouter = new SDMediaRouter(context);
        SDMediaRouteSelector mediaRouteSelector = new SDMediaRouteSelector();
        mChromecastManager = new SDChromecastManager(mediaRouter, mediaRouteSelector, apiClientCreator);
    }

    public SDChromecastManager getChromecastManager() {
        return mChromecastManager;
    }
}
