package com.android.timlin.ivedioplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.timlin.ivedioplayer.file.VideoListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final VideoListFragment videoListFragment = new VideoListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_main, videoListFragment)
                .commit();
    }
}
