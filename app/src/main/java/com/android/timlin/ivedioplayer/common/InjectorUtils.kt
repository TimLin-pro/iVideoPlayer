package com.android.timlin.ivedioplayer.common

import com.android.timlin.ivedioplayer.business.list.file.FileViewModelFactory
import com.android.timlin.ivedioplayer.business.list.VideoFileDetector
import com.android.timlin.ivedioplayer.business.list.VideoFileRepository
import com.android.timlin.ivedioplayer.business.list.video.VideoListViewModelFactory
import java.io.File

/**
 * Created by linjintian on 2019/2/18.
 */
object InjectorUtils {
    fun provideRepository(): VideoFileRepository? {
        val videoFileDetector = VideoFileDetector
        return VideoFileRepository.getInstance(videoFileDetector)
    }

    fun provideFileViewModelFactory(): FileViewModelFactory {
        val repository = provideRepository()
        return FileViewModelFactory(repository!!)
    }

    fun provideVideoViewModelFactory(directory: File): VideoListViewModelFactory {
        val repository = provideRepository()
        return VideoListViewModelFactory(repository!!, directory)
    }
}