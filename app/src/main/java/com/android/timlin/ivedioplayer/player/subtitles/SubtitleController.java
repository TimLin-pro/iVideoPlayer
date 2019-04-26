package com.android.timlin.ivedioplayer.player.subtitles;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.MediaController;
import android.widget.TextView;

import java.util.List;

/**
 * Created by linjintian on 2019/4/26.
 */
public class SubtitleController {
    private static final String TAG = "SubtitleController";
    private static final int SHOW_SUBTITLE_DELAY = 100;
    private static final int INTERVAL_TIME = 300;

    private TextView mTvSubtitleContainer;
    private MediaController.MediaPlayerControl mMediaPlayerControl;
    private List<SubtitlesEntry> mEntryList;
    private Handler mSubtitlesHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_SUBTITLE_DELAY:
                    displaySubtitleInTime();
                    if (mMediaPlayerControl.isPlaying()) {
                        mSubtitlesHandler.sendEmptyMessageDelayed(SHOW_SUBTITLE_DELAY, INTERVAL_TIME);
                    }
                    break;
            }
        }
    };

    public SubtitleController(TextView tvSubtitleContainer, MediaController.MediaPlayerControl mediaPlayerControl) {
        mTvSubtitleContainer = tvSubtitleContainer;
        mMediaPlayerControl = mediaPlayerControl;
    }

    public void startDisplaySubtitle(String srtFilePath) {
        mEntryList = SubtitlesParser.parseSrtFile(srtFilePath);
        displaySubtitleInTime();
        mSubtitlesHandler.sendEmptyMessageDelayed(SHOW_SUBTITLE_DELAY, INTERVAL_TIME);
    }

    private void displaySubtitleInTime() {
        List<SubtitlesEntry> entryList = mEntryList;
        final int currentPosition = mMediaPlayerControl.getCurrentPosition();
        final SubtitlesEntry entry = binarySearchCurrentSubtitle(entryList, currentPosition);
        if (entry != null) {
            mTvSubtitleContainer.setText(String.format("%s\n%s", entry.chinese, entry.english));
        } else {
            mTvSubtitleContainer.setText("");
            Log.d(TAG, "displaySubtitleInTime: entry is null.");
        }
    }

    /**
     * 采用二分法去查找当前应该播放的字幕
     *
     * @param list       全部字幕
     * @param currentPos 当前播放的时间点
     */
    private SubtitlesEntry binarySearchCurrentSubtitle(List<SubtitlesEntry> list, int currentPos) {
        int start = 0;
        int end = list.size() - 1;
        while (start <= end) {
            int middle = (start + end) / 2;
            if (currentPos < list.get(middle).startTime) {
                if (currentPos > list.get(middle).endTime) {
                    return list.get(middle);
                }
                end = middle - 1;
            } else if (currentPos > list.get(middle).endTime) {
                if (currentPos < list.get(middle).startTime) {
                    return list.get(middle);
                }
                start = middle + 1;
            } else if (currentPos >= list.get(middle).startTime && currentPos <= list.get(middle).endTime) {
                return list.get(middle);
            }
        }
        return null;
    }
}