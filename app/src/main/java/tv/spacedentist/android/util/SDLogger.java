package tv.spacedentist.android.util;

import android.util.Log;

/**
 * A wrapper around {@link Log} that can be mocked and injected in tests.
 */
public interface SDLogger {
    void i(String tag, String msg);
    void d(String tag, String msg);
    void e(String tag, String msg);
    void e(String tag, String msg, Throwable tr);
}
