package tv.spacedentist.android

import com.google.android.gms.cast.framework.CastContext
import dagger.Module
import dagger.Provides
import tv.spacedentist.android.chromecast.SDChromecastManager
import tv.spacedentist.android.util.SDLogger
import tv.spacedentist.android.util.SDLoggerAndroid
import javax.inject.Singleton

@Module
open class SDModule(private val mApplication: SDApplication?) {

    @Provides
    @Singleton
    internal open fun provideLogger(): SDLogger {
        return if (BuildConfig.DEBUG) SDLoggerAndroid() else object: SDLogger {}
    }

    @Provides
    internal open fun provideCastContext(): CastContext {
        return CastContext.getSharedInstance(mApplication!!)
    }

    @Provides
    @Singleton
    internal fun provideChromecastManager(): SDChromecastManager {
        return SDChromecastManager(mApplication!!.component)
    }

    @Provides
    @Singleton
    internal open fun provideNotificationManager(): SDNotificationManager {
        return SDNotificationManager(mApplication!!, mApplication.component)
    }
}
