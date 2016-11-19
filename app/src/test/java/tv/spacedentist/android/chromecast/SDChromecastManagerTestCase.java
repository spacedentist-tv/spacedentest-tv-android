package tv.spacedentist.android.chromecast;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tv.spacedentist.android.DaggerSDComponent;
import tv.spacedentist.android.SDComponent;
import tv.spacedentist.android.SDTestModule;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SDChromecastManagerTestCase {

    private Cast.CastApi mCastApi;
    private SDChromecastManager mChromecastManager;

    @Before
    public void setUp() {
        SDComponent component = DaggerSDComponent.builder()
                .sDModule(new SDTestModule())
                .build();

        mChromecastManager = new SDChromecastManager(component);
        mCastApi = component.getCastApi();
    }

    @After
    public void tearDown() {
        mChromecastManager = null;
        mCastApi = null;
    }

    @Test
    public void testAddListener() {
        SDChromecastManagerListener listener = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(listener);
        verify(listener, never()).onConnectionStateChanged();
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener, times(1)).onConnectionStateChanged();
    }

    @Test
    public void testAddSameListenerTwice() {
        SDChromecastManagerListener listener = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(listener);
        mChromecastManager.addListener(listener);
        verify(listener, never()).onConnectionStateChanged();
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener, times(1)).onConnectionStateChanged();
    }

    @Test
    public void testAddTwoListeners() {
        SDChromecastManagerListener listener1 = mock(SDChromecastManagerListener.class);
        SDChromecastManagerListener listener2 = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(listener1);
        mChromecastManager.addListener(listener2);
        verify(listener1, never()).onConnectionStateChanged();
        verify(listener2, never()).onConnectionStateChanged();
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener1, times(1)).onConnectionStateChanged();
        verify(listener2, times(1)).onConnectionStateChanged();
    }

    @Test
    public void testRemoveListener() {
        SDChromecastManagerListener listener = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(listener);
        mChromecastManager.removeListener(listener);
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener, never()).onConnectionStateChanged();
    }

    @Test
    public void testRemoveTwoListeners() {
        SDChromecastManagerListener listener1 = mock(SDChromecastManagerListener.class);
        SDChromecastManagerListener listener2 = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(listener1);
        mChromecastManager.addListener(listener2);
        mChromecastManager.removeListener(listener1);
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener1, never()).onConnectionStateChanged();
        verify(listener2, times(1)).onConnectionStateChanged();
    }

    @Test
    public void testRemoveUnaddedListener() {
        SDChromecastManagerListener listener = mock(SDChromecastManagerListener.class);
        mChromecastManager.removeListener(listener);
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener, never()).onConnectionStateChanged();
    }

    @Test
    public void testIsConnecting() {
        GoogleApiClient apiClient = mock(GoogleApiClient.class);
        assertFalse(mChromecastManager.isConnecting());
        mChromecastManager.setApiClient(apiClient);
        assertFalse(mChromecastManager.isConnecting());
        when(apiClient.isConnecting()).thenReturn(true);
        assertTrue(mChromecastManager.isConnecting());
        when(apiClient.isConnecting()).thenReturn(false);
        assertFalse(mChromecastManager.isConnecting());
        mChromecastManager.setApiClient(null);
        assertFalse(mChromecastManager.isConnecting());
    }

    @Test
    public void testIsConnected() {
        GoogleApiClient apiClient = mock(GoogleApiClient.class);
        assertFalse(mChromecastManager.isConnected());
        mChromecastManager.setApiClient(apiClient);
        assertFalse(mChromecastManager.isConnected());
        when(apiClient.isConnected()).thenReturn(true);
        assertTrue(mChromecastManager.isConnected());
        when(apiClient.isConnected()).thenReturn(false);
        assertFalse(mChromecastManager.isConnected());
        mChromecastManager.setApiClient(null);
        assertFalse(mChromecastManager.isConnected());
    }

    @Test
    public void testLaunch() {
        mChromecastManager.launch();
    }

    @Test
    public void testSendChromecastMessage() {
        GoogleApiClient apiClient = mock(GoogleApiClient.class);
        mChromecastManager.setApiClient(apiClient);
        PendingResult<Status> pendingResult = mockPendingResult();
        when(mCastApi.sendMessage(eq(apiClient), any(String.class), any(String.class))).thenReturn(pendingResult);
        mChromecastManager.sendChromecastMessage("hello");
    }

    @SuppressWarnings("unchecked")
    private <T extends Result> PendingResult<T> mockPendingResult() {
        return mock(PendingResult.class);
    }
}
