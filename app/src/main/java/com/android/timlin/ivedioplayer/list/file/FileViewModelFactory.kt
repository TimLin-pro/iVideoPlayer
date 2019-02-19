package com.android.timlin.ivedioplayer.list.file

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.timlin.ivedioplayer.list.FileRepository

/**
 * Created by linjintian on 2019/2/18.
 */
class FileViewModelFactory(private var mFileRepository: FileRepository) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return  FileListViewModel(mFileRepository) as T
    }
}