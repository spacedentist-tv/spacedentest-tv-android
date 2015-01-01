package tv.spacedentist.spacedentist_tv_android;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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


public class SDMainActivity extends ActionBarActivity
                            implements GoogleApiClient.OnConnectionFailedListener,
                                       GoogleApiClient.ConnectionCallbacks,
                                       Cast.MessageReceivedCallback {

    private static final String TAG = SDMainActivity.class.getSimpleName();
    private static final String SDTV_MSG_KEY = "sdtv_msg";
    private static final String SDTV_REMOTE_CONTROL_KEY = "rc";
    private static final String SDTV_KEY_KEY = "key";

    enum SDButton {
        BUTTON_ONE(R.id.button_one, "1"),
        BUTTON_TWO(R.id.button_two, "2"),
        BUTTON_THREE(R.id.button_three, "3"),
        BUTTON_FOUR(R.id.button_four, "4"),
        BUTTON_FIVE(R.id.button_five, "5"),
        BUTTON_SIX(R.id.button_six, "6"),
        BUTTON_SEVEN(R.id.button_seven, "7"),
        BUTTON_EIGHT(R.id.button_eight, "8"),
        BUTTON_NINE(R.id.button_nine, "9"),
        BUTTON_ZERO(R.id.button_zero, "0"),
        BUTTON_TRUFAX(R.id.button_trufax, "cycle");

        private final int mResId;
        private final String mKey;

        SDButton(int resId, String key) {
            mResId = resId;
            mKey = key;
        }

        public int getResId() {
            return mResId;
        }

        public String getKey() {
            return mKey;
        }

        public static SDButton fromResId(int resId) {
            for (SDButton button : SDButton.values()) {
                if (button.getResId() == resId) {
                    return button;
                }
            }

            return null;
        }
    }

    private Cast.Listener mCastClientListener = new Cast.Listener() {
        @Override
        public void onApplicationStatusChanged() {
            if (mApiClient != null) {
                Log.d(TAG, "onApplicationStatusChanged: " + Cast.CastApi.getApplicationStatus(mApiClient));
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
            //String routeId = info.getId();

            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(mSelectedDevice, mCastClientListener);

            mApiClient = new GoogleApiClient.Builder(SDMainActivity.this)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(SDMainActivity.this)
                    .addOnConnectionFailedListener(SDMainActivity.this)
                    .build();

            mApiClient.connect();
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            tearDown();
            mSelectedDevice = null;
        }
    }

    private final MediaRouterCallback mMediaRouterCallback = new MediaRouterCallback();
    private GoogleApiClient mApiClient;
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private CastDevice mSelectedDevice;
    private boolean mWaitingForReconnect = false;
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

                    Cast.CastApi.sendMessage(mApiClient, getApplicationContext().getString(R.string.application_namespace), message.toString())
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (SDButton button : SDButton.values()) {
            findViewById(button.getResId()).setOnClickListener(mButtonListener);
        }

        mMediaRouter = MediaRouter.getInstance(getApplicationContext());

        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(getApplicationContext().getString(R.string.application_id)))
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            mMediaRouter.removeCallback(mMediaRouterCallback);
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    protected void onStop() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        if (mWaitingForReconnect) {
            mWaitingForReconnect = false;
            //reconnectChannels();
        } else {
            try {
                Cast.CastApi.launchApplication(mApiClient, getApplicationContext().getString(R.string.application_id), false)
                    .setResultCallback(
                        new ResultCallback<Cast.ApplicationConnectionResult>() {
                            @Override
                            public void onResult(Cast.ApplicationConnectionResult result) {
                                Status status = result.getStatus();

                                Log.d(TAG, "launch application result: " + status);

                                if (status.isSuccess()) {
                                    ApplicationMetadata applicationMetadata =
                                            result.getApplicationMetadata();
                                    String sessionId = result.getSessionId();
                                    String applicationStatus = result.getApplicationStatus();
                                    boolean wasLaunched = result.getWasLaunched();

                                    Log.d(TAG, String.format("launch success %s %s %b", sessionId, applicationStatus, wasLaunched));

                                    try {
                                        Cast.CastApi.setMessageReceivedCallbacks(mApiClient,
                                                getApplicationContext().getString(R.string.application_namespace),
                                                SDMainActivity.this);
                                    } catch (IOException e) {
                                        Log.e(TAG, "Exception while creating channel", e);
                                    }
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

    @Override
    public void onConnectionSuspended(int cause) {
        mWaitingForReconnect = true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        tearDown();
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace,
                                  String message) {
        Log.d(TAG, "onMessageReceived: " + message);
    }

    private void tearDown() {

    }
}
