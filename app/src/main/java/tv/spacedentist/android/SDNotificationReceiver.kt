package tv.spacedentist.android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.json.JSONException
import tv.spacedentist.android.util.SDLogger
import tv.spacedentist.android.view.SDButton

/**
 * A receiver to handle intent actions from our notifications
 */
class SDNotificationReceiver : BroadcastReceiver() {

    private fun getComponent(context: Context): SDComponent {
        return (context.applicationContext as SDApplication).component
    }

    private fun getLogger(context: Context): SDLogger {
        return getComponent(context).logger
    }

    override fun onReceive(context: Context, intent: Intent) {
        val chromecastManager = getComponent(context).chromecastManager

        if (context.getString(R.string.notification_intent_action_stop, BuildConfig.APPLICATION_ID) == intent.action) {
            // We've received the stop intent so disconnect from the chromecast device
            chromecastManager.endCurrentSession()
        } else if (context.getString(R.string.notification_intent_action_trufax, BuildConfig.APPLICATION_ID) == intent.action) {
            // We've got the trufax intent so send that button message to the chromecast
            try {
                chromecastManager.sendChromecastMessage(SDButton.BUTTON_TRUFAX.message)
            } catch (e: JSONException) {
                getLogger(context).e(TAG, "error sending message to chromecast", e)
            }

        }
    }

    companion object {
        private val TAG = "SDNotificationReceiver"
    }
}
