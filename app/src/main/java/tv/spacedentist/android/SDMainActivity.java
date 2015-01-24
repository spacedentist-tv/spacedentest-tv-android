package tv.spacedentist.android;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

import java.io.IOException;

import tv.spacedentist.android.view.SDTextView;

public class SDMainActivity extends ActionBarActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        Cast.MessageReceivedCallback {

    private static final String TAG = SDMainActivity.class.getSimpleName();

    private static final String SDTV_MSG_KEY = "sdtv_msg";
    private static final String SDTV_REMOTE_CONTROL_KEY = "rc";
    private static final String SDTV_KEY_KEY = "key";

    private static final String SAVE_KEY_SESSION_ID = "save_key_session_id";
    private static final String SAVE_KEY_STATUS = "save_key_status";

    private String mSessionId;
    private String mStatus;

    private SDApplication getSDApplication() {
        return ((SDApplication) getApplication());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View titleView = inflater.inflate(R.layout.action_bar_title, null);
        actionBar.setCustomView(titleView);

        for (SDButton button : SDButton.values()) {
            findViewById(button.getResId()).setOnClickListener(mButtonListener);
        }

        if (savedInstanceState != null) {
            mSessionId = savedInstanceState.getString(SAVE_KEY_SESSION_ID);
            mStatus = savedInstanceState.getString(SAVE_KEY_STATUS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(SAVE_KEY_SESSION_ID, mSessionId);
        outState.putString(SAVE_KEY_STATUS, mStatus);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        getSDApplication().mMediaRouter.addCallback(getSDApplication().mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);

        showCorrectView();
        setStatusText();
        setDisconnectedText();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        getSDApplication().mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        getSDApplication().mMediaRouter.addCallback(getSDApplication().mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        getSDApplication().mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(getSDApplication().mMediaRouteSelector);

        return true;
    }

    private Cast.Listener mCastClientListener = new Cast.Listener() {
        @Override
        public void onApplicationStatusChanged() {


            if (getSDApplication().mApiClient != null) {
                mStatus = Cast.CastApi.getApplicationStatus(getSDApplication().mApiClient);
                Log.d(TAG, "onApplicationStatusChanged: " + mStatus);

                setStatusText();
            }
        }

        @Override
        public void onVolumeChanged() {
            if (getSDApplication().mApiClient != null) {
                Log.d(TAG, "onVolumeChanged: " + Cast.CastApi.getVolume(getSDApplication().mApiClient));
            }
        }

        @Override
        public void onApplicationDisconnected(int errorCode) {
            tearDown();
        }
    };

    private void connect(MediaRouter.RouteInfo routeInfo) {
        getSDApplication().mSelectedDevice = CastDevice.getFromBundle(routeInfo.getExtras());

        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions.builder(getSDApplication().mSelectedDevice, mCastClientListener);

        getSDApplication().mApiClient = new GoogleApiClient.Builder(getSDApplication().getApplicationContext())
                .addApi(Cast.API, apiOptionsBuilder.build())
                .addConnectionCallbacks(SDMainActivity.this)
                .addOnConnectionFailedListener(SDMainActivity.this)
                .build();

        getSDApplication().mApiClient.connect();
        showCorrectView();
    }

    private class MediaRouterCallback extends MediaRouter.Callback {

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
            setDisconnectedText();
        }

        @Override
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteRemoved(router, route);
            setDisconnectedText();
        }
    }

    private final MediaRouterCallback mMediaRouterCallback = new MediaRouterCallback();

    private boolean mWaitingForReconnect = false;

    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getSDApplication().mApiClient != null) {

                try {
                    SDButton button = SDButton.fromResId(v.getId());
                    Log.d(TAG, "Button was clicked: " + button.getKey());
                    JSONObject message = new JSONObject();
                    message.put(SDTV_MSG_KEY, SDTV_REMOTE_CONTROL_KEY);
                    message.put(SDTV_KEY_KEY, button.getKey());

                    Cast.CastApi.sendMessage(getSDApplication().mApiClient, getSDApplication().mApplicationNamespace, message.toString())
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

    private void setStatusText() {
        //SDTextView statusTextView = ((SDTextView) getView().findViewById(R.id.status_text));
        //statusTextView.setText(mStatus != null ? mStatus : "");
    }

    private void setDisconnectedText() {
        boolean routeAvailable = getSDApplication().mMediaRouter.isRouteAvailable(getSDApplication().mMediaRouteSelector,
                MediaRouter.AVAILABILITY_FLAG_IGNORE_DEFAULT_ROUTE);

        Log.d(TAG, String.format("setDisconnectedText: %b", routeAvailable));
        ((SDTextView) findViewById(R.id.disconnected)).setText((routeAvailable) ?
                R.string.disconnected_text:
                R.string.no_chromecast_text);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        if (mWaitingForReconnect) {
            mWaitingForReconnect = false;
            connectChannel();
        } else {
            try {
                Cast.CastApi.launchApplication(getSDApplication().mApiClient, getSDApplication().mApplicationId, false)
                        .setResultCallback(
                                new ResultCallback<Cast.ApplicationConnectionResult>() {
                                    @Override
                                    public void onResult(Cast.ApplicationConnectionResult result) {
                                        Status status = result.getStatus();

                                        Log.d(TAG, "launch application result: " + status);

                                        if (status.isSuccess()) {
                                            mSessionId = result.getSessionId();
                                            mStatus = result.getApplicationStatus();
                                            setStatusText();

                                            Log.d(TAG, String.format("launch success %s %s", mSessionId, mStatus));

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
        if (getSDApplication().mApiClient != null && getSDApplication().mApiClient.isConnecting()) {
            // we are connecting
            findViewById(R.id.connecting_spinner).setVisibility(View.VISIBLE);
            findViewById(R.id.disconnected).setVisibility(View.GONE);
            findViewById(R.id.connected).setVisibility(View.GONE);
        } else {
            boolean connected = getSDApplication().mApiClient != null&& getSDApplication().mApiClient.isConnected();

            findViewById(R.id.connecting_spinner).setVisibility(View.GONE);
            findViewById(R.id.disconnected).setVisibility(connected ? View.GONE : View.VISIBLE);
            findViewById(R.id.connected).setVisibility(connected ? View.VISIBLE : View.GONE);
        }
    }

    private void connectChannel() {
        try {
            Cast.CastApi.setMessageReceivedCallbacks(getSDApplication().mApiClient,
                    getSDApplication().mApplicationNamespace,
                    SDMainActivity.this);
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
        if (getSDApplication().mApiClient != null) {
            if (getSDApplication().mApiClient.isConnected() && mSessionId != null) {
                Cast.CastApi.stopApplication(getSDApplication().mApiClient, mSessionId);
            }

            if (getSDApplication().mApiClient.isConnected() || getSDApplication().mApiClient.isConnecting()) {
                try {
                    Cast.CastApi.removeMessageReceivedCallbacks(
                            getSDApplication().mApiClient,
                            getSDApplication().mApplicationNamespace);
                } catch (IOException e) {
                    Log.e(TAG, "Exception while removing channel", e);
                }
                getSDApplication().mApiClient.disconnect();
            }
            getSDApplication().mApiClient = null;
        }
        getSDApplication().mSelectedDevice = null;
        mWaitingForReconnect = false;
        mSessionId = null;

        getSDApplication().mMediaRouter.selectRoute(getSDApplication().mMediaRouter.getDefaultRoute());

        showCorrectView();
    }
}
