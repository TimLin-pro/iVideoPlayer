package com.android.timlin.ivedioplayer.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.common.utils.ScreenUtil;
import com.android.timlin.ivedioplayer.player.widget.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by linjintian on 2019/4/27.
 */
public class PlayerManager {
    private static final int DISMISS_DELAY_TIME_IN_MILL = 500;
    /**
     * 可能会剪裁,保持原视频的大小，显示在中心,当原视频的大小超过view的大小超过部分裁剪处理
     */
    public static final String SCALETYPE_FITPARENT = "fitParent";
    /**
     * 可能会剪裁,等比例放大视频，直到填满View为止,超过View的部分作裁剪处理
     */
    public static final String SCALETYPE_FILLPARENT = "fillParent";
    /**
     * 将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中
     */
    public static final String SCALETYPE_WRAPCONTENT = "wrapContent";
    /**
     * 不剪裁,非等比例拉伸画面填满整个View
     */
    public static final String SCALETYPE_FITXY = "fitXY";
    /**
     * 不剪裁,非等比例拉伸画面到16:9,并完全显示在View中
     */
    public static final String SCALETYPE_16_9 = "16:9";
    /**
     * 不剪裁,非等比例拉伸画面到4:3,并完全显示在View中
     */
    public static final String SCALETYPE_4_3 = "4:3";

    /**
     * 状态常量
     */
    private final int STATUS_ERROR = -1;
    private final int STATUS_IDLE = 0;
    private final int STATUS_LOADING = 1;
    private final int STATUS_PLAYING = 2;
    private final int STATUS_PAUSE = 3;
    private final int STATUS_COMPLETED = 4;

    private final Activity mActivity;
    private final IjkVideoView mVideoView;
    private final AudioManager mAudioManager;
    public GestureDetector mGestureDetector;
    private ProgressBar mPbVolume;
    private ProgressBar mPbBrightness;
    private LinearLayout mLlValueIcContainer;
    private ImageView mIvIcon;
    private TextView mTvValue;
    private WindowManager.LayoutParams mLayoutParams;

    private boolean playerSupport;
    private boolean isLive = false;//是否为直播
    private boolean fullScreenOnly;
    private boolean portrait;

    private final int mMaxVolume;
    private int screenWidthPixels;
    private int currentPosition;
    private int status = STATUS_IDLE;
    private long pauseTime;
    private Uri url;

    private float mBrightness = -1;
    private int mVolumeVal = -1;
    private long newPosition = -1;
    private long defaultRetryTime = 5000;

    private OrientationEventListener orientationEventListener;
    private PlayerStateListener playerStateListener;
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

    public void setPlayerStateListener(PlayerStateListener playerStateListener) {
        this.playerStateListener = playerStateListener;
    }

    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public void onError(int what, int extra) {
        }
    };

    private OnCompleteListener onCompleteListener = new OnCompleteListener() {
        @Override
        public void onComplete() {
        }
    };

    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public void onInfo(int what, int extra) {

        }
    };
    private OnControlPanelVisibilityChangeListener onControlPanelVisibilityChangeListener = new OnControlPanelVisibilityChangeListener() {
        @Override
        public void change(boolean isShowing) {
        }
    };

    /**
     * try to play when error(only for live video)
     *
     * @param defaultRetryTime millisecond,0 will stop retry,default is 5000 millisecond
     */
    public void setDefaultRetryTime(long defaultRetryTime) {
        this.defaultRetryTime = defaultRetryTime;
    }

    public PlayerManager(final Activity activity) {
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            playerSupport = true;
        } catch (Throwable e) {
            //Log.e("GiraffePlayer", "loadLibraries error", e);
        }
        this.mActivity = activity;
        screenWidthPixels = ScreenUtil.INSTANCE.getScreenWidthInPixel();

        mVideoView = activity.findViewById(R.id.video_view);
        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                statusChange(STATUS_COMPLETED);
                onCompleteListener.onComplete();
            }
        });
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                statusChange(STATUS_ERROR);
                onErrorListener.onError(what, extra);
                return true;
            }
        });
        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        statusChange(STATUS_LOADING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        statusChange(STATUS_PLAYING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        //显示下载速度
//                      Toast.show("download rate:" + extra);
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        statusChange(STATUS_PLAYING);
                        break;
                }
                onInfoListener.onInfo(what, extra);
                return false;
            }
        });
        mPbBrightness = activity.findViewById(R.id.pb_light_bar);
        mPbVolume = activity.findViewById(R.id.pb_volume_bar);
        mLlValueIcContainer = activity.findViewById(R.id.ll_value_ic_container);
        mIvIcon = activity.findViewById(R.id.iv_icon);
        mTvValue = activity.findViewById(R.id.tv_value);

        mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mGestureDetector = new GestureDetector(activity, new PlayerGestureListener());

        if (fullScreenOnly) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        portrait = getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        if (!playerSupport) {
            //Log.e("播放器不支持此设备");
        }
        mLayoutParams = activity.getWindow().getAttributes();
    }

    private void statusChange(int newStatus) {
        status = newStatus;
        if (!isLive && newStatus == STATUS_COMPLETED) {
            if (playerStateListener != null) {
                playerStateListener.onComplete();
            }
        } else if (newStatus == STATUS_ERROR) {
            if (playerStateListener != null) {
                playerStateListener.onError();
            }
        } else if (newStatus == STATUS_LOADING) {
            if (playerStateListener != null) {
                playerStateListener.onLoading();
            }
        } else if (newStatus == STATUS_PLAYING) {
            if (playerStateListener != null) {
                playerStateListener.onPlay();
            }
        }
    }

    public void onPause() {
        pauseTime = System.currentTimeMillis();
        if (status == STATUS_PLAYING) {
            mVideoView.pause();
            if (!isLive) {
                currentPosition = mVideoView.getCurrentPosition();
            }
        }
    }

    public void onResume() {
        pauseTime = 0;
        if (status == STATUS_PLAYING) {
            if (isLive) {
                mVideoView.seekTo(0);
            } else {
                if (currentPosition > 0) {
                    mVideoView.seekTo(currentPosition);
                }
            }
            mVideoView.start();
        }
    }

    public void onDestroy() {
        orientationEventListener.disable();
        mVideoView.stopPlayback();
    }

    public void play(Uri uri) {
        this.url = uri;
        if (playerSupport) {
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        }
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    private int getScreenOrientation() {
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }
        return orientation;
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
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        // 变更进度条
        int volumeProgress = (int) (currentVolume * 1.0 / mMaxVolume * 100);
        updateVolumeBar(volumeProgress);
        //Log.d("onVolumeSlide:" + s);
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

    private void onProgressSlide(float percent) {
        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);

        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        if (showDelta != 0) {
            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
            //Log.d("onProgressSlide:" + text);
        }
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
        //Log.d("mBrightness:" + mBrightness + ",percent:" + percent);
        WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
        float currentVal = mBrightness + percent;
        lpa.screenBrightness = currentVal;
        if (currentVal < 0) {
            currentVal = 0;
        } else if (currentVal >= 1.0f) {
            currentVal = 1.0f;
        }
        updateBrightnessBar(currentVal);
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.0f) {
            lpa.screenBrightness = 0.01f;
        }
        mActivity.getWindow().setAttributes(lpa);
    }

    private void updateBrightnessBar(float currentVal) {
        mPbBrightness.setVisibility(View.VISIBLE);
        mPbBrightness.removeCallbacks(mDismissBrightnessBarRunnable);
        final int displayVal = (int) (currentVal * 100);
        mPbBrightness.setProgress(displayVal);
        mPbBrightness.postDelayed(mDismissBrightnessBarRunnable, DISMISS_DELAY_TIME_IN_MILL);

        mLlValueIcContainer.removeCallbacks(mDismissIcValueContainerRunnable);
        mLlValueIcContainer.setVisibility(View.VISIBLE);
        mLlValueIcContainer.postDelayed(mDismissIcValueContainerRunnable, DISMISS_DELAY_TIME_IN_MILL);
        mIvIcon.setImageResource(R.drawable.ic_brightness);
        mTvValue.setText(String.valueOf(displayVal));
    }

    public void setFullScreenOnly(boolean fullScreenOnly) {
        this.fullScreenOnly = fullScreenOnly;
        tryFullScreen(fullScreenOnly);
        if (fullScreenOnly) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    private void tryFullScreen(boolean fullScreen) {
        if (mActivity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) mActivity).getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
        }
        setFullScreen(fullScreen);
    }

    private void setFullScreen(boolean fullScreen) {
        if (mActivity != null) {
            WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
    }

    /**
     * <pre>
     *     fitParent:可能会剪裁,保持原视频的大小，显示在中心,当原视频的大小超过view的大小超过部分裁剪处理
     *     fillParent:可能会剪裁,等比例放大视频，直到填满View为止,超过View的部分作裁剪处理
     *     wrapContent:将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中
     *     fitXY:不剪裁,非等比例拉伸画面填满整个View
     *     16:9:不剪裁,非等比例拉伸画面到16:9,并完全显示在View中
     *     4:3:不剪裁,非等比例拉伸画面到4:3,并完全显示在View中
     * </pre>
     *
     * @param scaleType
     */
//    public void setScaleType(String scaleType) {
//        if (SCALETYPE_FITPARENT.equals(scaleType)) {
//            mVideoView.setAspectRatio(IRenderView.AR_ASPECT_FIT_PARENT);
//        } else if (SCALETYPE_FILLPARENT.equals(scaleType)) {
//            mVideoView.setAspectRatio(IRenderView.AR_ASPECT_FILL_PARENT);
//        } else if (SCALETYPE_WRAPCONTENT.equals(scaleType)) {
//            mVideoView.setAspectRatio(IRenderView.AR_ASPECT_WRAP_CONTENT);
//        } else if (SCALETYPE_FITXY.equals(scaleType)) {
//            mVideoView.setAspectRatio(IRenderView.AR_MATCH_PARENT);
//        } else if (SCALETYPE_16_9.equals(scaleType)) {
//            mVideoView.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
//        } else if (SCALETYPE_4_3.equals(scaleType)) {
//            mVideoView.setAspectRatio(IRenderView.AR_4_3_FIT_PARENT);
//        }
//    }
    public void start() {
        mVideoView.start();
    }

    public void pause() {
        mVideoView.pause();
    }

    public boolean onBackPressed() {
        if (!fullScreenOnly && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    class Query {
        private final Activity activity;
        private View view;

        public Query(Activity activity) {
            this.activity = activity;
        }

        public Query id(int id) {
            view = activity.findViewById(id);
            return this;
        }

        public Query image(int resId) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(resId);
            }
            return this;
        }

        public Query visible() {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            return this;
        }

        public Query gone() {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            return this;
        }

        public Query invisible() {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
            return this;
        }

        public Query clicked(View.OnClickListener handler) {
            if (view != null) {
                view.setOnClickListener(handler);
            }
            return this;
        }

        public Query text(CharSequence text) {
            if (view != null && view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public Query visibility(int visible) {
            if (view != null) {
                view.setVisibility(visible);
            }
            return this;
        }

        private void size(boolean width, int n, boolean dip) {
            if (view != null) {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (n > 0 && dip) {
                    n = dip2pixel(activity, n);
                }
                if (width) {
                    lp.width = n;
                } else {
                    lp.height = n;
                }
                view.setLayoutParams(lp);
            }
        }

        public void height(int height, boolean dip) {
            size(false, height, dip);
        }

        public int dip2pixel(Context context, float n) {
            int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
            return value;
        }

        public float pixel2dip(Context context, float n) {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float dp = n / (metrics.densityDpi / 160f);
            return dp;
        }
    }

    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mVideoView.toggleAspectRatio();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            //每次按下的时候更新当前亮度和音量，还有进度
            updateBrightness();
            updateVolume();
            return super.onDown(e);
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

        /*
         * 获取系统亮度
         */
        int getSystemBrightness() {
            return Settings.System.getInt(mActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = mOldX > screenWidthPixels * 0.5f;
                firstTouch = false;
            }

            if (toSeek) {
                if (!isLive) {
                    onProgressSlide(-deltaX / mVideoView.getWidth());
                }
            } else {
                float percent = deltaY / mVideoView.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    }

    /**
     * is player support this device
     *
     * @return
     */
    public boolean isPlayerSupport() {
        return playerSupport;
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return mVideoView != null ? mVideoView.isPlaying() : false;
    }

    public void stop() {
        mVideoView.stopPlayback();
    }

    public int getCurrentPosition() {
        return mVideoView.getCurrentPosition();
    }

    /**
     * get video duration
     *
     * @return
     */
    public int getDuration() {
        return mVideoView.getDuration();
    }

    public PlayerManager playInFullScreen(boolean fullScreen) {
        if (fullScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        return this;
    }

    public PlayerManager onError(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
        return this;
    }

    public PlayerManager onComplete(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }

    public PlayerManager onInfo(OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
        return this;
    }

    public PlayerManager onControlPanelVisibilityChange(OnControlPanelVisibilityChangeListener listener) {
        this.onControlPanelVisibilityChangeListener = listener;
        return this;
    }

    /**
     * set is live (can't seek forward)
     *
     * @param isLive
     * @return
     */
    public PlayerManager live(boolean isLive) {
        this.isLive = isLive;
        return this;
    }

    public PlayerManager toggleAspectRatio() {
        if (mVideoView != null) {
            mVideoView.toggleAspectRatio();
        }
        return this;
    }

    public interface PlayerStateListener {
        void onComplete();

        void onError();

        void onLoading();

        void onPlay();
    }

    public interface OnErrorListener {
        void onError(int what, int extra);
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public interface OnControlPanelVisibilityChangeListener {
        void change(boolean isShowing);
    }

    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }
}