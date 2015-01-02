package tv.spacedentist.spacedentist_tv_android;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import tv.spacedentist.spacedentist_tv_android.view.SDTextView;

public class SDMainActivity extends ActionBarActivity {

    private static final String TAG = SDMainActivity.class.getSimpleName();

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
    }
}
