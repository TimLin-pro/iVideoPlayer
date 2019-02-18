package com.android.timlin.ivedioplayer.file

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.timlin.ivedioplayer.R
import com.android.timlin.ivedioplayer.file.data.FileEntry

/**
 * Created by linjintian on 2019/2/17.
 */
class VideoFileListAdapter : RecyclerView.Adapter<VideoFileListAdapter.VideoFileViewHolder>() {
    var mFileEntryList: List<FileEntry> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): VideoFileViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_video_file, parent, false)
        return VideoFileViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mFileEntryList.size
    }

    override fun onBindViewHolder(videoFileViewHolder: VideoFileViewHolder, pos: Int) {
        videoFileViewHolder.bindView(mFileEntryList[pos])
    }

    fun swapFileEntryList(fileEntryList: List<FileEntry>) {
        mFileEntryList = fileEntryList
        notifyDataSetChanged()
    }

    inner class VideoFileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTvPath: TextView = itemView.findViewById(R.id.tvPath)
        var mTvCount: TextView = itemView.findViewById(R.id.tvCount)

        fun bindView(fileEntry: FileEntry) {
            mTvPath.text = fileEntry.path
            mTvCount.text = "${fileEntry.count}个视频文件"
        }
    }
}
