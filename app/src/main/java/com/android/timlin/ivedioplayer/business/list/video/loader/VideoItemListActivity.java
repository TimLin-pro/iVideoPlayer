package com.android.timlin.ivedioplayer.business.list.video.loader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.business.player.activities.VideoActivity;
import com.android.timlin.ivedioplayer.common.BottomSheetDialog;
import com.android.timlin.ivedioplayer.common.VideoAddressInputHelper;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * Created by linjintian on 2019/4/23.
 */
public class VideoItemListActivity extends AppCompatActivity implements VideoItemCollection.VideoItemCallbacks, VideoItemAdapter.OnVideoItemClickListener, BottomSheetDialog.RefreshCallback {
    private static final String TAG = "VideoFolderListActivity";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";

    private final VideoItemCollection mVideoItemCollection = new VideoItemCollection();
    private VideoItemAdapter mVideoItemAdapter = new VideoItemAdapter();
    private String mBuckedId;
    private RefreshLayout mRefreshLayout;
    private FloatingActionButton mFab;
    private String mFolderName;

    public static void startActivity(Context context, String id, String name) {
        Intent intent = new Intent(context, VideoItemListActivity.class);
        intent.putExtra(KEY_ID, id);
        intent.putExtra(KEY_NAME, name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initParamFromIntent();
        setTitle();
        initRv();
        initVideoItemCollection();
        initRefreshLayout();
        initVideoInputBtn();
    }

    private void initVideoItemCollection() {
        mVideoItemCollection.onCreate(this, this);
        mVideoItemCollection.startLoadVideoData(mBuckedId);
    }

    private void initParamFromIntent() {
        mBuckedId = getIntent().getStringExtra(KEY_ID);
        mFolderName = getIntent().getStringExtra(KEY_NAME);
    }

    private void setTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mFolderName);
        }
    }

    private void initVideoInputBtn() {
        mFab = findViewById(R.id.fab);
        new VideoAddressInputHelper(mFab);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_ID, mBuckedId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBuckedId = savedInstanceState.getString(KEY_ID);

    }

    private void initRefreshLayout() {
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                reloadVideoData();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoItemCollection.onDestroy();
    }

    private void initRv() {
        mVideoItemAdapter.setOnVideoItemClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.mVideoListRecyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mVideoItemAdapter);
    }

    @Override
    public void onVideoItemsLoad(Cursor cursor) {
        mRefreshLayout.finishRefresh(true);
        mVideoItemAdapter.swapCursor(cursor);
    }

    @Override
    public void onVideoItemsReset() {
        mVideoItemAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(VideoItem videoItem) {
        VideoActivity.intentTo(this, videoItem.uri, videoItem.displayName);
    }

    @Override
    public void onMoreClick(VideoItem videoItem) {
        BottomSheetDialog.newInstance(videoItem)
                .show(getSupportFragmentManager(), this);
    }

    @Override
    public void refresh() {
        reloadVideoData();
    }

    private void reloadVideoData() {
        mVideoItemCollection.reloadVideoData(mBuckedId);
    }
}
