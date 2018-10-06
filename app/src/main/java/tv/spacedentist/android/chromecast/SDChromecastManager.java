package tv.spacedentist.android.chromecast;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.common.api.Status;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import tv.spacedentist.android.BuildConfig;
import tv.spacedentist.android.SDComponent;
import tv.spacedentist.android.util.SDLogger;

/**
 * This is where most of the Chormecast logic happens
 */
public class SDChromecastManager implements CastStateListener {

    private static final String TAG = "SDChromecastManager";

    private final Set<CastStateListener> mListeners = new HashSet<>();
    private final SessionManagerListener<CastSession> mSessionManagerListener;

    @Inject SDLogger mLogger;
    @Inject CastContext mCastContext;

    public SDChromecastManager(SDComponent component) {
        component.inject(this);

        mSessionManagerListener = new SDSessionManagerListener(mLogger);
    }

    public void addCastStateListener(CastStateListener listener) {
        mListeners.add(listener);
    }

    public void removeCastStateListener(CastStateListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void onCastStateChanged(int state) {
        mLogger.i(TAG, "onCastStateChanged: " + state);
        for (CastStateListener listener : mListeners) {
            listener.onCastStateChanged(state);
        }
    }

    public @Nullable String getSelectedDeviceFriendlyName() {
        return mCastContext.getSessionManager().getCurrentCastSession().getCastDevice().getFriendlyName();
    }

    private void onMessageResult(Status status) {
        if (!status.isSuccess()) {
            mLogger.e(TAG, "Sending message failed");
        }
    };

    public void sendChromecastMessage(String message) {
        mLogger.i(TAG, "sending message: '" + message + "' state: " + mCastContext.getCastState());

        CastSession castSession = mCastContext.getSessionManager().getCurrentCastSession();

        if (castSession != null) {
            mLogger.i(TAG, "there's a session");
            castSession
                    .sendMessage(BuildConfig.CHROMECAST_APP_NAMESPACE, message)
                    .setResultCallback(this::onMessageResult);
        } else {
            mLogger.i(TAG, "no current session");
        }
    }

    public void onResume() {
        mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);
        mCastContext.addCastStateListener(this);
    }

    public void onPause() {
        mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);
        mCastContext.removeCastStateListener(this);
    }

    public void endCurrentSession() {
        mCastContext.getSessionManager().endCurrentSession(true);
    }

    public CastDevice getCastDevice() {
        return mCastContext.getSessionManager().getCurrentCastSession().getCastDevice();
    }

    public int getCurrentCastState() {
        return mCastContext.getCastState();
    }
}
