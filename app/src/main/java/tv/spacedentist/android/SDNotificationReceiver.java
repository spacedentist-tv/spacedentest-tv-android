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

    private SDChromecastManager getChromecastManager(Context context) {
        return getComponent(context).getChromecastManager();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context.getString(R.string.notification_intent_action_stop).equals(intent.getAction())) {
            // We've received the stop intent so disconnect from the chromecast device
            getChromecastManager(context).tearDown();
        } else if (context.getString(R.string.notification_intent_action_trufax).equals(intent.getAction())) {
            // We've got the trufax intent so send that button message to the chromecast
            try {
                getChromecastManager(context).sendChromecastMessage(SDButton.BUTTON_TRUFAX.getMessage());
            } catch (JSONException e) {
                getLogger(context).e(TAG, "error sending message to chromecast", e);
            }
        }
    }
}
