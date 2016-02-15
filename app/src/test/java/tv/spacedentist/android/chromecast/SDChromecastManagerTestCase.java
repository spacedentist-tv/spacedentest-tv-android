package tv.spacedentist.android.chromecast;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

import junit.framework.TestCase;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tv.spacedentist.android.util.SDLogger;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SDChromecastManagerTestCase extends TestCase {

    @Mock private GoogleApiClient mApiClient;
    @Mock private Cast.CastApi mCastApi;
    @Mock private SDLogger mLogger;
    @Mock private SDMediaRouter mMediaRouter;
    @Mock private SDMediaRouteSelector mMediaRouteSelector;
    @InjectMocks private SDChromecastManager mChromecastManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        MockitoAnnotations.initMocks(this);
    }

    @SmallTest
    public void testBroadcastConnectionStateChange() {
        SDChromecastManagerListener mockListener = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(mockListener);
        verify(mockListener, times(0)).onConnectionStateChanged();
        mChromecastManager.broadcastConnectionStateChange();
        verify(mockListener, times(1)).onConnectionStateChanged();
        mChromecastManager.broadcastConnectionStateChange();
        verify(mockListener, times(2)).onConnectionStateChanged();
        mChromecastManager.removeListener(mockListener);
        mChromecastManager.broadcastConnectionStateChange();
        verify(mockListener, times(2)).onConnectionStateChanged();
    }

    @SmallTest
    public void testIsConnecting() {
        assertFalse(mChromecastManager.isConnecting());
        mChromecastManager.setApiClient(mApiClient);
        assertFalse(mChromecastManager.isConnecting());
        when(mApiClient.isConnecting()).thenReturn(true);
        assertTrue(mChromecastManager.isConnecting());
        when(mApiClient.isConnecting()).thenReturn(false);
        assertFalse(mChromecastManager.isConnecting());
        mChromecastManager.setApiClient(null);
        assertFalse(mChromecastManager.isConnecting());
    }

    @SmallTest
    public void testIsConnected() {
        assertFalse(mChromecastManager.isConnected());
        mChromecastManager.setApiClient(mApiClient);
        assertFalse(mChromecastManager.isConnected());
        when(mApiClient.isConnected()).thenReturn(true);
        assertTrue(mChromecastManager.isConnected());
        when(mApiClient.isConnected()).thenReturn(false);
        assertFalse(mChromecastManager.isConnected());
        mChromecastManager.setApiClient(null);
        assertFalse(mChromecastManager.isConnected());
    }

    @SmallTest
    public void testLaunch() {
        mChromecastManager.launch();
    }

    @SmallTest
    public void testSendChromecastMessage() {
        mChromecastManager.setApiClient(mApiClient);
        PendingResult<Status> pendingResult = mockPendingResult();
        when(mCastApi.sendMessage(eq(mApiClient), any(String.class), any(String.class))).thenReturn(pendingResult);
        mChromecastManager.sendChromecastMessage("hello");
    }

    @SuppressWarnings("unchecked")
    private <T extends Result> PendingResult<T> mockPendingResult() {
        return mock(PendingResult.class);
    }
}
