package com.android.timlin.ivedioplayer.file.data

import android.arch.lifecycle.MutableLiveData

/**
 * Created by linjintian on 2019/2/17.
 */
class VideoFileRepository private constructor(var mVideoFileDetector: VideoFileDetector) {
    var mInitialized: Boolean = false

    fun getFileEntry(): MutableLiveData<List<FileEntry>> {
        initializeData()
        return mVideoFileDetector.mFileEntryListLiveData
    }

    private fun initializeData() {
        if (mInitialized) return
        mInitialized = true
        mVideoFileDetector.getRootFileData()
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
