package tv.spacedentist.android;

import android.app.Application;
import android.content.Context;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by coffey on 12/01/15.
 */
public class SDApplication extends Application {

    public GoogleApiClient mApiClient;
    public MediaRouter mMediaRouter;
    public MediaRouteSelector mMediaRouteSelector;
    public CastDevice mSelectedDevice;

    public String mApplicationId;
    public String mApplicationNamespace;

    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = getApplicationContext();
        mApplicationId = context.getString(R.string.application_id);
        mApplicationNamespace = context.getString(R.string.application_namespace);

        mMediaRouter = MediaRouter.getInstance(context);

        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(mApplicationId))
                .build();
    }
}
