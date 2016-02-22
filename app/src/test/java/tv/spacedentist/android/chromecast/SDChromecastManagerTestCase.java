package tv.spacedentist.android.chromecast;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

import junit.framework.TestCase;

import javax.inject.Inject;

import dagger.MembersInjector;
import dagger.ObjectGraph;
import tv.spacedentist.android.SDTestModule;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SDChromecastManagerTestCase extends TestCase implements MembersInjector {

    @Inject Cast.CastApi mCastApi;
    private ObjectGraph mObjectGraph;
    private SDChromecastManager mChromecastManager;

    @Override
    public void injectMembers(Object instance) {
        mObjectGraph.inject(instance);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mObjectGraph = ObjectGraph.create(SDTestModule.class);
        this.injectMembers(this);
        mChromecastManager = new SDChromecastManager(this);
    }

    @SmallTest
    public void testAddListener() {
        SDChromecastManagerListener listener = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(listener);
        verify(listener, times(0)).onConnectionStateChanged();
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener, times(1)).onConnectionStateChanged();
    }

    @SmallTest
    public void testAddSameListenerTwice() {
        SDChromecastManagerListener listener = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(listener);
        mChromecastManager.addListener(listener);
        verify(listener, times(0)).onConnectionStateChanged();
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener, times(1)).onConnectionStateChanged();
    }

    @SmallTest
    public void testAddTwoListeners() {
        SDChromecastManagerListener listener1 = mock(SDChromecastManagerListener.class);
        SDChromecastManagerListener listener2 = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(listener1);
        mChromecastManager.addListener(listener2);
        verify(listener1, times(0)).onConnectionStateChanged();
        verify(listener2, times(0)).onConnectionStateChanged();
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener1, times(1)).onConnectionStateChanged();
        verify(listener2, times(1)).onConnectionStateChanged();
    }

    @SmallTest
    public void testRemoveListener() {
        SDChromecastManagerListener listener = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(listener);
        mChromecastManager.removeListener(listener);
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener, times(0)).onConnectionStateChanged();
    }

    @SmallTest
    public void testRemoveTwoListeners() {
        SDChromecastManagerListener listener1 = mock(SDChromecastManagerListener.class);
        SDChromecastManagerListener listener2 = mock(SDChromecastManagerListener.class);
        mChromecastManager.addListener(listener1);
        mChromecastManager.addListener(listener2);
        mChromecastManager.removeListener(listener1);
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener1, times(0)).onConnectionStateChanged();
        verify(listener2, times(1)).onConnectionStateChanged();
    }

    @SmallTest
    public void testRemoveUnaddedListener() {
        SDChromecastManagerListener listener = mock(SDChromecastManagerListener.class);
        mChromecastManager.removeListener(listener);
        mChromecastManager.broadcastConnectionStateChange();
        verify(listener, times(0)).onConnectionStateChanged();
    }

    @SmallTest
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

    @SmallTest
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

    @SmallTest
    public void testLaunch() {
        mChromecastManager.launch();
    }

    @SmallTest
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
