package tv.spacedentist.android;

import android.content.Context;

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

@Module(
        injects = {
                Context.class,
                SDMainActivity.class,
                SDMediaRouter.class,
                SDMediaRouteSelector.class,
                SDApiClientCreator.class,
                SDChromecastManager.class,
                com.google.android.gms.cast.Cast.CastApi.class
        }
)
public class SDModule {

    private SDApplication mApplication;

    /** No-args constructor needed to override in SDTestModule */
    public SDModule() {}
    public SDModule(SDApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    SDLogger provideLogger() {
        return new SDLogger();
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
    Cast.CastApi provideCastApi() {
        return Cast.CastApi;
    }

    @Provides
    SDApiClientCreator provideApiClientCreator() {
        return new SDApiClientCreator() {
            @Override
            public GoogleApiClient get(Cast.CastOptions castOptions,
                                       GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                                       GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
                return new GoogleApiClient.Builder(mApplication)
                        .addApi(Cast.API, castOptions)
                        .addConnectionCallbacks(connectionCallbacks)
                        .addOnConnectionFailedListener(connectionFailedListener)
                        .build();
            }
        };
    }

    @Provides
    @Singleton
    SDChromecastManager provideChromecastManager() {
        return new SDChromecastManager(mApplication);
    }
}