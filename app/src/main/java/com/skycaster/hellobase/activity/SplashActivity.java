package com.skycaster.hellobase.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.skycaster.hellobase.R;
import com.skycaster.hellobase.customize.TwinklingTextView;

public class SplashActivity extends AppCompatActivity {
    private TwinklingTextView tv_welcome;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_welcome = (TwinklingTextView) findViewById(R.id.activity_splash_twnktv_welcome);
        tv_version= (TextView) findViewById(R.id.activity_splash_tv_version);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            String versionName = info.versionName;
            if(!TextUtils.isEmpty(versionName)){
                tv_version.setText("Ver "+versionName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            tv_version.setText("Ver Unknown");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_version.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.start(SplashActivity.this);
            }
        },2000);
        tv_version.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },3000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tv_welcome.startTwinkling();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tv_welcome.stopTwinkling();
    }
}
