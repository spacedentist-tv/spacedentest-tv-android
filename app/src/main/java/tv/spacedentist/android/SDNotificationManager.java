package tv.spacedentist.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v7.app.NotificationCompat;

import javax.inject.Inject;

import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.chromecast.SDChromecastManagerListener;
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
public class SDNotificationManager implements SDChromecastManagerListener {

    private static final String TAG = "SDNotificationManager";

    private static final int NOTIFICATION_ID = 1;

    private final Context mContext;
    @Inject SDChromecastManager mChromecastManager;
    @Inject SDLogger mLogger;

    private final NotificationManager mNotificationManager;
    private final Action mStopAction;
    private final Action mTextAction;

    private boolean mActivityOpen;

    public SDNotificationManager(Context context, SDComponent component) {
        mContext = context;

        component.inject(this);

        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        mChromecastManager.addListener(this);

        mStopAction = new Action.Builder(
                R.drawable.cast_ic_notification_on,
                context.getString(R.string.notification_action_text_stop),
                createActionIntent(R.string.notification_intent_action_stop))
                .build();

        mTextAction = new Action.Builder(
                R.drawable.sd_text,
                context.getString(R.string.notification_action_text_trufax),
                createActionIntent(R.string.notification_intent_action_trufax))
                .build();
    };

    private Notification createNotification() {
        return new NotificationCompat.Builder(mContext)
                .setContentTitle(mContext.getString(R.string.notification_title_text))
                .setContentText(
                        String.format(
                                mContext.getString(R.string.notification_content_text_format),
                                mChromecastManager.getSelectedDeviceFriendlyName()))
                .setSmallIcon(R.drawable.ic_small_icon)
                .setContentIntent(createOpenIntent())
                .addAction(mTextAction)
                .addAction(mStopAction)
                .setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1))
                .build();
    }

    private PendingIntent createOpenIntent() {
        final Intent contentIntent = new Intent();
        contentIntent.setClass(mContext, SDMainActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(mContext, 0, contentIntent, 0);
    }

    private PendingIntent createActionIntent(@StringRes int actionId) {
        final Intent contentIntent = new Intent(mContext.getString(actionId));
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
        checkNotification();
    }

    /**
     * The state of the connection with the chromecast device has changed.
     */
    @Override
    public void onConnectionStateChanged() {
        checkNotification();
    }

    /**
     * Either show or hide the notification depending on what state we are in.
     */
    private void checkNotification() {
        if (mChromecastManager.isConnected() && !mActivityOpen) {
            mLogger.i(TAG, "show notification");
            mNotificationManager.notify(NOTIFICATION_ID, createNotification());
        } else {
            mLogger.i(TAG, "hide notification");
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }
}
