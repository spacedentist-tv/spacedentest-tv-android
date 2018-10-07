package tv.spacedentist.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.CastStateListener
import tv.spacedentist.android.chromecast.SDCastManager
import tv.spacedentist.android.util.SDLogger

/**
 * A manager for a notification supplied to give easy access to getting back to the app, turning it
 * off, and cycling through trufax states.
 *
 * The notification is posted if we leave the app and are connected to a cast device.
 *
 * The notification can be dismissed by the user. Stick notifications are the worst.
 *
 * The notification is cancelled by us if they return to the app or casting stops for any reason.
 *
 */
open class SDNotificationManager(private val context: Context, private val component: SDComponent) : CastStateListener {

    private val castManager: SDCastManager
        get() = component.castManager

    private val logger: SDLogger
        get() = component.logger

    private val mNotificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var activityOpen = false

    init {
        castManager.addCastStateListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW)
            channel.description = context.getString(R.string.notification_channel_description)
            mNotificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.notification_title_text))
                .setColor(ContextCompat.getColor(context, R.color.sd_notification_accent))
                .setContentText(
                        String.format(
                                context.getString(R.string.notification_content_text_format),
                                castManager.selectedDeviceFriendlyName))
                .setSmallIcon(R.drawable.sd_notification_icon)
                .setContentIntent(createOpenIntent())
                .addAction(
                        NotificationCompat.Action.Builder(
                                R.drawable.sd_notification_action_trufax,
                                context.getString(R.string.notification_action_text_trufax),
                                createActionIntent(
                                        R.string.notification_intent_action_trufax,
                                        castManager.castDevice)).build())
                .addAction(
                        NotificationCompat.Action.Builder(
                                R.drawable.sd_notification_action_stop,
                                context.getString(R.string.notification_action_text_stop),
                                createActionIntent(
                                        R.string.notification_intent_action_stop,
                                        castManager.castDevice)).build())
                .setStyle(MediaStyle().setShowActionsInCompactView(0, 1))
                .build()
    }

    private fun createOpenIntent(): PendingIntent {
        val contentIntent = Intent()
        contentIntent.setClass(context, SDMainActivity::class.java)
        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(context, 0, contentIntent, 0)
    }

    private fun createActionIntent(@StringRes actionId: Int, castDevice: CastDevice?): PendingIntent {
        val contentIntent = Intent(context.getString(actionId, BuildConfig.APPLICATION_ID))
        val extras = Bundle()
        castDevice?.putInBundle(extras)
        contentIntent.putExtras(extras)
        contentIntent.setClass(context, SDNotificationReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, contentIntent, 0)
    }

    /**
     * Called by the activity onPause and onResume
     *
     * @param activityOpen
     */
    fun setActivityOpen(activityOpen: Boolean) {
        this.activityOpen = activityOpen
        checkNotification(castManager.currentCastState)
    }

    /**
     * The state of the connection with the chromecast device has changed.
     */
    override fun onCastStateChanged(state: Int) {
        checkNotification(state)
    }

    /**
     * Either show or hide the notification depending on what state we are in.
     */
    private fun checkNotification(state: Int) {
        if (state == CastState.CONNECTED && !activityOpen) {
            logger.i(TAG, "show notification")
            mNotificationManager.notify(NOTIFICATION_ID, createNotification())
        } else {
            logger.i(TAG, "hide notification")
            mNotificationManager.cancel(NOTIFICATION_ID)
        }
    }

    companion object {
        private const val TAG = "SDNotificationManager"
        private const val CHANNEL_ID = "now_casting"
        private const val NOTIFICATION_ID = 1
    }
}
