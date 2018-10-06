package tv.spacedentist.android;

import com.google.android.gms.cast.framework.CastContext;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tv.spacedentist.android.util.SDLogger;
import tv.spacedentist.android.util.SDLoggerAndroid;

public class SDTestModule extends SDModule {
    private static final SDLogger mLogger = new SDLoggerAndroid();
    @Mock private CastContext mCastApi;
    @Mock private SDNotificationManager mNotificationManager;

    public SDTestModule() {
        super(null);
        MockitoAnnotations.initMocks(this);
    }

    @Override
    CastContext provideCastContext() {
        return mCastApi;
    }

    @Override
    SDLogger provideLogger() {
        return mLogger;
    }

    @Override
    SDNotificationManager provideNotificationManager() {
        return mNotificationManager;
    }
}
