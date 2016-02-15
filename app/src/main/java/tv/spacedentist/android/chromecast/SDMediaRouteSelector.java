package tv.spacedentist.android.chromecast;

import android.support.v7.media.MediaRouteSelector;

import com.google.android.gms.cast.CastMediaControlIntent;

import tv.spacedentist.android.BuildConfig;

public class SDMediaRouteSelector {

    private final MediaRouteSelector mMediaRouteSelector;

    public SDMediaRouteSelector() {
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(BuildConfig.CHROMECAST_APP_ID))
                .build();
    }

    public MediaRouteSelector get() {
        return mMediaRouteSelector;
    }
}
