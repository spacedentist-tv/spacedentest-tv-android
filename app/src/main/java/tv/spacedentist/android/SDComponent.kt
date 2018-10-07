package tv.spacedentist.android

import com.google.android.gms.cast.framework.CastContext
import dagger.Component
import tv.spacedentist.android.chromecast.SDChromecastManager
import tv.spacedentist.android.util.SDLogger
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(SDModule::class))
interface SDComponent {
    val logger: SDLogger
    val chromecastManager: SDChromecastManager
    val castContext: CastContext

    fun inject(mainActivity: SDMainActivity)
    fun inject(chromecastManager: SDChromecastManager)
    fun inject(notificationManager: SDNotificationManager)
}
