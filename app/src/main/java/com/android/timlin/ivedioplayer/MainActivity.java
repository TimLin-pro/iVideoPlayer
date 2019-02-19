package com.android.timlin.ivedioplayer;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.timlin.ivedioplayer.list.file.VideoListFragment;

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

        new AlertDialog.Builder(this)
                .setPositiveButton("aaa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }
}
