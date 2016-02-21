package tv.spacedentist.android;

import android.app.Application;

import dagger.MembersInjector;
import dagger.ObjectGraph;

/**
 * We keep the global state alive here (Chromecast client connection) so that it doesn't get
 * destroyed and that we don't have to deal with lifecycle events in the activity.
 */
public class SDApplication extends Application implements MembersInjector {
    public static final String TAG = SDApplication.class.getSimpleName();

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(new SDModule(this));
    }

    @Override
    public void injectMembers(Object instance) {
        mObjectGraph.inject(instance);
    }
}
