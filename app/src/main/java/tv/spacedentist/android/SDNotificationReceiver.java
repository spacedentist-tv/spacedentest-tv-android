package tv.spacedentist.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;

import tv.spacedentist.android.chromecast.SDChromecastManager;
import tv.spacedentist.android.util.SDLogger;
import tv.spacedentist.android.view.SDButton;

/**
 * A receiver to handle intent actions from our notifications
 */
public class SDNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "SDNotificationReceiver";

    private SDComponent getComponent(Context context) {
        return ((SDApplication) context.getApplicationContext()).getComponent();
    }

    private SDLogger getLogger(Context context) {
        return getComponent(context).getLogger();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SDChromecastManager chromecastManager = getComponent(context).getChromecastManager();

        if (context.getString(R.string.notification_intent_action_stop, BuildConfig.APPLICATION_ID).equals(intent.getAction())) {
            // We've received the stop intent so disconnect from the chromecast device
            chromecastManager.endCurrentSession();
        } else if (context.getString(R.string.notification_intent_action_trufax, BuildConfig.APPLICATION_ID).equals(intent.getAction())) {
            // We've got the trufax intent so send that button message to the chromecast
            try {
                chromecastManager.sendChromecastMessage(SDButton.BUTTON_TRUFAX.getMessage());
            } catch (JSONException e) {
                getLogger(context).e(TAG, "error sending message to chromecast", e);
            }
        }
    }

}
