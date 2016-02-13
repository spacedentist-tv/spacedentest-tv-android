package tv.spacedentist.android.chromecast;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.LaunchOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.Set;

import tv.spacedentist.android.R;

public class SDChromecastManager implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Cast.MessageReceivedCallback {

    public static final String TAG = SDChromecastManager.class.getSimpleName();

    private static final Set<SDChromecastManagerListener> mListeners = Sets.newHashSet();

    private final Context mContext;
    private final MediaRouter.Callback mMediaRouterCallback;

    private final MediaRouter mMediaRouter;
    private final MediaRouteSelector mMediaRouteSelector;

    private final String mChromecastApplicationId;
    private final String mChromecastApplicationNamespace;

    private GoogleApiClient mApiClient;
    private CastDevice mSelectedDevice;

    private String mSessionId;
    private String mStatus;

    private boolean mWaitingForReconnect = false;

    public SDChromecastManager(Context context) {
        mContext = context;

        mChromecastApplicationId = context.getString(R.string.chromecast_application_id);
        mChromecastApplicationNamespace = context.getString(R.string.chomrecast_application_namespace);

        mMediaRouter = MediaRouter.getInstance(context);

        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(mChromecastApplicationId))
                .build();

        mMediaRouterCallback = new MediaRouter.Callback() {
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
                super.onRouteAdded(router, route);
                broadcastConnectionStateChange();
            }

            @Override
            public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
                super.onRouteRemoved(router, route);
                broadcastConnectionStateChange();
            }
        };
    }

    public void addListener(SDChromecastManagerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(SDChromecastManagerListener listener) {
        mListeners.remove(listener);
    }

    private void broadcastConnectionStateChange() {
        for (SDChromecastManagerListener listener : mListeners) {
            listener.onConnectionStateChanged();
        }
    }

    public void setMediaRouteActionProvider(MediaRouteActionProvider mediaRouteActionProvider) {
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
    }

    public void launch() {
        try {
            Cast.CastApi.launchApplication(mApiClient, mChromecastApplicationId, new LaunchOptions())
                    .setResultCallback(
                            new ResultCallback<Cast.ApplicationConnectionResult>() {
                                @Override
                                public void onResult(Cast.ApplicationConnectionResult result) {
                                    Status status = result.getStatus();

                                    Log.d(TAG, "launch application result: " + status);

                                    if (status.isSuccess()) {
                                        mSessionId = result.getSessionId();
                                        mStatus = result.getApplicationStatus();

                                        Log.d(TAG, String.format("launch success %s %s", mSessionId, mStatus));

                                        broadcastConnectionStateChange();
                                        connectChannel();
                                    } else {
                                        Log.d(TAG, "launch failed");
                                        tearDown();
                                    }
                                }
                            }
                    );

        } catch (Exception e) {
            Log.e(TAG, "Failed to launch application", e);
        }
    }

    private void connect(MediaRouter.RouteInfo routeInfo) {
        mSelectedDevice = CastDevice.getFromBundle(routeInfo.getExtras());

        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions.builder(mSelectedDevice, new Cast.Listener() {
            @Override
            public void onApplicationStatusChanged() {
                if (mApiClient != null) {
                    mStatus = Cast.CastApi.getApplicationStatus(mApiClient);
                    Log.d(TAG, "onApplicationStatusChanged: " + mStatus);

                    broadcastConnectionStateChange();
                }
            }

            @Override
            public void onVolumeChanged() {
                if (mApiClient != null) {
                    Log.d(TAG, "onVolumeChanged: " + Cast.CastApi.getVolume(mApiClient));
                }
            }

            @Override
            public void onApplicationDisconnected(int errorCode) {
                tearDown();
            }
        });

        mApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Cast.API, apiOptionsBuilder.build())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
        broadcastConnectionStateChange();
    }

    public boolean isConnecting() {
        return mApiClient != null && mApiClient.isConnecting();
    }

    public boolean isConnected() {
        return mApiClient != null && mApiClient.isConnected();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        if (mWaitingForReconnect) {
            mWaitingForReconnect = false;
            connectChannel();
        } else {
            launch();
        }
    }

    private void connectChannel() {
        try {
            Cast.CastApi.setMessageReceivedCallbacks(mApiClient,
                    mChromecastApplicationNamespace,
                    this);
        } catch (IOException e) {
            Log.e(TAG, "Exception while creating channel", e);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);
        mWaitingForReconnect = true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: " + result);
        tearDown();
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace,
                                  String message) {
        Log.d(TAG, "onMessageReceived: " + message);
    }

    private void tearDown() {
        Log.d(TAG, "teardown");
        if (mApiClient != null) {
            if (mApiClient.isConnected() && mSessionId != null) {
                Cast.CastApi.stopApplication(mApiClient, mSessionId);
            }

            if (mApiClient.isConnected() || mApiClient.isConnecting()) {
                try {
                    Cast.CastApi.removeMessageReceivedCallbacks(
                            mApiClient,
                            mChromecastApplicationNamespace);
                } catch (IOException e) {
                    Log.e(TAG, "Exception while removing channel", e);
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

    public void addMediaRouterCallback() {
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback, MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    public void removeMediaRouterCallback() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    public boolean isRouteAvailable() {
        return mMediaRouter.isRouteAvailable(mMediaRouteSelector, MediaRouter.AVAILABILITY_FLAG_IGNORE_DEFAULT_ROUTE);
    }

    public void sendChromecastMessage(String message) {
        Cast.CastApi.sendMessage(mApiClient, mChromecastApplicationNamespace, message)
                .setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status result) {
                                if (!result.isSuccess()) {
                                    Log.e(TAG, "Sending message failed");
                                }
                            }
                        });
    }

}
