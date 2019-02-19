package com.android.timlin.ivedioplayer.list.file

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.android.timlin.ivedioplayer.list.file.data.FileEntry
import com.android.timlin.ivedioplayer.list.file.data.FileRepository

/**
 * Created by linjintian on 2019/2/17.
 */
class FileListViewModel(mRepository: FileRepository) : ViewModel() {
    var mFileEntryList: LiveData<List<FileEntry>> = mRepository.getFileEntry()

}