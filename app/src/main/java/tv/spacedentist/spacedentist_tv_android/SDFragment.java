package tv.spacedentist.spacedentist_tv_android;



import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.io.IOException;
import java.util.zip.Inflater;

import tv.spacedentist.spacedentist_tv_android.view.SDTextView;

/**
 * Created by coffey on 01/01/15.
 */
public class SDFragment extends Fragment
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        Cast.MessageReceivedCallback {

    private static final String TAG = SDFragment.class.getSimpleName();

    private static final String SDTV_MSG_KEY = "sdtv_msg";
    private static final String SDTV_REMOTE_CONTROL_KEY = "rc";
    private static final String SDTV_KEY_KEY = "key";

    private String mApplicationNamespace;
    private String mApplicationId;

    private String mSessionId;
    private String mStatus;

    private Cast.Listener mCastClientListener = new Cast.Listener() {
        @Override
        public void onApplicationStatusChanged() {
            if (mApiClient != null) {
                mStatus = Cast.CastApi.getApplicationStatus(mApiClient);
                Log.d(TAG, "onApplicationStatusChanged: " + mStatus);

                setStatusText();
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
    };

    private class MediaRouterCallback extends MediaRouter.Callback {

        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            mSelectedDevice = CastDevice.getFromBundle(info.getExtras());

            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(mSelectedDevice, mCastClientListener);

            mApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(SDFragment.this)
                    .addOnConnectionFailedListener(SDFragment.this)
                    .build();

            mApiClient.connect();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            tearDown();
            mSelectedDevice = null;
        }

        private void setDisconnectedText(MediaRouter router) {
            ((SDTextView) getView().findViewById(R.id.disconnected)).setText((router.getRoutes().size() == 0) ?
                                                                                R.string.no_chromecast_text:
                                                                                R.string.disconnected_text);
        }

        @Override
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteAdded(router, route);
            setDisconnectedText(router);
        }

        @Override
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteRemoved(router, route);
            setDisconnectedText(router);
        }
    }

    private final MediaRouterCallback mMediaRouterCallback = new MediaRouterCallback();
    private GoogleApiClient mApiClient;
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private CastDevice mSelectedDevice;

    private boolean mWaitingForReconnect = false;
    private boolean mApplicationStarted = false;

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mApiClient != null) {

                try {
                    SDButton button = SDButton.fromResId(v.getId());
                    Log.d(TAG, "Button was clicked: " + button.getKey());
                    JSONObject message = new JSONObject();
                    message.put(SDTV_MSG_KEY, SDTV_REMOTE_CONTROL_KEY);
                    message.put(SDTV_KEY_KEY, button.getKey());

                    Cast.CastApi.sendMessage(mApiClient, mApplicationNamespace, message.toString())
                            .setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status result) {
                                            if (!result.isSuccess()) {
                                                Log.e(TAG, "Sending message failed");
                                            }
                                        }
                                    });
                } catch (Exception e) {
                    Log.e(TAG, "Exception while sending message", e);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu");

        inflater.inflate(R.menu.menu_main, menu);

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
    }

    private void setStatusText() {
        SDTextView statusTextView = ((SDTextView) getView().findViewById(R.id.status_text));
        statusTextView.setText(mStatus != null ? mStatus : "");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        final Context context = getActivity().getApplicationContext();
        mApplicationId = context.getString(R.string.application_id);
        mApplicationNamespace = context.getString(R.string.application_namespace);

        for (SDButton button : SDButton.values()) {
            getView().findViewById(button.getResId()).setOnClickListener(mButtonListener);
        }

        mMediaRouter = MediaRouter.getInstance(context);

        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(mApplicationId))
                .build();

        showCorrectView();
        setStatusText();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    public void onPause() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");

        mMediaRouter.removeCallback(mMediaRouterCallback);

        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        if (mWaitingForReconnect) {
            mWaitingForReconnect = false;
            connectChannel();
        } else {
            try {
                Cast.CastApi.launchApplication(mApiClient, mApplicationId, false)
                        .setResultCallback(
                                new ResultCallback<Cast.ApplicationConnectionResult>() {
                                    @Override
                                    public void onResult(Cast.ApplicationConnectionResult result) {
                                        Status status = result.getStatus();

                                        Log.d(TAG, "launch application result: " + status);

                                        if (status.isSuccess()) {
                                            ApplicationMetadata applicationMetadata =
                                                    result.getApplicationMetadata();
                                            mSessionId = result.getSessionId();
                                            String applicationStatus = result.getApplicationStatus();
                                            boolean wasLaunched = result.getWasLaunched();

                                            Log.d(TAG, String.format("launch success %s %s %b", mSessionId, applicationStatus, wasLaunched));

                                            mApplicationStarted = true;
                                            showCorrectView();
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
    }

    private void showCorrectView() {
        getView().findViewById(R.id.disconnected).setVisibility(mApplicationStarted ? View.GONE : View.VISIBLE);
        getView().findViewById(R.id.connected).setVisibility(mApplicationStarted ? View.VISIBLE : View.GONE);
    }

    private void connectChannel() {
        try {
            Cast.CastApi.setMessageReceivedCallbacks(mApiClient,
                    mApplicationNamespace,
                    SDFragment.this);
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
    public void onConnectionFailed(ConnectionResult result) {
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
            if (mApplicationStarted) {
                if (mApiClient.isConnected() || mApiClient.isConnecting()) {
                    try {
                        Cast.CastApi.stopApplication(mApiClient, mSessionId);
                        Cast.CastApi.removeMessageReceivedCallbacks(
                                    mApiClient,
                                    mApplicationNamespace);
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while removing channel", e);
                    }
                    mApiClient.disconnect();
                }
                mApplicationStarted = false;
            }
            mApiClient = null;
        }
        mSelectedDevice = null;
        mWaitingForReconnect = false;
        mSessionId = null;

        mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());

        showCorrectView();
    }
}
