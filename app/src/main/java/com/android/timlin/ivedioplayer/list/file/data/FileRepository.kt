package com.android.timlin.ivedioplayer.list.file.data

import android.arch.lifecycle.MutableLiveData

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
