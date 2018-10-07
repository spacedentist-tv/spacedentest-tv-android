package tv.spacedentist.android.chromecast

import com.google.android.gms.cast.Cast
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import tv.spacedentist.android.BuildConfig
import tv.spacedentist.android.util.SDLogger
import java.io.IOException

internal class SDSessionManagerListener(private val mLogger: SDLogger) : SessionManagerListener<CastSession>, Cast.MessageReceivedCallback {

    override fun onSessionStarting(session: CastSession) {
        mLogger.d(TAG, "onSessionStarting")
        trySetMessageReceivedCallbacks(session)
    }

    override fun onSessionStarted(session: CastSession, sessionId: String) {
        mLogger.d(TAG, "onSessionStarted: $sessionId")
        trySetMessageReceivedCallbacks(session)
    }

    override fun onSessionStartFailed(session: CastSession, error: Int) {
        mLogger.d(TAG, "onSessionStartFailed: $error")
    }

    override fun onSessionEnding(session: CastSession) {
        mLogger.i(TAG, "onSessionEnding")
    }

    override fun onSessionEnded(session: CastSession, error: Int) {
        mLogger.i(TAG, "onSessionEnded $error")
    }

    override fun onSessionResuming(session: CastSession, sessionId: String) {
        mLogger.i(TAG, "onSessionResuming $sessionId")
        trySetMessageReceivedCallbacks(session)
    }

    override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
        mLogger.i(TAG, "onSessionResumed: $wasSuspended")
        trySetMessageReceivedCallbacks(session)
    }

    override fun onSessionResumeFailed(session: CastSession, result: Int) {
        mLogger.d(TAG, "onConnectionFailed: $result")
    }

    override fun onSessionSuspended(session: CastSession, reason: Int) {
        mLogger.d(TAG, "onConnectionSuspended: $reason")
    }

    override fun onMessageReceived(castDevice: CastDevice, namespace: String, message: String) {
        mLogger.d(TAG, "onMessageReceived: $message")
    }

    private fun trySetMessageReceivedCallbacks(session: CastSession) {
        try {
            session.setMessageReceivedCallbacks(BuildConfig.CHROMECAST_APP_NAMESPACE, this)
        } catch (e: IOException) {
            mLogger.e(TAG, "we couldn't register for message received callbacks", e)
        }

    }

    companion object {
        private val TAG = "SDSessionManagerListener"
    }
}
