package tv.spacedentist.android;

import android.support.v7.media.MediaRouter;
import android.support.annotation.IdRes;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;

import javax.inject.Inject;

import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.chromecast.SDChromecastManagerListener;
import tv.spacedentist.android.chromecast.SDMediaRouterCallback;
import tv.spacedentist.android.view.SDButton;
import tv.spacedentist.android.view.SDButtonClickSender;
import tv.spacedentist.android.view.SDTextView;

/**
 * The activity class that does everything. Luckily there's not that much to do, but would be nice
 * to refactor and make more testable.
 */
public class SDMainActivity extends AppCompatActivity implements SDChromecastManagerListener {

    private static final String TAG = SDMainActivity.class.getSimpleName();

    @Inject SDChromecastManager mChromecastManager;
    private MediaRouter.Callback mMediaRouterCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ((SDApplication) getApplication()).injectMembers(this);

        mMediaRouterCallback = new SDMediaRouterCallback(mChromecastManager);

        mChromecastManager.addListener(this);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            LayoutInflater inflater = LayoutInflater.from(this);
            View titleView = inflater.inflate(R.layout.action_bar_title, null);
            actionBar.setCustomView(titleView);
        }

        final SDButtonClickSender buttonClickSender = new SDButtonClickSender(mChromecastManager);

        for (SDButton button : SDButton.values()) {
            findViewById(button.getResId()).setOnClickListener(buttonClickSender);
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
        showCorrectView();
        setDisconnectedText();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        mChromecastManager.addMediaRouterCallback(mMediaRouterCallback);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        mChromecastManager.removeMediaRouterCallback(mMediaRouterCallback);
        super.onStop();
    }

    private void setMenuItem(Menu menu, @IdRes int itemId, String title, boolean visible) {
        MenuItem menuItem = menu.findItem(itemId);
        menuItem.setTitle(title);
        menuItem.setVisible(visible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mChromecastManager.setMediaRouteActionProvider(mediaRouteActionProvider);

        setMenuItem(menu, R.id.app_id, BuildConfig.APPLICATION_ID, BuildConfig.DEBUG);
        setMenuItem(menu, R.id.app_flavor, BuildConfig.FLAVOR, BuildConfig.DEBUG);
        setMenuItem(menu, R.id.app_build_type, BuildConfig.BUILD_TYPE, BuildConfig.DEBUG);
        setMenuItem(menu, R.id.app_version, String.format(Locale.US, "%s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE), true);

        return true;
    }

    @Override
    public void onConnectionStateChanged() {
        setDisconnectedText();
        showCorrectView();
    }

    private void setDisconnectedText() {
        ((SDTextView) findViewById(R.id.disconnected)).setText((mChromecastManager.isRouteAvailable()) ?
                R.string.disconnected_text:
                R.string.no_chromecast_text);
    }

    private void showCorrectView() {
        if (mChromecastManager.isConnecting()) {
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
