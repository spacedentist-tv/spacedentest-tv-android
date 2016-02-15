package tv.spacedentist.android.chromecast;

import android.content.Context;
import android.support.v7.media.MediaRouter;

public class SDMediaRouterCallback extends MediaRouter.Callback {

    Context mContext;
    SDChromecastManager mChromecaseManager;

    public SDMediaRouterCallback(Context context, SDChromecastManager chromecastManager) {
        mContext = context;
        mChromecaseManager = chromecastManager;
    }

    @Override
    public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo routeInfo) {
        mChromecaseManager.connect(mContext, routeInfo);
    }

    @Override
    public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
        mChromecaseManager.tearDown();
    }

    @Override
    public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
        mChromecaseManager.broadcastConnectionStateChange();
    }

    @Override
    public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
        mChromecaseManager.broadcastConnectionStateChange();
    }

    @Override
    public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo route) {
        mChromecaseManager.broadcastConnectionStateChange();
    }
}
