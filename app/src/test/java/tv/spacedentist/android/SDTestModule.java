package tv.spacedentist.android;

import com.google.android.gms.cast.Cast;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tv.spacedentist.android.chromecast.SDMediaRouteSelector;
import tv.spacedentist.android.chromecast.SDMediaRouter;
import tv.spacedentist.android.util.SDLogger;

public class SDTestModule extends SDModule {
    private static final SDLogger mLogger = SDLogger.JAVA_LOGGER;
    @Mock private Cast.CastApi mCastApi;
    @Mock private SDMediaRouter mMediaRouter;
    @Mock private SDMediaRouteSelector mMediaRouteSelector;
    @Mock private SDNotificationManager mNotificationManager;

    public SDTestModule() {
        super(null);
        MockitoAnnotations.initMocks(this);
    }

    @Override
    Cast.CastApi provideCastApi() {
        return mCastApi;
    }

    @Override
    SDLogger provideLogger() {
        return mLogger;
    }

    @Override
    SDMediaRouter provideMediaRouter() {
        return mMediaRouter;
    }

    @Override
    SDMediaRouteSelector provideMediaRouteSelector() {
        return mMediaRouteSelector;
    }

    @Override
    SDNotificationManager provideNotificationManager() {
        return mNotificationManager;
    }
}
