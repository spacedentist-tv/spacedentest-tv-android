package tv.spacedentist.android.chromecast

import com.google.android.gms.cast.framework.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import tv.spacedentist.android.DaggerSDComponent
import tv.spacedentist.android.SDTestModule

class SDCastManagerTestCase {

    @Mock
    private lateinit var mMockSessionManager: SessionManager
    @Mock
    private lateinit var mMockCastSession: CastSession
    @Mock
    private lateinit var mMockCastContext: CastContext

    private lateinit var mCastManager: SDCastManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val component = DaggerSDComponent.builder()
                .sDModule(SDTestModule())
                .build()

        mCastManager = SDCastManager(component)
        mMockCastContext = component.castContext

        `when`(mMockCastContext.sessionManager).thenReturn(mMockSessionManager)
        `when`(mMockSessionManager.currentCastSession).thenReturn(mMockCastSession)
    }

    @Test
    fun testAddListener() {
        val listener = mock(CastStateListener::class.java)
        mCastManager.addCastStateListener(listener)
        verify(listener, never()).onCastStateChanged(anyInt())
        mCastManager.onCastStateChanged(CastState.CONNECTED)
        verify(listener, times(1)).onCastStateChanged(CastState.CONNECTED)
    }

    @Test
    fun testAddSameListenerTwice() {
        val listener = mock(CastStateListener::class.java)
        mCastManager.addCastStateListener(listener)
        mCastManager.addCastStateListener(listener)
        verify(listener, never()).onCastStateChanged(anyInt())
        mCastManager.onCastStateChanged(CastState.CONNECTING)
        verify(listener, times(1)).onCastStateChanged(CastState.CONNECTING)
    }

    @Test
    fun testAddTwoListeners() {
        val listener1 = mock(CastStateListener::class.java)
        val listener2 = mock(CastStateListener::class.java)
        mCastManager.addCastStateListener(listener1)
        mCastManager.addCastStateListener(listener2)
        verify(listener1, never()).onCastStateChanged(anyInt())
        verify(listener2, never()).onCastStateChanged(anyInt())
        mCastManager.onCastStateChanged(CastState.CONNECTING)
        verify(listener1, times(1)).onCastStateChanged(CastState.CONNECTING)
        verify(listener2, times(1)).onCastStateChanged(CastState.CONNECTING)
    }

    @Test
    fun testRemoveListener() {
        val listener = mock(CastStateListener::class.java)
        mCastManager.addCastStateListener(listener)
        mCastManager.removeCastStateListener(listener)
        mCastManager.onCastStateChanged(CastState.CONNECTING)
        verify(listener, never()).onCastStateChanged(CastState.CONNECTING)
    }

    @Test
    fun testRemoveTwoListeners() {
        val listener1 = mock(CastStateListener::class.java)
        val listener2 = mock(CastStateListener::class.java)
        mCastManager.addCastStateListener(listener1)
        mCastManager.addCastStateListener(listener2)
        mCastManager.removeCastStateListener(listener1)
        mCastManager.onCastStateChanged(CastState.CONNECTING)
        verify(listener1, never()).onCastStateChanged(CastState.CONNECTING)
        verify(listener2, times(1)).onCastStateChanged(CastState.CONNECTING)
    }

    @Test
    fun testRemoveUnaddedListener() {
        val listener = mock(CastStateListener::class.java)
        mCastManager.removeCastStateListener(listener)
        mCastManager.onCastStateChanged(CastState.CONNECTING)
        verify(listener, never()).onCastStateChanged(CastState.CONNECTING)
    }

    @Test
    fun testIsConnecting() {
        assertFalse(mCastManager.currentCastState === CastState.CONNECTING)
        `when`(mMockCastContext.castState).thenReturn(CastState.CONNECTING)
        assertEquals(mCastManager.currentCastState, CastState.CONNECTING)
        `when`(mMockCastContext.castState).thenReturn(CastState.NO_DEVICES_AVAILABLE)
        assertFalse(mCastManager.currentCastState === CastState.CONNECTING)
    }

    @Test
    fun testIsConnected() {
        assertFalse(mCastManager.currentCastState === CastState.CONNECTED)
        `when`(mMockCastContext.castState).thenReturn(CastState.CONNECTED)
        assertEquals(mCastManager.currentCastState, CastState.CONNECTED)
        `when`(mMockCastContext.castState).thenReturn(CastState.CONNECTING)
        assertFalse(mCastManager.currentCastState === CastState.CONNECTED)
    }
}
