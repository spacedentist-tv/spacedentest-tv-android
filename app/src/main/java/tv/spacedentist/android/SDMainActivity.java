package tv.spacedentist.android;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import javax.inject.Inject;

import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.chromecast.SDChromecastManagerListener;
import tv.spacedentist.android.util.SDLogger;
import tv.spacedentist.android.view.SDButton;
import tv.spacedentist.android.view.SDButtonClickSender;

/**
 * The activity class that does everything. Luckily there's not that much to do, but would be nice
 * to refactor and make more testable.
 */
public class SDMainActivity extends AppCompatActivity implements SDChromecastManagerListener {

    private static final String TAG = "SDMainActivity";

    @Inject SDNotificationManager mNotificationManager;
    @Inject SDChromecastManager mChromecastManager;
    @Inject SDLogger mLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SDApplication) getApplication()).getComponent().inject(this);

        mLogger.d(TAG, "onCreate()");

        mChromecastManager.addListener(this);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(R.layout.action_bar_title);
        }

        final SDButtonClickSender buttonClickSender = new SDButtonClickSender(mChromecastManager);

        for (SDButton button : SDButton.values()) {
            final View buttonView = findViewById(button.getResId());
            if (buttonView != null) {
                buttonView.setOnClickListener(buttonClickSender);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLogger.d(TAG, "onDestroy()");

        mChromecastManager.removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLogger.d(TAG, "onResume()");
        showCorrectView();
        setDisconnectedText();

        mNotificationManager.setActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLogger.d(TAG, "onPause()");

        mNotificationManager.setActivityOpen(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLogger.d(TAG, "onStart() route available: " + mChromecastManager.isRouteAvailable());

    }

    @Override
    public void onStop() {
        mLogger.d(TAG, "onStop");
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
        final TextView disconnectedView = (TextView) findViewById(R.id.disconnected);

        if (disconnectedView != null) {
            disconnectedView
                    .setText((mChromecastManager.isRouteAvailable()) ?
                            R.string.disconnected_text:
                            R.string.no_chromecast_text);
        }
    }

    private void showCorrectView() {
        final View connectingSpinnerView = findViewById(R.id.connecting_spinner);
        final View disconnectedView = findViewById(R.id.disconnected);
        final View connectedView = findViewById(R.id.connected);

        final boolean connecting = mChromecastManager.isConnecting();
        final boolean connected = mChromecastManager.isConnected();

        if (connectingSpinnerView != null) {
            connectingSpinnerView
                    .setVisibility(connecting ?
                            View.VISIBLE:
                            View.GONE);
        }

        if (disconnectedView != null) {
            disconnectedView
                    .setVisibility(connecting ?
                            View.GONE:
                            connected ?
                                    View.GONE:
                                    View.VISIBLE);
        }

        if (connectedView != null) {
            connectedView
                    .setVisibility(connecting ?
                            View.GONE :
                            connected ?
                                    View.VISIBLE:
                                    View.GONE);
        }
    }
}
