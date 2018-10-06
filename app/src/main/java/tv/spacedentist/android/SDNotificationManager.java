package tv.spacedentist.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;

import javax.inject.Inject;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.util.SDLogger;

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
public class SDNotificationManager implements CastStateListener {

    private static final String TAG = "SDNotificationManager";

    private static final String CHANNEL_ID = "now_casting";
    private static final int NOTIFICATION_ID = 1;

    private final Context mContext;
    @Inject SDChromecastManager mChromecastManager;
    @Inject SDLogger mLogger;

    private final NotificationManager mNotificationManager;

    private boolean mActivityOpen;

    public SDNotificationManager(Context context, SDComponent component) {
        mContext = context;

        component.inject(this);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mChromecastManager.addCastStateListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            mContext.getString(R.string.notification_channel_name),
                            NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(mContext.getString(R.string.notification_channel_description));
            mNotificationManager.createNotificationChannel(channel);
        }
    };

    private Notification createNotification() {
        return new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(mContext.getString(R.string.notification_title_text))
                .setColor(ContextCompat.getColor(mContext, R.color.sd_notification_accent))
                .setContentText(
                        String.format(
                                mContext.getString(R.string.notification_content_text_format),
                                mChromecastManager.getSelectedDeviceFriendlyName()))
                .setSmallIcon(R.drawable.sd_notification_icon)
                .setContentIntent(createOpenIntent())
                .addAction(
                        new NotificationCompat.Action.Builder(
                                R.drawable.sd_notification_action_trufax,
                                mContext.getString(R.string.notification_action_text_trufax),
                                createActionIntent(
                                        R.string.notification_intent_action_trufax,
                                        mChromecastManager.getCastDevice())).build())
                .addAction(
                        new NotificationCompat.Action.Builder(
                                R.drawable.sd_notification_action_stop,
                                mContext.getString(R.string.notification_action_text_stop),
                                createActionIntent(
                                        R.string.notification_intent_action_stop,
                                        mChromecastManager.getCastDevice())).build())
                .setStyle(new MediaStyle().setShowActionsInCompactView(0, 1))
                .build();
    }

    private PendingIntent createOpenIntent() {
        final Intent contentIntent = new Intent();
        contentIntent.setClass(mContext, SDMainActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(mContext, 0, contentIntent, 0);
    }

    private PendingIntent createActionIntent(@StringRes int actionId, CastDevice castDevice) {
        final Intent contentIntent = new Intent(mContext.getString(actionId, BuildConfig.APPLICATION_ID));
        final Bundle extras = new Bundle();
        castDevice.putInBundle(extras);
        contentIntent.putExtras(extras);
        contentIntent.setClass(mContext, SDNotificationReceiver.class);
        return PendingIntent.getBroadcast(mContext, 0, contentIntent, 0);
    }

    /**
     * Called by the activity onPause and onResume
     *
     * @param activityOpen
     */
    public void setActivityOpen(boolean activityOpen) {
        mActivityOpen = activityOpen;
        checkNotification(mChromecastManager.getCurrentCastState());
    }

    /**
     * The state of the connection with the chromecast device has changed.
     */
    @Override
    public void onCastStateChanged(int state) {
        checkNotification(state);
    }

    /**
     * Either show or hide the notification depending on what state we are in.
     */
    private void checkNotification(int state) {
        if (state == CastState.CONNECTED && !mActivityOpen) {
            mLogger.i(TAG, "show notification");
            mNotificationManager.notify(NOTIFICATION_ID, createNotification());
        } else {
            mLogger.i(TAG, "hide notification");
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
