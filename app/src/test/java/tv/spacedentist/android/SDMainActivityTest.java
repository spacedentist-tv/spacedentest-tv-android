package tv.spacedentist.android;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class SDMainActivityTest {

    private ActivityController<SDMainActivity> mActivityController;

    @Before
    public void setup() {
        mActivityController = Robolectric.buildActivity(SDMainActivity.class).create();
    }

    @Test
    public void testGetActivity() throws Exception {
        mActivityController.get();
    }
}
