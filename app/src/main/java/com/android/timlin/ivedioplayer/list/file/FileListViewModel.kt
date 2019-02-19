package com.android.timlin.ivedioplayer.list.file

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.android.timlin.ivedioplayer.list.VideoFileRepository

/**
 * Created by linjintian on 2019/2/17.
 */
class FileListViewModel(mRepositoryVideo: VideoFileRepository) : ViewModel() {
    var mFileEntryList: LiveData<List<FileEntry>> = mRepositoryVideo.getFileEntryList()

}