package tv.spacedentist.android;

import android.app.Application;

/**
 * We keep the global state alive here (Chromecast client connection) so that it doesn't get
 * destroyed and that we don't have to deal with lifecycle events in the activity.
 */
public class SDApplication extends Application {

    private SDComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mComponent = DaggerSDComponent.builder()
                .sDModule(new SDModule(this))
                .build();
    }

    public SDComponent getComponent() {
        return mComponent;
    }
}
