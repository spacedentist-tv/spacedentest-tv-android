package tv.spacedentist.android

import com.google.android.gms.cast.framework.CastContext
import dagger.Component
import tv.spacedentist.android.chromecast.SDCastManager
import tv.spacedentist.android.util.SDLogger
import javax.inject.Singleton

@Singleton
@Component(modules = [SDModule::class])
interface SDComponent {
    val logger: SDLogger
    val castManager: SDCastManager
    val castContext: CastContext
    val notificationManager: SDNotificationManager

    fun inject(mainActivity: SDMainActivity)
}
