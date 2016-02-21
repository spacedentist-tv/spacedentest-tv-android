package tv.spacedentist.android.chromecast;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.LaunchOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import dagger.MembersInjector;
import tv.spacedentist.android.BuildConfig;
import tv.spacedentist.android.util.SDLogger;

/**
 * This is where most of the Chormecast logic happens
 */
public class SDChromecastManager implements
        SDMediaRouterCallback.Callback,
        SDCastListener.Callback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Cast.MessageReceivedCallback {

    private static final String TAG = SDChromecastManager.class.getSimpleName();
    private static final Set<SDChromecastManagerListener> mListeners = Sets.newHashSet();

    @Inject SDMediaRouter mMediaRouter;
    @Inject SDMediaRouteSelector mMediaRouteSelector;
    @Inject SDApiClientCreator mApiClientCreator;

    // These are not static final so that tests can inject mocked versions
    private Cast.CastApi CAST_API = Cast.CastApi;
    private SDLogger mLogger = new SDLogger();
    private GoogleApiClient mApiClient;

    private CastDevice mSelectedDevice;
    private String mSessionId;
    private String mStatus;

    private boolean mWaitingForReconnect = false;

    public SDChromecastManager(MembersInjector<SDChromecastManager> injector) {
        injector.injectMembers(this);
    }

    public void addListener(SDChromecastManagerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(SDChromecastManagerListener listener) {
        mListeners.remove(listener);
    }

    protected void broadcastConnectionStateChange() {
        for (SDChromecastManagerListener listener : mListeners) {
            listener.onConnectionStateChanged();
        }
    }

    public void setMediaRouteActionProvider(MediaRouteActionProvider mediaRouteActionProvider) {
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector.get());
    }

    private ResultCallback<Cast.ApplicationConnectionResult> LAUNCH_APPLICATION_CALLBACK = new ResultCallback<Cast.ApplicationConnectionResult>() {
        @Override
        public void onResult(@NonNull Cast.ApplicationConnectionResult result) {
            Status status = result.getStatus();
            mLogger.d(TAG, "launch application result: " + status);

            if (status.isSuccess()) {
                mSessionId = result.getSessionId();
                mStatus = result.getApplicationStatus();
                mLogger.d(TAG, String.format("launch success %s %s", mSessionId, mStatus));
                broadcastConnectionStateChange();
                connectChannel();
            } else {
                mLogger.d(TAG, "launch failed");
                tearDown();
            }
        }
    };

    public void launch() {
        try {
            CAST_API.launchApplication(mApiClient, BuildConfig.CHROMECAST_APP_ID, new LaunchOptions()).setResultCallback(LAUNCH_APPLICATION_CALLBACK);
        } catch (Exception e) {
            mLogger.e(TAG, "Failed to launch application", e);
        }
    }

    protected void connect(MediaRouter.RouteInfo routeInfo) {
        mSelectedDevice = CastDevice.getFromBundle(routeInfo.getExtras());
        Cast.CastOptions apiOptionsBuilder = new Cast.CastOptions.Builder(mSelectedDevice, new SDCastListener(this)).build();
        mApiClient = mApiClientCreator.get(apiOptionsBuilder, this, this);
        mApiClient.connect();
        broadcastConnectionStateChange();
    }

    @Override
    public void onApplicationStatusChanged() {
        if (mApiClient != null) {
            mStatus = CAST_API.getApplicationStatus(mApiClient);
            mLogger.d(TAG, "onApplicationStatusChanged: " + mStatus);

            broadcastConnectionStateChange();
        }
    }

    @Override
    public void onVolumeChanged() {
        if (mApiClient != null) {
            mLogger.d(TAG, "onVolumeChanged: " + CAST_API.getVolume(mApiClient));
        }
    }

    @Override
    public void onApplicationDisconnected(int errorCode) {
        tearDown();
    }

    protected void setApiClient(GoogleApiClient apiClient) {
        mApiClient = apiClient;
    }

    public boolean isConnecting() {
        return mApiClient != null && mApiClient.isConnecting();
    }

    public boolean isConnected() {
        return mApiClient != null && mApiClient.isConnected();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLogger.d(TAG, "onConnected");

        if (mWaitingForReconnect) {
            mWaitingForReconnect = false;
            connectChannel();
        } else {
            launch();
        }
    }

    private void connectChannel() {
        try {
            CAST_API.setMessageReceivedCallbacks(mApiClient, BuildConfig.CHROMECAST_APP_NAMESPACE, this);
        } catch (IOException e) {
            mLogger.e(TAG, "Exception while creating channel", e);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mLogger.d(TAG, "onConnectionSuspended: " + cause);
        mWaitingForReconnect = true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        mLogger.d(TAG, "onConnectionFailed: " + result);
        tearDown();
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        mLogger.d(TAG, "onMessageReceived: " + message);
    }

    protected void tearDown() {
        mLogger.d(TAG, "teardown");
        if (mApiClient != null) {
            if (mApiClient.isConnected() && mSessionId != null) {
                CAST_API.stopApplication(mApiClient, mSessionId);
            }

            if (mApiClient.isConnected() || mApiClient.isConnecting()) {
                try {
                    CAST_API.removeMessageReceivedCallbacks(
                            mApiClient,
                            BuildConfig.CHROMECAST_APP_NAMESPACE);
                } catch (IOException e) {
                    mLogger.e(TAG, "Exception while removing channel", e);
                }
                mApiClient.disconnect();
            }
            mApiClient = null;
        }
        mSelectedDevice = null;
        mWaitingForReconnect = false;
        mSessionId = null;

        mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());

        broadcastConnectionStateChange();
    }

    public void addMediaRouterCallback(MediaRouter.Callback mediaRouterCallback) {
        mMediaRouter.addCallback(mMediaRouteSelector, mediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    public void removeMediaRouterCallback(MediaRouter.Callback mediaRouterCallback) {
        mMediaRouter.removeCallback(mediaRouterCallback);
    }

    public boolean isRouteAvailable() {
        return mMediaRouter.isRouteAvailable(mMediaRouteSelector, MediaRouter.AVAILABILITY_FLAG_IGNORE_DEFAULT_ROUTE);
    }

    private ResultCallback<Status> SEND_MESSAGE_CALLBACK = new ResultCallback<Status>() {
        @Override
        public void onResult(@NonNull Status result) {
            if (!result.isSuccess()) {
                mLogger.e(TAG, "Sending message failed");
            }
        }
    };

    public void sendChromecastMessage(String message) {
        CAST_API.sendMessage(mApiClient, BuildConfig.CHROMECAST_APP_NAMESPACE, message)
                .setResultCallback(SEND_MESSAGE_CALLBACK);
    }

    @Override
    public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo routeInfo) {
        connect(routeInfo);
    }

    @Override
    public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
        tearDown();
    }

    @Override
    public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
        broadcastConnectionStateChange();
    }

    @Override
    public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
        broadcastConnectionStateChange();
    }

    @Override
    public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo route) {
        broadcastConnectionStateChange();
    }
}
