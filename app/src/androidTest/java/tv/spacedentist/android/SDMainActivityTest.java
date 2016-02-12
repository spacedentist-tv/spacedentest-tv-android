package tv.spacedentist.android;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityTestCase;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class SDMainActivityTest extends ActivityInstrumentationTestCase2<SDMainActivity> {
    public SDMainActivityTest() {
        super(SDMainActivity.class);
    }

    @SmallTest
    public void test() {
        SDMainActivity mainActivity = getActivity();
    }
}