package tv.spacedentist.android.chromecast

import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.CastStateListener
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.common.api.Status
import tv.spacedentist.android.BuildConfig
import tv.spacedentist.android.SDComponent
import tv.spacedentist.android.util.SDLogger
import java.util.*

/**
 * This used to be where most of the Chormecast logic happens, but now it mostly just redistributes
 * cast state changed callbacks
 */
class SDChromecastManager(private val component: SDComponent) : CastStateListener {

    private val mListeners = HashSet<CastStateListener>()
    private val mSessionManagerListener: SessionManagerListener<CastSession>

    private val logger: SDLogger
        get() = component.logger

    private val castContext: CastContext
        get() = component.castContext

    val selectedDeviceFriendlyName: String?
        get() = castContext.sessionManager.currentCastSession?.castDevice?.friendlyName

    val castDevice: CastDevice?
        get() = castContext.sessionManager.currentCastSession?.castDevice

    val currentCastState: Int
        get() = castContext.castState

    init {
        mSessionManagerListener = SDSessionManagerListener(logger)
    }

    fun addCastStateListener(listener: CastStateListener) {
        mListeners.add(listener)
    }

    fun removeCastStateListener(listener: CastStateListener) {
        mListeners.remove(listener)
    }

    override fun onCastStateChanged(state: Int) {
        logger.i(TAG, "onCastStateChanged: $state")
        for (listener in mListeners) {
            listener.onCastStateChanged(state)
        }
    }

    private fun onMessageResult(status: Status) {
        if (!status.isSuccess) {
            logger.e(TAG, "Sending message failed")
        }
    }

    fun sendChromecastMessage(message: String) {
        logger.i(TAG, "sending message: '" + message + "' state: " + castContext.castState)

        castContext.sessionManager.currentCastSession?.let {
            it.sendMessage(BuildConfig.CHROMECAST_APP_NAMESPACE, message)
                    .setResultCallback(this::onMessageResult)
        }
    }

    fun onResume() {
        castContext.sessionManager.addSessionManagerListener(mSessionManagerListener, CastSession::class.java)
        castContext.addCastStateListener(this)
    }

    fun onPause() {
        castContext.sessionManager.addSessionManagerListener(mSessionManagerListener, CastSession::class.java)
        castContext.removeCastStateListener(this)
    }

    fun endCurrentSession() {
        castContext.sessionManager.endCurrentSession(true)
    }

    companion object {
        private const val TAG = "SDChromecastManager"
    }
}
