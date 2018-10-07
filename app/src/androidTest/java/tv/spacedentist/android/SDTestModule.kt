package tv.spacedentist.android

import com.google.android.gms.cast.framework.CastContext
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import tv.spacedentist.android.util.SDLogger
import tv.spacedentist.android.util.SDLoggerAndroid

class SDTestModule : SDModule(null) {
    @Mock private lateinit var mCastContext: CastContext
    @Mock private lateinit var mNotificationManager: SDNotificationManager

    init {
        MockitoAnnotations.initMocks(this)
    }

    override fun provideCastContext(): CastContext {
        return mCastContext
    }

    override fun provideLogger(): SDLogger {
        return mLogger
    }

    override fun provideNotificationManager(): SDNotificationManager {
        return mNotificationManager
    }

    companion object {
        private val mLogger = SDLoggerAndroid()
    }
}
