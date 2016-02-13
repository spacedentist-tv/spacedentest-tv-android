package tv.spacedentist.android;

import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.chromecast.SDChromecastManagerListener;
import tv.spacedentist.android.view.SDButton;
import tv.spacedentist.android.view.SDButtonListener;
import tv.spacedentist.android.view.SDTextView;

/**
 * The activity class that does everything. Luckily there's not that much to do, but would be nice
 * to refactor and make more testable.
 */
public class SDMainActivity extends AppCompatActivity implements SDChromecastManagerListener {

    private static final String TAG = SDMainActivity.class.getSimpleName();

    private SDChromecastManager mChromecastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mChromecastManager = ((SDApplication) getApplication()).getChromecastManager();

        mChromecastManager.addListener(this);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            LayoutInflater inflater = LayoutInflater.from(this);
            View titleView = inflater.inflate(R.layout.action_bar_title, null);
            actionBar.setCustomView(titleView);
        }

        final SDButtonListener sdButtonListener = new SDButtonListener(mChromecastManager);

        for (SDButton button : SDButton.values()) {
            findViewById(button.getResId()).setOnClickListener(sdButtonListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mChromecastManager.removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        mChromecastManager.addMediaRouterCallback();
        showCorrectView();
        setDisconnectedText();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        mChromecastManager.removeMediaRouterCallback();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        mChromecastManager.addMediaRouterCallback();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        mChromecastManager.removeMediaRouterCallback();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mChromecastManager.setMediaRouteActionProvider(mediaRouteActionProvider);

        return true;
    }

    @Override
    public void onConnectionStateChanged() {
        setDisconnectedText();
        showCorrectView();
    }

    private void setDisconnectedText() {
        boolean routeAvailable = mChromecastManager.isRouteAvailable();

        Log.d(TAG, String.format("setDisconnectedText: %b", routeAvailable));
        ((SDTextView) findViewById(R.id.disconnected)).setText((routeAvailable) ?
                R.string.disconnected_text:
                R.string.no_chromecast_text);
    }

    private void showCorrectView() {
        if (mChromecastManager.isConnecting()) {
            // we are connecting
            findViewById(R.id.connecting_spinner).setVisibility(View.VISIBLE);
            findViewById(R.id.disconnected).setVisibility(View.GONE);
            findViewById(R.id.connected).setVisibility(View.GONE);
        } else {
            boolean connected = mChromecastManager.isConnected();

            findViewById(R.id.connecting_spinner).setVisibility(View.GONE);
            findViewById(R.id.disconnected).setVisibility(connected ? View.GONE : View.VISIBLE);
            findViewById(R.id.connected).setVisibility(connected ? View.VISIBLE : View.GONE);
        }
    }
}
