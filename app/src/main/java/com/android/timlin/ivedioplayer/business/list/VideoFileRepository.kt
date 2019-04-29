package com.android.timlin.ivedioplayer.business.list

import android.arch.lifecycle.MutableLiveData
import com.android.timlin.ivedioplayer.business.list.file.FileEntry
import com.android.timlin.ivedioplayer.business.list.video.VideoEntry
import java.io.File

/**
 * Created by linjintian on 2019/2/17.
 */
class VideoFileRepository private constructor(var mVideoFileDetector: VideoFileDetector) {
    var mInitialized: Boolean = false

    fun getFileEntryList(): MutableLiveData<List<FileEntry>> {
        initializeFileEntryData()
        return mVideoFileDetector.mFileEntryListLiveData
    }

    private fun initializeFileEntryData() {
        if (mInitialized) return
        mInitialized = true
        mVideoFileDetector.getRootFileData()
    }

    fun getVideoEntryList(directory: File): MutableLiveData<List<VideoEntry>> {
        mVideoFileDetector.getVideoData(directory)
        return mVideoFileDetector.mVideoEntryListLiveData
    }

    companion object {
        @Volatile
        private var sInstance: VideoFileRepository? = null

        fun getInstance(videoFileDetector: VideoFileDetector): VideoFileRepository? {
            if (sInstance == null) {
                synchronized(VideoFileRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = VideoFileRepository(videoFileDetector)
                    }
                }
            }
            return sInstance
        }
    }
}
