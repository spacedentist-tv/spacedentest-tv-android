package tv.spacedentist.android.util

import android.util.Log

/**
 * A wrapper around [Log] that can be mocked and injected in tests.
 */
interface SDLogger {
    fun i(tag: String, msg: String) {}
    fun d(tag: String, msg: String) {}
    fun e(tag: String, msg: String) {}
    fun e(tag: String, msg: String, tr: Throwable) {}
}
