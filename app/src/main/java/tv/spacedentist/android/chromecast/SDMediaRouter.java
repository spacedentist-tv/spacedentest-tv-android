package tv.spacedentist.android.chromecast;

import android.content.Context;
import android.support.v7.media.MediaRouter;

public class SDMediaRouter {
    MediaRouter mMediaRouter;

    public SDMediaRouter(Context context) {
        mMediaRouter = MediaRouter.getInstance(context);
    }

    public void selectRoute(MediaRouter.RouteInfo routeInfo) {
        mMediaRouter.selectRoute(routeInfo);
    }

    public MediaRouter.RouteInfo getDefaultRoute() {
        return mMediaRouter.getDefaultRoute();
    }

    public void addCallback(SDMediaRouteSelector mediaRouteSelector, MediaRouter.Callback callback, int flags) {
        mMediaRouter.addCallback(mediaRouteSelector.get(), callback, flags);
    }

    public void removeCallback(MediaRouter.Callback callback) {
        mMediaRouter.removeCallback(callback);
    }

    public boolean isRouteAvailable(SDMediaRouteSelector mediaRouteSelector, int flags) {
        return mMediaRouter.isRouteAvailable(mediaRouteSelector.get(), flags);
    }
}
