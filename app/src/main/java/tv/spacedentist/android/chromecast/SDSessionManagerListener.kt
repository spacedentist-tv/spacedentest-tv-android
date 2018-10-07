package tv.spacedentist.android.chromecast

import com.google.android.gms.cast.Cast
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import tv.spacedentist.android.BuildConfig
import tv.spacedentist.android.util.SDLogger
import java.io.IOException

internal class SDSessionManagerListener(private val logger: SDLogger) : SessionManagerListener<CastSession>, Cast.MessageReceivedCallback {

    override fun onSessionStarting(session: CastSession) {
        logger.d(TAG, "onSessionStarting")
        trySetMessageReceivedCallbacks(session)
    }

    override fun onSessionStarted(session: CastSession, sessionId: String) {
        logger.d(TAG, "onSessionStarted: $sessionId")
        trySetMessageReceivedCallbacks(session)
    }

    override fun onSessionStartFailed(session: CastSession, error: Int) {
        logger.d(TAG, "onSessionStartFailed: $error")
    }

    override fun onSessionEnding(session: CastSession) {
        logger.i(TAG, "onSessionEnding")
    }

    override fun onSessionEnded(session: CastSession, error: Int) {
        logger.i(TAG, "onSessionEnded $error")
    }

    override fun onSessionResuming(session: CastSession, sessionId: String) {
        logger.i(TAG, "onSessionResuming $sessionId")
        trySetMessageReceivedCallbacks(session)
    }

    override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
        logger.i(TAG, "onSessionResumed: $wasSuspended")
        trySetMessageReceivedCallbacks(session)
    }

    override fun onSessionResumeFailed(session: CastSession, result: Int) {
        logger.d(TAG, "onConnectionFailed: $result")
    }

    override fun onSessionSuspended(session: CastSession, reason: Int) {
        logger.d(TAG, "onConnectionSuspended: $reason")
    }

    override fun onMessageReceived(castDevice: CastDevice, namespace: String, message: String) {
        logger.d(TAG, "onMessageReceived: $message")
    }

    private fun trySetMessageReceivedCallbacks(session: CastSession) {
        try {
            session.setMessageReceivedCallbacks(BuildConfig.CHROMECAST_APP_NAMESPACE, this)
        } catch (e: IOException) {
            logger.e(TAG, "we couldn't register for message received callbacks", e)
        }

    }

    companion object {
        private const val TAG = "SDSessionManagerListener"
    }
}
