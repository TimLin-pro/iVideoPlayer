package com.android.timlin.ivedioplayer.list

import android.arch.lifecycle.MutableLiveData
import com.android.timlin.ivedioplayer.list.file.FileEntry
import com.android.timlin.ivedioplayer.list.video.VideoEntry
import java.io.File

/**
 * Created by linjintian on 2019/2/17.
 */
class FileRepository private constructor(var mVideoFileDetector: FileDetector) {
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

    fun getVideoEntryList(directory: File): MutableLiveData<List<VideoEntry>> {
        mVideoFileDetector.getVideoData(directory)
        return mVideoFileDetector.mVideoEntryListLiveData
    }

    companion object {
        @Volatile
        private var sInstance: FileRepository? = null

        fun getInstance(videoFileDetector: FileDetector): FileRepository? {
            if (sInstance == null) {
                synchronized(FileRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = FileRepository(videoFileDetector)
                    }
                }
            }
            return sInstance
        }
    }
}
