package com.android.timlin.ivedioplayer.list.video.loader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.common.BottomSheetDialog;
import com.android.timlin.ivedioplayer.player.activities.VideoActivity;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * Created by linjintian on 2019/4/23.
 */
public class VideoItemListActivity extends FragmentActivity implements VideoItemCollection.VideoItemCallbacks, VideoItemAdapter.OnVideoItemClickListener, BottomSheetDialog.RefreshCallback {
    private static final String TAG = "VideoFolderListActivity";
    public static final String KEY_ID = "id";
    private final VideoItemCollection mVideoItemCollection = new VideoItemCollection();
    private VideoItemAdapter mVideoItemAdapter = new VideoItemAdapter();
    private String mBuckedId;
    private RefreshLayout mRefreshLayout;

    public static void startActivity(Context context, String id) {
        Intent intent = new Intent(context, VideoItemListActivity.class);
        intent.putExtra(KEY_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        mBuckedId = getIntent().getStringExtra(KEY_ID);
        initRv();
        mVideoItemCollection.onCreate(this, this);
        mVideoItemCollection.startLoadVideoData(mBuckedId);
        initRefreshLayout();
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
