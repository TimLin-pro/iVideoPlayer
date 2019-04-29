package com.android.timlin.ivedioplayer.business.list.video

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.android.timlin.ivedioplayer.business.list.VideoFileRepository
import java.io.File

/**
 * Created by linjintian on 2019/2/19.
 */
class VideoListViewModel(mRepositoryVideo: VideoFileRepository, directory: File) : ViewModel() {
    var mVideoEntryListLiveData: LiveData<List<VideoEntry>> = mRepositoryVideo.getVideoEntryList(directory)
}