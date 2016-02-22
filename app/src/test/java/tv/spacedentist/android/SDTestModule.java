package tv.spacedentist.android;

import com.google.android.gms.cast.Cast;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tv.spacedentist.android.chromecast.SDChromecastManagerTestCase;
import tv.spacedentist.android.chromecast.SDMediaRouteSelector;
import tv.spacedentist.android.chromecast.SDMediaRouter;
import tv.spacedentist.android.util.SDLogger;

@Module(
        injects = {
                SDChromecastManagerTestCase.class
        },
        includes = {
                SDModule.class
        },
        overrides = true,
        library = true)
public class SDTestModule {
    @Mock private SDLogger mLogger;
    @Mock private Cast.CastApi mCastApi;
    @Mock private SDMediaRouter mMediaRouter;
    @Mock private SDMediaRouteSelector mMediaRouteSelector;

    public SDTestModule() {
        MockitoAnnotations.initMocks(this);
    }

    @Provides
    @Singleton
    Cast.CastApi provideCastApi() {
        return mCastApi;
    }

    @Provides
    @Singleton
    SDLogger provideLogger() {
        return mLogger;
    }

    @Provides
    SDMediaRouter provideMediaRouter() {
        return mMediaRouter;
    }

    @Provides
    SDMediaRouteSelector provideMediaRouteSelector() {
        return mMediaRouteSelector;
    }
}
