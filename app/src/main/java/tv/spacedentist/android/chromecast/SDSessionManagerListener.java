package tv.spacedentist.android.chromecast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;

import java.io.IOException;

import tv.spacedentist.android.BuildConfig;
import tv.spacedentist.android.util.SDLogger;

class SDSessionManagerListener implements SessionManagerListener<CastSession>, Cast.MessageReceivedCallback {

    private static final String TAG = "SDSessionManagerListener";

    private SDLogger mLogger;

    SDSessionManagerListener(SDLogger logger) {
        mLogger = logger;
    }

    @Override
    public void onSessionStarting(CastSession session) {
        mLogger.d(TAG, "onSessionStarting");
        trySetMessageReceivedCallbacks(session);
    }

    @Override
    public void onSessionStarted(CastSession session, String sessionId) {
        mLogger.d(TAG, "onSessionStarted: " + sessionId);
        trySetMessageReceivedCallbacks(session);
    }

    @Override
    public void onSessionStartFailed(CastSession session, int error) {
        mLogger.d(TAG, "onSessionStartFailed: " + error);
    }

    @Override
    public void onSessionEnding(CastSession session) {
        mLogger.i(TAG, "onSessionEnding");
    }

    @Override
    public void onSessionEnded(CastSession session, int error) {
        mLogger.i(TAG, "onSessionEnded" + error);
    }

    @Override
    public void onSessionResuming(CastSession session, String sessionId) {
        mLogger.i(TAG, "onSessionResuming" + sessionId);
        trySetMessageReceivedCallbacks(session);
    }

    @Override
    public void onSessionResumed(CastSession session, boolean wasSuspended) {
        mLogger.i(TAG, "onSessionResumed: " + wasSuspended);
        trySetMessageReceivedCallbacks(session);
    }

    @Override
    public void onSessionResumeFailed(CastSession session, int result) {
        mLogger.d(TAG, "onConnectionFailed: " + result);
    }

    @Override
    public void onSessionSuspended(CastSession session, int reason) {
        mLogger.d(TAG, "onConnectionSuspended: " + reason);
    }

    @Override
    public void onMessageReceived(CastDevice castDevice, String namespace, String message) {
        mLogger.d(TAG, "onMessageReceived: " + message);
    }

    private void trySetMessageReceivedCallbacks(CastSession session) {
        try {
            session.setMessageReceivedCallbacks(BuildConfig.CHROMECAST_APP_NAMESPACE, this);
        } catch (IOException e) {
            mLogger.e(TAG, "we couldn't register for message received callbacks", e);
        }
    }
}
