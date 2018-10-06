package tv.spacedentist.android;

import com.google.android.gms.cast.framework.CastContext;

import javax.inject.Singleton;

import dagger.Component;
import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.util.SDLogger;

@Singleton
@Component(modules = SDModule.class)
public interface SDComponent {
    SDLogger getLogger();
    SDChromecastManager getChromecastManager();
    CastContext getCastContext();

    void inject(SDMainActivity mainActivity);
    void inject(SDChromecastManager chromecastManager);
    void inject(SDNotificationManager notificationManager);
}
