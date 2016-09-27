package tv.spacedentist.android.util;

import android.util.Log;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A wrapper around {@link Log} that can be mocked and injected in tests.
 */
public interface SDLogger {
    void i(String tag, String msg);
    void d(String tag, String msg);
    void e(String tag, String msg);
    void e(String tag, String msg, Throwable tr);

    SDLogger JAVA_LOGGER = new SDLogger() {

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
    };
}
