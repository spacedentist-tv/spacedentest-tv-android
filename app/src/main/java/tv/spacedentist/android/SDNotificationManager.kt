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
import tv.spacedentist.android.chromecast.SDChromecastManager
import tv.spacedentist.android.util.SDLogger
import javax.inject.Inject

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
open class SDNotificationManager(private val mContext: Context, component: SDComponent) : CastStateListener {
    @Inject
    internal lateinit var mChromecastManager: SDChromecastManager
    @Inject
    internal lateinit var mLogger: SDLogger

    private val mNotificationManager: NotificationManager

    private var mActivityOpen: Boolean = false

    init {

        component.inject(this)

        mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mChromecastManager.addCastStateListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_ID,
                    mContext.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW)
            channel.description = mContext.getString(R.string.notification_channel_description)
            mNotificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(mContext.getString(R.string.notification_title_text))
                .setColor(ContextCompat.getColor(mContext, R.color.sd_notification_accent))
                .setContentText(
                        String.format(
                                mContext.getString(R.string.notification_content_text_format),
                                mChromecastManager.selectedDeviceFriendlyName))
                .setSmallIcon(R.drawable.sd_notification_icon)
                .setContentIntent(createOpenIntent())
                .addAction(
                        NotificationCompat.Action.Builder(
                                R.drawable.sd_notification_action_trufax,
                                mContext.getString(R.string.notification_action_text_trufax),
                                createActionIntent(
                                        R.string.notification_intent_action_trufax,
                                        mChromecastManager.castDevice)).build())
                .addAction(
                        NotificationCompat.Action.Builder(
                                R.drawable.sd_notification_action_stop,
                                mContext.getString(R.string.notification_action_text_stop),
                                createActionIntent(
                                        R.string.notification_intent_action_stop,
                                        mChromecastManager.castDevice)).build())
                .setStyle(MediaStyle().setShowActionsInCompactView(0, 1))
                .build()
    }

    private fun createOpenIntent(): PendingIntent {
        val contentIntent = Intent()
        contentIntent.setClass(mContext, SDMainActivity::class.java)
        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(mContext, 0, contentIntent, 0)
    }

    private fun createActionIntent(@StringRes actionId: Int, castDevice: CastDevice): PendingIntent {
        val contentIntent = Intent(mContext.getString(actionId, BuildConfig.APPLICATION_ID))
        val extras = Bundle()
        castDevice.putInBundle(extras)
        contentIntent.putExtras(extras)
        contentIntent.setClass(mContext, SDNotificationReceiver::class.java)
        return PendingIntent.getBroadcast(mContext, 0, contentIntent, 0)
    }

    /**
     * Called by the activity onPause and onResume
     *
     * @param activityOpen
     */
    fun setActivityOpen(activityOpen: Boolean) {
        mActivityOpen = activityOpen
        checkNotification(mChromecastManager.currentCastState)
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
        if (state == CastState.CONNECTED && !mActivityOpen) {
            mLogger.i(TAG, "show notification")
            mNotificationManager.notify(NOTIFICATION_ID, createNotification())
        } else {
            mLogger.i(TAG, "hide notification")
            mNotificationManager.cancel(NOTIFICATION_ID)
        }
    }

    companion object {
        private val TAG = "SDNotificationManager"

        private val CHANNEL_ID = "now_casting"
        private val NOTIFICATION_ID = 1
    }
}
