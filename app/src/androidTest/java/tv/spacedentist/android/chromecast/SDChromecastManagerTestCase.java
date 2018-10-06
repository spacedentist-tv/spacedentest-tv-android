package tv.spacedentist.android.chromecast;

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tv.spacedentist.android.DaggerSDComponent;
import tv.spacedentist.android.SDComponent;
import tv.spacedentist.android.SDTestModule;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SDChromecastManagerTestCase {

    @Mock private SessionManager mMockSessionManager;
    @Mock private CastSession mMockCastSession;
    @Mock private CastContext mMockCastContext;

    private SDChromecastManager mChromecastManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        SDComponent component = DaggerSDComponent.builder()
                .sDModule(new SDTestModule())
                .build();

        mChromecastManager = new SDChromecastManager(component);
        mMockCastContext = component.getCastContext();

        when(mMockCastContext.getSessionManager()).thenReturn(mMockSessionManager);
        when(mMockSessionManager.getCurrentCastSession()).thenReturn(mMockCastSession);
    }

    @Test
    public void testAddListener() {
        CastStateListener listener = mock(CastStateListener.class);
        mChromecastManager.addCastStateListener(listener);
        verify(listener, never()).onCastStateChanged(anyInt());
        mChromecastManager.onCastStateChanged(CastState.CONNECTED);
        verify(listener, times(1)).onCastStateChanged(CastState.CONNECTED);
    }

    @Test
    public void testAddSameListenerTwice() {
        CastStateListener listener = mock(CastStateListener.class);
        mChromecastManager.addCastStateListener(listener);
        mChromecastManager.addCastStateListener(listener);
        verify(listener, never()).onCastStateChanged(anyInt());
        mChromecastManager.onCastStateChanged(CastState.CONNECTING);
        verify(listener, times(1)).onCastStateChanged(CastState.CONNECTING);
    }

    @Test
    public void testAddTwoListeners() {
        CastStateListener listener1 = mock(CastStateListener.class);
        CastStateListener listener2 = mock(CastStateListener.class);
        mChromecastManager.addCastStateListener(listener1);
        mChromecastManager.addCastStateListener(listener2);
        verify(listener1, never()).onCastStateChanged(anyInt());
        verify(listener2, never()).onCastStateChanged(anyInt());
        mChromecastManager.onCastStateChanged(CastState.CONNECTING);
        verify(listener1, times(1)).onCastStateChanged(CastState.CONNECTING);
        verify(listener2, times(1)).onCastStateChanged(CastState.CONNECTING);
    }

    @Test
    public void testRemoveListener() {
        CastStateListener listener = mock(CastStateListener.class);
        mChromecastManager.addCastStateListener(listener);
        mChromecastManager.removeCastStateListener(listener);
        mChromecastManager.onCastStateChanged(CastState.CONNECTING);
        verify(listener, never()).onCastStateChanged(CastState.CONNECTING);
    }

    @Test
    public void testRemoveTwoListeners() {
        CastStateListener listener1 = mock(CastStateListener.class);
        CastStateListener listener2 = mock(CastStateListener.class);
        mChromecastManager.addCastStateListener(listener1);
        mChromecastManager.addCastStateListener(listener2);
        mChromecastManager.removeCastStateListener(listener1);
        mChromecastManager.onCastStateChanged(CastState.CONNECTING);
        verify(listener1, never()).onCastStateChanged(CastState.CONNECTING);
        verify(listener2, times(1)).onCastStateChanged(CastState.CONNECTING);
    }

    @Test
    public void testRemoveUnaddedListener() {
        CastStateListener listener = mock(CastStateListener.class);
        mChromecastManager.removeCastStateListener(listener);
        mChromecastManager.onCastStateChanged(CastState.CONNECTING);
        verify(listener, never()).onCastStateChanged(CastState.CONNECTING);
    }

    @Test
    public void testIsConnecting() {
        assertFalse(mChromecastManager.mCastContext.getCastState() == CastState.CONNECTING);
        when(mMockCastContext.getCastState()).thenReturn(CastState.CONNECTING);
        assertEquals(mChromecastManager.mCastContext.getCastState(), CastState.CONNECTING);
        when(mMockCastContext.getCastState()).thenReturn(CastState.NO_DEVICES_AVAILABLE);
        assertFalse(mChromecastManager.mCastContext.getCastState() == CastState.CONNECTING);
    }

    @Test
    public void testIsConnected() {
        assertFalse(mChromecastManager.mCastContext.getCastState() == CastState.CONNECTED);
        when(mMockCastContext.getCastState()).thenReturn(CastState.CONNECTED);
        assertEquals(mChromecastManager.mCastContext.getCastState(), CastState.CONNECTED);
        when(mMockCastContext.getCastState()).thenReturn(CastState.CONNECTING);
        assertFalse(mChromecastManager.mCastContext.getCastState() == CastState.CONNECTED);
    }

    @Test
    public void testSendChromecastMessage() {
        PendingResult<Status> pendingResult = mockPendingResult();
        when(mMockCastSession.sendMessage(any(String.class), any(String.class))).thenReturn(pendingResult);
        mChromecastManager.sendChromecastMessage("hello");
    }

    @SuppressWarnings("unchecked")
    private <T extends Result> PendingResult<T> mockPendingResult() {
        return mock(PendingResult.class);
    }
}
