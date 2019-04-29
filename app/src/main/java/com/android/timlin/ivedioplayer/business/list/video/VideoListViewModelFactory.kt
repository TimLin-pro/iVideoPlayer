package com.android.timlin.ivedioplayer.business.list.video

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.timlin.ivedioplayer.business.list.VideoFileRepository
import java.io.File

/**
 * Created by linjintian on 2019/2/19.
 */
class VideoListViewModelFactory(private var mVideoFileRepository: VideoFileRepository, private var mDirectory: File) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return VideoListViewModel(mVideoFileRepository, mDirectory) as T
    }
}