package com.android.timlin.ivedioplayer;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.timlin.ivedioplayer.list.file.FileListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FileListFragment fileListFragment = new FileListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_main, fileListFragment)
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
