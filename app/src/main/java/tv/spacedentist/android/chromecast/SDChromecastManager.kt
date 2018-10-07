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
import javax.inject.Inject

/**
 * This used to be where most of the Chormecast logic happens, but now it mostly just redistributes
 * cast state changed callbacks
 */
class SDChromecastManager(component: SDComponent) : CastStateListener {

    private val mListeners = HashSet<CastStateListener>()
    private val mSessionManagerListener: SessionManagerListener<CastSession>

    @Inject
    internal lateinit var mLogger: SDLogger
    @Inject
    internal lateinit var mCastContext: CastContext

    val selectedDeviceFriendlyName: String?
        get() = mCastContext.sessionManager.currentCastSession.castDevice.friendlyName

    val castDevice: CastDevice
        get() = mCastContext.sessionManager.currentCastSession.castDevice

    val currentCastState: Int
        get() = mCastContext.castState

    init {
        component.inject(this)

        mSessionManagerListener = SDSessionManagerListener(mLogger)
    }

    fun addCastStateListener(listener: CastStateListener) {
        mListeners.add(listener)
    }

    fun removeCastStateListener(listener: CastStateListener) {
        mListeners.remove(listener)
    }

    override fun onCastStateChanged(state: Int) {
        mLogger.i(TAG, "onCastStateChanged: $state")
        for (listener in mListeners) {
            listener.onCastStateChanged(state)
        }
    }

    private fun onMessageResult(status: Status) {
        if (!status.isSuccess) {
            mLogger.e(TAG, "Sending message failed")
        }
    }

    fun sendChromecastMessage(message: String) {
        mLogger.i(TAG, "sending message: '" + message + "' state: " + mCastContext.castState)

        mCastContext.sessionManager.currentCastSession?.let {
            it.sendMessage(BuildConfig.CHROMECAST_APP_NAMESPACE, message)
                    .setResultCallback(this::onMessageResult)
        }
    }

    fun onResume() {
        mCastContext.sessionManager.addSessionManagerListener(mSessionManagerListener, CastSession::class.java)
        mCastContext.addCastStateListener(this)
    }

    fun onPause() {
        mCastContext.sessionManager.addSessionManagerListener(mSessionManagerListener, CastSession::class.java)
        mCastContext.removeCastStateListener(this)
    }

    fun endCurrentSession() {
        mCastContext.sessionManager.endCurrentSession(true)
    }

    companion object {
        private val TAG = "SDChromecastManager"
    }
}
