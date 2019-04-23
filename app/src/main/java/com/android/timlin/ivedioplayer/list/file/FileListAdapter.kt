package com.android.timlin.ivedioplayer.list.file

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.timlin.ivedioplayer.R

/**
 * Created by linjintian on 2019/2/17.
 */
class FileListAdapter : RecyclerView.Adapter<FileListAdapter.VideoFileViewHolder>() {
    var mFileEntryList: List<FileEntry> = ArrayList()
    var mItemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): VideoFileViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
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
        var mTvPath: TextView = itemView.findViewById(R.id.tv_folder_name)
        var mTvCount: TextView = itemView.findViewById(R.id.tv_count)

        init {
            itemView.setOnClickListener { mItemClickListener?.onItemClick(adapterPosition, mFileEntryList[adapterPosition]) }
        }

        fun bindView(fileEntry: FileEntry) {
            mTvPath.text = fileEntry.path
            mTvCount.text = "${fileEntry.count}个视频文件"
        }
    }

    interface ItemClickListener {
        fun onItemClick(pos: Int, fileEntry: FileEntry)
    }
}
