package tv.spacedentist.android

import android.app.Application

/**
 * We keep the global state alive here (Chromecast client connection) so that it doesn't get
 * destroyed and that we don't have to deal with lifecycle events in the activity.
 */
class SDApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        component = DaggerSDComponent.builder()
                .sDModule(SDModule(this))
                .build()
    }

    companion object {
        lateinit var component: SDComponent
            private set
    }
}
