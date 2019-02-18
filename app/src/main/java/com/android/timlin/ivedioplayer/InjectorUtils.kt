package com.android.timlin.ivedioplayer

import com.android.timlin.ivedioplayer.file.FileViewModelFactory
import com.android.timlin.ivedioplayer.file.data.VideoFileDetector
import com.android.timlin.ivedioplayer.file.data.VideoFileRepository

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


}