/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.timlin.ivedioplayer.business.player.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.business.player.VideoGestureManager;
import com.android.timlin.ivedioplayer.business.player.application.Settings;
import com.android.timlin.ivedioplayer.business.player.content.RecentMediaStorage;
import com.android.timlin.ivedioplayer.business.player.fragments.TracksFragment;
import com.android.timlin.ivedioplayer.business.player.subtitles.SubtitleController;
import com.android.timlin.ivedioplayer.business.player.widget.media.IjkMediaController;
import com.android.timlin.ivedioplayer.business.player.widget.media.IjkVideoView;
import com.android.timlin.ivedioplayer.business.player.widget.media.MeasureHelper;
import com.android.timlin.ivedioplayer.business.player.widget.media.PlayerSpeedController;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class VideoActivity extends AppCompatActivity implements TracksFragment.ITrackHolder {
    private static final String TAG = "VideoActivity";
    public static final String KEY_VIDEO_URI = "videoUri";
    public static final String KEY_VIDEO_TITLE = "videoTitle";
    public static final String KEY_VIDEO_PATH = "videoPath";

    private String mVideoPath;
    private Uri mVideoUri;

    private IjkMediaController mMediaController;
    private IjkVideoView mVideoView;
    private TextView mToastTextView;
    private TableLayout mHudView;
    private DrawerLayout mDrawerLayout;
    private ViewGroup mRightDrawer;

    private Settings mSettings;
    private boolean mBackPressed;
    private PlayerSpeedController mPlayerSpeedController;
    private TextView mTvSrt;
    private VideoGestureManager mVideoGestureManager;

    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(KEY_VIDEO_PATH, videoPath);
        intent.putExtra(KEY_VIDEO_TITLE, videoTitle);
        return intent;
    }

    public static void intentTo(Context context, String videoPath, String videoTitle) {
        context.startActivity(newIntent(context, videoPath, videoTitle));
    }

    public static void intentTo(Context context, Uri videoUri, String videoTitle) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(KEY_VIDEO_URI, videoUri);
        intent.putExtra(KEY_VIDEO_TITLE, videoTitle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mSettings = new Settings(this);

        // handle arguments
        initParamsFromIntent();

        // init UI
        ActionBar actionBar = initActionBar();
        initMediaController(actionBar);

        initView();

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        if (initVideoView()) return;
        initVideoGestureManager();
        initPlayerSpeedController();
        initSubtitleController();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mVideoView.resume();
    }

    private void initView() {
        mToastTextView = findViewById(R.id.toast_text_view);
        mHudView = findViewById(R.id.hud_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mRightDrawer = findViewById(R.id.right_drawer);
        mTvSrt = findViewById(R.id.tv_srt);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mVideoView = findViewById(R.id.video_view);
    }

    private boolean initVideoView() {
        mVideoView.setMediaController(mMediaController);
        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        if (!TextUtils.isEmpty(mVideoPath))
            mVideoView.setVideoPath(mVideoPath);
        else if (mVideoUri != null) {
            mVideoView.setVideoURI(mVideoUri);
        } else {
            Log.e(TAG, "Null Data Source\n");
            finish();
            return true;
        }
        mVideoView.start();
        return false;
    }

    private void initPlayerSpeedController() {
        mPlayerSpeedController = new PlayerSpeedController(mVideoView, getWindow().getDecorView());
    }

    private void initMediaController(ActionBar actionBar) {
        mMediaController = new IjkMediaController(this, true);
        mMediaController.setSupportActionBar(actionBar);
    }

    private ActionBar initActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        return getSupportActionBar();
    }

    private void initParamsFromIntent() {
        Intent intent = getIntent();
        mVideoPath = intent.getStringExtra(KEY_VIDEO_PATH);
        mVideoUri = intent.getParcelableExtra(KEY_VIDEO_URI);

        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction)) {
            if (intentAction.equals(Intent.ACTION_VIEW)) {
                mVideoPath = intent.getDataString();
            } else if (intentAction.equals(Intent.ACTION_SEND)) {
                mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            }
        }

        if (!TextUtils.isEmpty(mVideoPath)) {
            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
        }
    }

    private void initSubtitleController() {
        SubtitleController subtitleController = new SubtitleController(mTvSrt, mVideoView);
        // TODO: 2019/4/26 read file path, avoid hard code
        subtitleController.startDisplaySubtitle(Environment.getExternalStorageDirectory().getAbsolutePath() + "/f2.srt");
    }

    private void initVideoGestureManager() {
        mVideoGestureManager = new VideoGestureManager(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mVideoGestureManager.mGestureDetector.onTouchEvent(ev))
            return true;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;

        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_toggle_ratio:
                int aspectRatio = mVideoView.toggleAspectRatio();
                String aspectRatioText = MeasureHelper.getAspectRatioText(this, aspectRatio);
                mToastTextView.setText(aspectRatioText);
                mMediaController.showOnce(mToastTextView);
                return true;
            case R.id.action_toggle_player:
                int player = mVideoView.togglePlayer();
                String playerText = IjkVideoView.getPlayerText(this, player);
                mToastTextView.setText(playerText);
                mMediaController.showOnce(mToastTextView);
                return true;
            case R.id.action_toggle_render:
                int render = mVideoView.toggleRender();
                String renderText = IjkVideoView.getRenderText(this, render);
                mToastTextView.setText(renderText);
                mMediaController.showOnce(mToastTextView);
                return true;
            case R.id.action_show_info:
                mVideoView.showMediaInfo();
                break;
            case R.id.action_show_tracks:
                if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
                    if (f != null) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.remove(f);
                        transaction.commit();
                    }
                    mDrawerLayout.closeDrawer(mRightDrawer);
                } else {
                    Fragment f = TracksFragment.newInstance();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.right_drawer, f);
                    transaction.commit();
                    mDrawerLayout.openDrawer(mRightDrawer);
                }
                break;
            case R.id.action_speed:
                mPlayerSpeedController.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        if (mVideoView == null)
            return null;

        return mVideoView.getTrackInfo();
    }

    @Override
    public void selectTrack(int stream) {
        mVideoView.selectTrack(stream);
    }

    @Override
    public void deselectTrack(int stream) {
        mVideoView.deselectTrack(stream);
    }

    @Override
    public int getSelectedTrack(int trackType) {
        if (mVideoView == null)
            return -1;

        return mVideoView.getSelectedTrack(trackType);
    }
}
