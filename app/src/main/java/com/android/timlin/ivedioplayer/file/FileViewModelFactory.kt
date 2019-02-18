package com.android.timlin.ivedioplayer.file

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.timlin.ivedioplayer.file.data.VideoFileRepository

/**
 * Created by linjintian on 2019/2/18.
 */
class FileViewModelFactory(private var mVideoFileRepository: VideoFileRepository) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return  FileListViewModel(mVideoFileRepository) as T
    }
}