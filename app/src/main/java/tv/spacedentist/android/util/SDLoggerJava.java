package tv.spacedentist.android.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SDLoggerJava implements SDLogger {
    private final Logger mLogger = Logger.getLogger("SDTV");

    @Override
    public void i(String tag, String msg) {
        mLogger.log(Level.INFO, msg);
    }

    @Override
    public void d(String tag, String msg) {
        mLogger.log(Level.FINE, msg);
    }

    @Override
    public void e(String tag, String msg) {
        mLogger.log(Level.SEVERE, msg);
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        mLogger.log(Level.SEVERE, msg, tr);
    }
}
