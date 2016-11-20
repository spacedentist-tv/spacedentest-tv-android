package tv.spacedentist.android.util;

public class SDLoggerNull implements SDLogger {
    @Override
    public void i(String tag, String msg) {}

    @Override
    public void d(String tag, String msg) {}

    @Override
    public void e(String tag, String msg) {}

    @Override
    public void e(String tag, String msg, Throwable tr) {}
}
