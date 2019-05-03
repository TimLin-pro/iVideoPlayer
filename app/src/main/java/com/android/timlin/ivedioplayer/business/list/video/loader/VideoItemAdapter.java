package com.android.timlin.ivedioplayer.business.list.video.loader;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.common.GlideApp;
import com.android.timlin.ivedioplayer.common.RecyclerViewCursorAdapter;
import com.android.timlin.ivedioplayer.common.utils.ScreenUtil;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by linjintian on 2019/4/23.
 */
public class VideoItemAdapter extends RecyclerViewCursorAdapter<VideoItemAdapter.MediaViewHolder> {
    private static final String TAG = "VideoFolderAdapter";
    private OnVideoItemClickListener mOnVideoItemClickListener;

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

    public void setOnVideoItemClickListener(OnVideoItemClickListener onVideoItemClickListener) {
        mOnVideoItemClickListener = onVideoItemClickListener;
    }

    class MediaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private VideoItem mVideoItem;
        private ImageView mIvVideoPreview;
        private TextView mTvTime;
        private TextView mTvName;
        private ImageView mIvMore;
        private int mImageResize;

        public MediaViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            initView(itemView);
            initMoreViewClickListener();
        }

        private void initMoreViewClickListener() {
            mIvMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnVideoItemClickListener != null) {
                        mOnVideoItemClickListener.onMoreClick(mVideoItem);
                    }
                }
            });
        }

        void bindView(Cursor cursor) {
            Log.d(TAG, "bindView: called");
            try {
                mVideoItem = VideoItem.valueOf(cursor);
                bindThumbnail();
                mTvName.setText(mVideoItem.displayName);
                mTvTime.setText(DateUtils.formatElapsedTime(mVideoItem.duration / 1000));
            } catch (Exception e) {
                Log.e(TAG, "bindView: ", e);
            }
        }

        private void bindThumbnail() {
            GlideApp.with(mIvVideoPreview)
                    .asBitmap()
                    .load(mVideoItem.uri)
                    .apply(new RequestOptions()
                            .transform(new CenterCrop(), new RoundedCorners(ScreenUtil.INSTANCE.dip2px(6)))
                            .override(mIvVideoPreview.getWidth(), mIvVideoPreview.getHeight())
                            .placeholder(R.drawable.ic_placeholder))
                    .into(mIvVideoPreview);
        }

//        private int getImageResize(Context context) {
//            if (mImageResize == 0) {
//                RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
//                int spanCount = ((GridLayoutManager) lm).getSpanCount();
//                int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
//                int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(
//                        R.dimen.media_grid_spacing) * (spanCount - 1);
//                mImageResize = availableWidth / spanCount;
//                mImageResize = (int) (mImageResize * mSelectionSpec.thumbnailScale);
//            }
//            return mImageResize;
//        }

        @Override
        public void onClick(View v) {
            if (mOnVideoItemClickListener != null) {
                mOnVideoItemClickListener.onItemClick(mVideoItem);
            }
        }

        private void initView(View parent) {
            mIvVideoPreview = parent.findViewById(R.id.mIvVideoPreview);
            mTvTime = parent.findViewById(R.id.mTvTime);
            mTvName = parent.findViewById(R.id.mTvName);
            mIvMore = parent.findViewById(R.id.mIvMore);
        }
    }

    public interface OnVideoItemClickListener {
        void onItemClick(VideoItem videoItem);

        void onMoreClick(VideoItem videoItem);
    }
}
