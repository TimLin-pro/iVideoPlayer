package com.android.timlin.ivedioplayer.list.video.loader;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.common.RecyclerViewCursorAdapter;
import com.android.timlin.ivedioplayer.player.activities.VideoActivity;

import java.text.SimpleDateFormat;

/**
 * Created by linjintian on 2019/4/23.
 */
public class VideoItemAdapter extends RecyclerViewCursorAdapter<VideoItemAdapter.MediaViewHolder> {
    private static final String TAG = "VideoFolderAdapter";

    public VideoItemAdapter() {
        super(null);
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MediaViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false));
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

        private VideoItem mVideoItem;
        private ImageView mIvVideoPreview;
        private TextView mTvTime;
        private TextView mTvName;
        private ImageView mIvMore;
        private static final String FORMAT = "HH:mm:ss";
        private SimpleDateFormat mFormatter = new SimpleDateFormat(FORMAT);
        public MediaViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            initView(itemView);
        }

        void bindView(Cursor cursor) {
            Log.d(TAG, "bindView: called");
            try {
                mVideoItem = VideoItem.valueOf(cursor);
//                mIvVideoPreview.setImageResource(mVideoItem.uri);
                mTvName.setText(mVideoItem.displayName);
                //todo 耗时操作，移到子线程
                mTvTime.setText(mFormatter.format(mVideoItem.duration));
            } catch (Exception e) {
                Log.e(TAG, "bindView: ", e);
            }
        }

        //getPath 存在问题
        @Override
        public void onClick(View v) {
            VideoActivity.intentTo(v.getContext(), mVideoItem.uri.getPath(), "title");
        }

        private void initView(View parent) {
            mIvVideoPreview = parent.findViewById(R.id.mIvVideoPreview);
            mTvTime = parent.findViewById(R.id.mTvTime);
            mTvName = parent.findViewById(R.id.mTvName);
            mIvMore = parent.findViewById(R.id.mIvMore);
        }
    }
}
