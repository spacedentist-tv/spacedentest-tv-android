package tv.spacedentist.android.chromecast;

import com.google.android.gms.cast.Cast;

public class SDCastListener extends Cast.Listener {

    public interface Callback {
        void onApplicationStatusChanged();
        void onVolumeChanged();
        void onApplicationDisconnected(int errorCode);
    }

    private final Callback mCallback;

    public SDCastListener(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onApplicationStatusChanged() {
        mCallback.onApplicationStatusChanged();;
    }

    @Override
    public void onVolumeChanged() {
        mCallback.onVolumeChanged();
    }

    @Override
    public void onApplicationDisconnected(int errorCode) {
        mCallback.onApplicationDisconnected(errorCode);
    }
}
