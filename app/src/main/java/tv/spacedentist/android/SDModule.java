package tv.spacedentist.android;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tv.spacedentist.android.chromecast.SDApiClientCreator;
import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.chromecast.SDMediaRouteSelector;
import tv.spacedentist.android.chromecast.SDMediaRouter;
import tv.spacedentist.android.util.SDLogger;
import tv.spacedentist.android.util.SDLoggerAndroid;

@Module
public class SDModule {

    private final SDApplication mApplication;

    public SDModule(SDApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    SDLogger provideLogger() {
        return new SDLoggerAndroid();
    }

    @Provides
    SDMediaRouter provideMediaRouter() {
        return new SDMediaRouter(mApplication);
    }

    @Provides
    SDMediaRouteSelector provideMediaRouteSelector() {
        return new SDMediaRouteSelector();
    }

    @Provides
    @Singleton
    Cast.CastApi provideCastApi() {
        return Cast.CastApi;
    }

    @Provides
    SDApiClientCreator provideApiClientCreator() {
        return (castOptions, connectionCallbacks, connectionFailedListener) -> new GoogleApiClient.Builder(mApplication)
                .addApi(Cast.API, castOptions)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .build();
    }

    @Provides
    @Singleton
    SDChromecastManager provideChromecastManager() {
        return new SDChromecastManager(mApplication.getComponent());
    }

    @Provides
    @Singleton
    SDNotificationManager provideNotificationManager() {
        return new SDNotificationManager(mApplication, mApplication.getComponent());
    }
}
