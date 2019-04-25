package com.android.timlin.ivedioplayer.list.file.loader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.timlin.ivedioplayer.R;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.Arrays;
import java.util.List;

/**
 * Created by linjintian on 2019/4/23.
 */
public class VideoFolderListActivity extends FragmentActivity implements VideoFolderCollection.AlbumCallbacks {
    private static final String TAG = "VideoFolderListActivity";
    private VideoFolderAdapter mVideoFolderAdapter = new VideoFolderAdapter();
    private VideoFolderCollection mVideoFolderCollection = new VideoFolderCollection();
    private View mEmptyView;
    private RefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initView();
        mVideoFolderCollection.onCreate(this, this);
        resolveStoragePermission();
    }

    private void initView() {
        initRv();
        mEmptyView = findViewById(R.id.empty_view);
        initRefreshLayout();
    }

    private void resolveStoragePermission() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Log.d(TAG, "onGranted  onAction: " + Arrays.toString(data.toArray()));
                        mVideoFolderCollection.startLoadVideoFolders();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(VideoFolderListActivity.this, R.string.need_storage_permission, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }

    private void initRv() {
        RecyclerView recyclerView = findViewById(R.id.mVideoListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mVideoFolderAdapter);
    }

    private void initRefreshLayout() {
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                Log.d(TAG, "onRefresh: ");
                mVideoFolderCollection.reloadVideoFolders();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoFolderCollection.onDestroy();
    }

    @Override
    public void onAlbumLoad(Cursor cursor) {
        Log.d(TAG, "onAlbumLoad: ");
        toggleEmptyView(cursor);
        mRefreshLayout.finishRefresh();
        mVideoFolderAdapter.swapCursor(cursor);
    }

    private void toggleEmptyView(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            final int count = cursor.getCount();
            if (count == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAlbumReset() {
        mVideoFolderAdapter.swapCursor(null);
    }
}
