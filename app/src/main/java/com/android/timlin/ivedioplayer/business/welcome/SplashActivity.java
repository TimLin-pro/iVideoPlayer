package com.android.timlin.ivedioplayer.business.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.business.list.file.loader.VideoFolderListActivity;

/**
 * Created by linjintian on 2019/4/25.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, VideoFolderListActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}
