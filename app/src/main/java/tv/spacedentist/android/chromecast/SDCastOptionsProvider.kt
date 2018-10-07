package tv.spacedentist.android.chromecast

import android.content.Context
import androidx.annotation.Keep
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.framework.media.MediaIntentReceiver
import com.google.android.gms.cast.framework.media.NotificationOptions
import tv.spacedentist.android.BuildConfig
import tv.spacedentist.android.SDMainActivity

@Keep
class SDCastOptionsProvider : OptionsProvider {

    override fun getCastOptions(context: Context): CastOptions {
        val notificationOptions = NotificationOptions.Builder()
                .setActions(
                        listOf(
                                MediaIntentReceiver.ACTION_TOGGLE_PLAYBACK,
                                MediaIntentReceiver.ACTION_STOP_CASTING),
                        intArrayOf(0, 1))
                .setTargetActivityClassName(SDMainActivity::class.java.name)
                .build()

        val mediaOptions = CastMediaOptions.Builder()
                .setNotificationOptions(notificationOptions)
                .build()

        return CastOptions.Builder()
                .setReceiverApplicationId(BuildConfig.CHROMECAST_APP_ID)
                .setCastMediaOptions(mediaOptions)
                .build()
    }

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? {
        return null
    }
}
