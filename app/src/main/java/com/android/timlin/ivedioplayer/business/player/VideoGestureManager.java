package com.android.timlin.ivedioplayer.business.player;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.business.player.widget.media.IjkVideoView;
import com.android.timlin.ivedioplayer.common.utils.ScreenUtil;

/**
 * Created by linjintian on 2019/4/27.
 */
public class VideoGestureManager {
    private static final String TAG = "VideoGestureManager";
    private static final int DISMISS_DELAY_TIME_IN_MILL = 500;

    private final Activity mActivity;
    private IjkVideoView mVideoView;
    private AudioManager mAudioManager;
    public GestureDetector mGestureDetector;
    private ProgressBar mPbVolume;
    private ProgressBar mPbBrightness;
    private LinearLayout mLlValueIcContainer;
    private ImageView mIvIcon;
    private TextView mTvValue;

    private WindowManager.LayoutParams mLayoutParams;

    private int mMaxVolume;
    private int mVolumeVal = -1;
    private float mBrightness = -1;
    private float mNewProgress = 0f;
    private float mOldProgress = 0f;
    private MediaController mMediaController;

    private Runnable mDismissVolumeRunnable = new Runnable() {
        @Override
        public void run() {
            mPbVolume.setVisibility(View.INVISIBLE);
        }
    };
    private Runnable mDismissIcValueContainerRunnable = new Runnable() {
        @Override
        public void run() {
            mLlValueIcContainer.setVisibility(View.INVISIBLE);
        }
    };
    private Runnable mDismissBrightnessBarRunnable = new Runnable() {
        @Override
        public void run() {
            mPbBrightness.setVisibility(View.INVISIBLE);
        }
    };

    public VideoGestureManager(final Activity activity, MediaController mediaController) {
        mActivity = activity;
        mMediaController = mediaController;
        initView(activity);
        initAudioManager(activity);

        mGestureDetector = new GestureDetector(activity, new PlayerGestureListener());
        mLayoutParams = activity.getWindow().getAttributes();
    }


    private void initAudioManager(Activity activity) {
        mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void initView(Activity activity) {
        initVideoView(activity);
        mPbBrightness = activity.findViewById(R.id.pb_light_bar);
        mPbVolume = activity.findViewById(R.id.pb_volume_bar);
        mLlValueIcContainer = activity.findViewById(R.id.ll_value_ic_container);
        mIvIcon = activity.findViewById(R.id.iv_icon);
        mTvValue = activity.findViewById(R.id.tv_value);
    }

    private void initVideoView(Activity activity) {
        mVideoView = activity.findViewById(R.id.video_view);
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolumeVal == -1) {
            mVolumeVal = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolumeVal < 0)
                mVolumeVal = 0;
        }
        int currentVolume = (int) (percent * mMaxVolume) + mVolumeVal;
        if (currentVolume > mMaxVolume) {
            currentVolume = mMaxVolume;
        } else if (currentVolume < 0) {
            currentVolume = 0;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        int volumeProgress = (int) (currentVolume * 1.0 / mMaxVolume * 100);
        updateVolumeBar(volumeProgress);
    }

    private void updateVolumeBar(int volume) {
        mPbVolume.setVisibility(View.VISIBLE);
        mPbVolume.removeCallbacks(mDismissVolumeRunnable);
        mPbVolume.setProgress(volume);
        mPbVolume.postDelayed(mDismissVolumeRunnable, DISMISS_DELAY_TIME_IN_MILL);

        mLlValueIcContainer.removeCallbacks(mDismissIcValueContainerRunnable);
        mLlValueIcContainer.postDelayed(mDismissIcValueContainerRunnable, DISMISS_DELAY_TIME_IN_MILL);
        mLlValueIcContainer.setVisibility(View.VISIBLE);
        mIvIcon.setImageResource(R.drawable.ic_volume);
        mTvValue.setText(String.valueOf(volume));
    }

//    /**
//     * 调整播放进度
//     */
//    private void onProgressSlide(float percent) {
//        long position = mVideoView.getCurrentPosition();
//        long duration = mVideoView.getDuration();
//        long deltaMax = Math.min(100 * 1000, duration - position);
//        long delta = (long) (deltaMax * percent);
//
//        mNewProgress = delta + position;
//        if (mNewProgress > duration) {
//            mNewProgress = duration;
//        } else if (mNewProgress <= 0) {
//            mNewProgress = 0;
//            delta = -position;
//        }
//        int showDelta = (int) delta / 1000;
//        if (showDelta != 0) {
//            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
//        }
//        mVideoView.seekTo((int) mNewProgress);
//    }

    private void onProgressSlide(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float offset = e2.getX() - e1.getX();
        final int duration = mVideoView.getDuration();
        final int videoViewWidth = mVideoView.getWidth();
        Log.d(TAG, "onProgressSlide: offset " + offset);
        Log.d(TAG, "onProgressSlide: videoViewWidth()" + videoViewWidth);
        mNewProgress = mOldProgress + offset / videoViewWidth;
        //根据移动的正负决定快进还是快退
        if (offset > 0) {
//            scl.setImageResource(R.drawable.ff);
            mIvIcon.setImageResource(R.drawable.ic_forward);
            if (mNewProgress > 1.f) {
                mNewProgress = 1.f;
            }
        } else {
            mIvIcon.setImageResource(R.drawable.ic_backward);
            if (mNewProgress < 0) {
                mNewProgress = 0;
            }
        }
        showValueIcContainer();
        mTvValue.setText(String.format("%s%%", Math.round(mNewProgress * 100)));
        mMediaController.show();
        mVideoView.seekTo((int) (mNewProgress * duration));
    }

    private void showValueIcContainer() {
        mLlValueIcContainer.removeCallbacks(mDismissIcValueContainerRunnable);
        mLlValueIcContainer.setVisibility(View.VISIBLE);
        mLlValueIcContainer.postDelayed(mDismissIcValueContainerRunnable, DISMISS_DELAY_TIME_IN_MILL);
    }


    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = mLayoutParams.screenBrightness;
            if (mBrightness <= 0.00f) {
                mBrightness = 0.50f;
            } else if (mBrightness < 0.01f) {
                mBrightness = 0.01f;
            }
        }
        Log.d(TAG, "onBrightnessSlide mBrightness=" + mBrightness + ",percent=" + percent);
        float currentVal = calCurrentBrightnessVal(percent);
        applyBrightness(currentVal);
        updateBrightnessBar(currentVal);
    }

    private void applyBrightness(float currentVal) {
        mLayoutParams.screenBrightness = currentVal;
        mActivity.getWindow().setAttributes(mLayoutParams);
    }

    private float calCurrentBrightnessVal(float percent) {
        float currentVal = mBrightness + percent;
        if (currentVal < 0) {
            currentVal = 0;
        } else if (currentVal > 1.0f) {
            currentVal = 1.0f;
        }
        return currentVal;
    }

    private void updateBrightnessBar(float currentVal) {
        mPbBrightness.setVisibility(View.VISIBLE);
        mPbBrightness.removeCallbacks(mDismissBrightnessBarRunnable);
        final int displayVal = (int) (currentVal * 100);
        mPbBrightness.setProgress(displayVal);
        mPbBrightness.postDelayed(mDismissBrightnessBarRunnable, DISMISS_DELAY_TIME_IN_MILL);

        showValueIcContainer();
        mIvIcon.setImageResource(R.drawable.ic_brightness);
        mTvValue.setText(String.valueOf(displayVal));
    }

    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean mFirstTouch;
        private boolean mIsAdjustingProgress;
        private boolean mIsVolumeControl;

        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            mVideoView.toggleAspectRatio();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            mFirstTouch = true;
            //每次按下的时候更新当前亮度和音量，还有进度
            mOldProgress = mNewProgress;
            updateBrightness();
            updateVolume();
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            final float lastX = e1.getX();
            final float lastY = e1.getY();
            final float deltaX = lastX - e2.getX();
            if (mFirstTouch) {
                mFirstTouch = false;
                mIsVolumeControl = lastX > ScreenUtil.INSTANCE.getScreenWidthInPixel() * 0.5f;//右半屏上下滑动调节音量
                mIsAdjustingProgress = Math.abs(distanceX) >= Math.abs(distanceY);
            }

            if (!mIsAdjustingProgress) {
                final float deltaY = lastY - e2.getY();
                final float percent = deltaY / mVideoView.getHeight();
                if (mIsVolumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }
            } else {
//                onProgressSlide(-deltaX / mVideoView.getWidth());
                onProgressSlide(e1, e2, distanceX, distanceY);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        private void updateVolume() {
            mVolumeVal = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }

        private void updateBrightness() {
            mBrightness = mLayoutParams.screenBrightness;
            if (mBrightness == -1) {
                //一开始是默认亮度的时候，获取系统亮度，计算比例值
                mBrightness = getSystemBrightness() / 255f;
            }
        }

        /**
         * 获取系统亮度
         */
        private int getSystemBrightness() {
            return Settings.System.getInt(mActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }
    }

}