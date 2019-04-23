package com.android.timlin.ivedioplayer.list.file.loader;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.common.RecyclerViewCursorAdapter;
import com.android.timlin.ivedioplayer.list.video.loader.VideoItemListActivity;

/**
 * Created by linjintian on 2019/4/23.
 */
public class VideoFolderAdapter extends RecyclerViewCursorAdapter<VideoFolderAdapter.MediaViewHolder> {
    private static final String TAG = "VideoFolderAdapter";

    public VideoFolderAdapter() {
        super(null);
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MediaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false));
    }

    @Override
    protected void onBindViewHolder(MediaViewHolder holder, Cursor cursor) {
        holder.bindView(cursor);
    }

    @Override
    protected int getItemViewType(int position, Cursor cursor) {
        return 0;
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTvPath;
        private TextView mTvCount;
        private VideoFolder mVideoFolder;

        public MediaViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
            itemView.setOnClickListener(this);
        }

        void bindView(Cursor cursor) {
            Log.d(TAG, "bindView: called");
            try {
                mVideoFolder = VideoFolder.valueOf(cursor);
                mTvPath.setText(mVideoFolder.getDisplayName(itemView.getContext()));
                mTvCount.setText(String.format("%d%s", mVideoFolder.getCount(), itemView.getContext().getString(R.string.count_videos)));
            } catch (Exception e) {
                Log.e(TAG, "bindView: ", e);
            }
        }

        @Override
        public void onClick(View v) {
            VideoItemListActivity.startActivity(v.getContext(), mVideoFolder.getId());
        }

        private void initView(View parent) {
            mTvPath = parent.findViewById(R.id.tv_folder_name);
            mTvCount = parent.findViewById(R.id.tv_count);
        }
    }
}
