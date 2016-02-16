package tv.spacedentist.android.chromecast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;

public interface SDApiClientCreator {
    GoogleApiClient get(Cast.CastOptions castOptions,
                        GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                        GoogleApiClient.OnConnectionFailedListener connectionFailedListener);
}
