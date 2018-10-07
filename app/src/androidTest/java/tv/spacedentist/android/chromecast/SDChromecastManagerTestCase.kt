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

class SDChromecastManagerTestCase {

    @Mock
    private lateinit var mMockSessionManager: SessionManager
    @Mock
    private lateinit var mMockCastSession: CastSession
    @Mock
    private lateinit var mMockCastContext: CastContext

    private lateinit var mChromecastManager: SDChromecastManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val component = DaggerSDComponent.builder()
                .sDModule(SDTestModule())
                .build()

        mChromecastManager = SDChromecastManager(component)
        mMockCastContext = component.castContext

        `when`(mMockCastContext.sessionManager).thenReturn(mMockSessionManager)
        `when`(mMockSessionManager.currentCastSession).thenReturn(mMockCastSession)
    }

    @Test
    fun testAddListener() {
        val listener = mock(CastStateListener::class.java)
        mChromecastManager.addCastStateListener(listener)
        verify(listener, never()).onCastStateChanged(anyInt())
        mChromecastManager.onCastStateChanged(CastState.CONNECTED)
        verify(listener, times(1)).onCastStateChanged(CastState.CONNECTED)
    }

    @Test
    fun testAddSameListenerTwice() {
        val listener = mock(CastStateListener::class.java)
        mChromecastManager.addCastStateListener(listener)
        mChromecastManager.addCastStateListener(listener)
        verify(listener, never()).onCastStateChanged(anyInt())
        mChromecastManager.onCastStateChanged(CastState.CONNECTING)
        verify(listener, times(1)).onCastStateChanged(CastState.CONNECTING)
    }

    @Test
    fun testAddTwoListeners() {
        val listener1 = mock(CastStateListener::class.java)
        val listener2 = mock(CastStateListener::class.java)
        mChromecastManager.addCastStateListener(listener1)
        mChromecastManager.addCastStateListener(listener2)
        verify(listener1, never()).onCastStateChanged(anyInt())
        verify(listener2, never()).onCastStateChanged(anyInt())
        mChromecastManager.onCastStateChanged(CastState.CONNECTING)
        verify(listener1, times(1)).onCastStateChanged(CastState.CONNECTING)
        verify(listener2, times(1)).onCastStateChanged(CastState.CONNECTING)
    }

    @Test
    fun testRemoveListener() {
        val listener = mock(CastStateListener::class.java)
        mChromecastManager.addCastStateListener(listener)
        mChromecastManager.removeCastStateListener(listener)
        mChromecastManager.onCastStateChanged(CastState.CONNECTING)
        verify(listener, never()).onCastStateChanged(CastState.CONNECTING)
    }

    @Test
    fun testRemoveTwoListeners() {
        val listener1 = mock(CastStateListener::class.java)
        val listener2 = mock(CastStateListener::class.java)
        mChromecastManager.addCastStateListener(listener1)
        mChromecastManager.addCastStateListener(listener2)
        mChromecastManager.removeCastStateListener(listener1)
        mChromecastManager.onCastStateChanged(CastState.CONNECTING)
        verify(listener1, never()).onCastStateChanged(CastState.CONNECTING)
        verify(listener2, times(1)).onCastStateChanged(CastState.CONNECTING)
    }

    @Test
    fun testRemoveUnaddedListener() {
        val listener = mock(CastStateListener::class.java)
        mChromecastManager.removeCastStateListener(listener)
        mChromecastManager.onCastStateChanged(CastState.CONNECTING)
        verify(listener, never()).onCastStateChanged(CastState.CONNECTING)
    }

    @Test
    fun testIsConnecting() {
        assertFalse(mChromecastManager.currentCastState === CastState.CONNECTING)
        `when`(mMockCastContext.castState).thenReturn(CastState.CONNECTING)
        assertEquals(mChromecastManager.currentCastState, CastState.CONNECTING)
        `when`(mMockCastContext.castState).thenReturn(CastState.NO_DEVICES_AVAILABLE)
        assertFalse(mChromecastManager.currentCastState === CastState.CONNECTING)
    }

    @Test
    fun testIsConnected() {
        assertFalse(mChromecastManager.currentCastState === CastState.CONNECTED)
        `when`(mMockCastContext.castState).thenReturn(CastState.CONNECTED)
        assertEquals(mChromecastManager.currentCastState, CastState.CONNECTED)
        `when`(mMockCastContext.castState).thenReturn(CastState.CONNECTING)
        assertFalse(mChromecastManager.currentCastState === CastState.CONNECTED)
    }
}
