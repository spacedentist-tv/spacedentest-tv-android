package tv.spacedentist.android.chromecast;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import tv.spacedentist.android.BuildConfig;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
public class SDChromecastManagerTest {

    private SDChromecastManager mChromecastManager;

    @Before
    public void setup() {
        mChromecastManager = new SDChromecastManager(RuntimeEnvironment.application);
    }

    @Test
    public void testBroadcast() throws Exception {
        SDChromecastManagerListener listener = new SDChromecastManagerListener() {
            @Override
            public void onConnectionStateChanged() {

            }
        };

        mChromecastManager.addListener(listener);

        // make something happen to do a broadcast

        mChromecastManager.removeListener(listener);
    }
}
