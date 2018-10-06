package tv.spacedentist.android;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;

import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.IdRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Scene;
import androidx.transition.TransitionManager;
import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.util.SDLogger;
import tv.spacedentist.android.view.SDButton;
import tv.spacedentist.android.view.SDButtonClickSender;

/**
 * The activity class that does everything. Luckily there's not that much to do, but would be nice
 * to refactor and make more testable.
 */
public class SDMainActivity extends AppCompatActivity implements CastStateListener {

    private static final String TAG = "SDMainActivity";

    @Inject SDNotificationManager mNotificationManager;
    @Inject SDChromecastManager mChromecastManager;
    @Inject SDLogger mLogger;

    private Scene mDisconnectedScene;
    private Scene mChromecastScene;
    private Scene mConnectingScene;
    private Scene mConnectedScene;

    private Scene mCurrentScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((SDApplication) getApplication()).getComponent().inject(this);

        mLogger.d(TAG, "onCreate()");

        mChromecastManager.addCastStateListener(this);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(R.layout.action_bar_title);
        }

        final ViewGroup sceneRoot = findViewById(R.id.scene_root);

        // add the button click listeners
        final SDButtonClickSender buttonClickSender = new SDButtonClickSender(mChromecastManager);
        sceneRoot.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                // if we're adding the keypad attach the key listeners
                if (child.getId() == R.id.connected) {
                    for (SDButton button : SDButton.values()) {
                        final View buttonView = findViewById(button.getResId());
                        if (buttonView != null) {
                            buttonView.setOnClickListener(buttonClickSender);
                        }
                    }
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {}
        });

        mDisconnectedScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_no_chromecast, this);
        mChromecastScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_chromecast, this);
        mConnectingScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_connecting, this);
        mConnectedScene = Scene.getSceneForLayout(sceneRoot, R.layout.scene_connected, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLogger.d(TAG, "onDestroy()");

        mChromecastManager.removeCastStateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLogger.d(TAG, "onResume()");
        mNotificationManager.setActivityOpen(true);
        showCorrectView(mChromecastManager.getCurrentCastState());
        mChromecastManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLogger.d(TAG, "onPause()");
        mNotificationManager.setActivityOpen(false);
        mChromecastManager.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

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
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);

        setMenuItem(menu, R.id.app_id, BuildConfig.APPLICATION_ID, BuildConfig.DEBUG);
        setMenuItem(menu, R.id.app_flavor, BuildConfig.FLAVOR, BuildConfig.DEBUG);
        setMenuItem(menu, R.id.app_build_type, BuildConfig.BUILD_TYPE, BuildConfig.DEBUG);
        setMenuItem(menu, R.id.app_version, String.format(Locale.US, "%s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE), true);

        return true;
    }

    @Override
    public void onCastStateChanged(int state) {
        showCorrectView(state);
    }

    private void showCorrectView(int state) {
        final Scene newScene = state == CastState.CONNECTING ?
                mConnectingScene :
                        state == CastState.CONNECTED ?
                                mConnectedScene :
                                state != CastState.NO_DEVICES_AVAILABLE ?
                                        mChromecastScene :
                                        mDisconnectedScene;

        if (!newScene.equals(mCurrentScene)) {
            mCurrentScene = newScene;
            TransitionManager.go(mCurrentScene);
        }
    }
}
