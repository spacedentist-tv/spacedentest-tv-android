package tv.spacedentist.spacedentist_tv_android;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class SDMainActivity extends ActionBarActivity {

    //private static final String TAG = SDMainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View titleView = inflater.inflate(R.layout.action_bar_title, null);
        actionBar.setCustomView(titleView);

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.argb(128, 0, 0, 0)));
    }
}
