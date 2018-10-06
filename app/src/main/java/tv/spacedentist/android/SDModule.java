package tv.spacedentist.android;

import com.google.android.gms.cast.framework.CastContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.util.SDLogger;
import tv.spacedentist.android.util.SDLoggerAndroid;
import tv.spacedentist.android.util.SDLoggerNull;

@Module
public class SDModule {

    private final SDApplication mApplication;

    public SDModule(SDApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    SDLogger provideLogger() {
        return BuildConfig.DEBUG ? new SDLoggerAndroid() : new SDLoggerNull();
    }

    @Provides
    CastContext provideCastContext() {
        return CastContext.getSharedInstance(mApplication);
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
