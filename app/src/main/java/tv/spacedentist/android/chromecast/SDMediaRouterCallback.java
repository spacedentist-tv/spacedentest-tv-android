package tv.spacedentist.android.chromecast;

import android.content.Context;
import android.support.v7.media.MediaRouter;

public class SDMediaRouterCallback extends MediaRouter.Callback {

    public interface Callback {
        void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo routeInfo);
        void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info);
        void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route);
        void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route);
        void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo route);
    }

    private final Callback mCallback;

    public SDMediaRouterCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo routeInfo) {
        mCallback.onRouteSelected(router, routeInfo);
    }

    @Override
    public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
        mCallback.onRouteUnselected(router, info);
    }

    @Override
    public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
        mCallback.onRouteAdded(router, route);
    }

    @Override
    public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
        mCallback.onRouteRemoved(router, route);
    }

    @Override
    public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo route) {
        mCallback.onRouteChanged(router, route);
    }
}
