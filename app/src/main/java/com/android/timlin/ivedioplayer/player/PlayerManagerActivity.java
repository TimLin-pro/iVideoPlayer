//package com.android.timlin.ivedioplayer.player;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.MotionEvent;
//
//import com.android.timlin.ivedioplayer.R;
//
///**
// * Created by linjintian on 2019/4/27.
// */
//public class PlayerManagerActivity extends AppCompatActivity implements VideoGestureManager.PlayerStateListener {
//    private String url1 = "rtmp://203.207.99.19:1935/live/CCTV5";
//    private VideoGestureManager mPlayerManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        initPlayer();
//    }
//
//    private void initPlayer() {
//        mPlayerManager = new VideoGestureManager(this);
//        mPlayerManager.setFullScreenOnly(true);
////        mPlayerManager.setScaleType(VideoGestureManager.SCALETYPE_FILLPARENT);
//        mPlayerManager.playInFullScreen(true);
//        mPlayerManager.setPlayerStateListener(this);
//        mPlayerManager.play(url1);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (mPlayerManager.mGestureDetector.onTouchEvent(event))
//            return true;
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    public void onComplete() {
//    }
//
//    @Override
//    public void onError() {
//    }
//
//    @Override
//    public void onLoading() {
//    }
//
//    @Override
//    public void onPlay() {
//    }
//}
//
//class MainActivity extends AppCompatActivity implements VideoGestureManager.PlayerStateListener {
//    private String url1 = "rtmp://203.207.99.19:1935/live/CCTV5";
//    private String url2 = "http://zv.3gv.ifeng.com/live/zhongwen800k.m3u8";
//    private String url3 = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
//    private String url4 = "http://42.96.249.166/live/24035.m3u8";
//    private VideoGestureManager player;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        initPlayer();
//    }
//
//    private void initPlayer() {
//        player = new VideoGestureManager(this);
//        player.setFullScreenOnly(true);
////        mPlayerManager.setScaleType(VideoGestureManager.SCALETYPE_FILLPARENT);
//        player.playInFullScreen(true);
//        player.setPlayerStateListener(this);
//        player.play(url1);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (player.mGestureDetector.onTouchEvent(event))
//            return true;
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    public void onComplete() {
//    }
//
//    @Override
//    public void onError() {
//    }
//
//    @Override
//    public void onLoading() {
//    }
//
//    @Override
//    public void onPlay() {
//    }
//}
