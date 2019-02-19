package com.android.timlin.ivedioplayer.list.video

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.android.timlin.ivedioplayer.InjectorUtils
import com.android.timlin.ivedioplayer.R
import kotlinx.android.synthetic.main.activity_video_list.*
import java.io.File

class VideoListActivity : AppCompatActivity() {
    private lateinit var mPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)
        mPath = intent.getStringExtra(Companion.KEY_PATH)

        mVideoListRecyclerView.layoutManager = GridLayoutManager(this, 2)
        val videoListAdapter = VideoListAdapter()
        videoListAdapter.mOnItemClickListener = object : VideoListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, videoEntry: VideoEntry) {
                TODO("播放视频")
            }
        }
        mVideoListRecyclerView.adapter = videoListAdapter

        val factory = InjectorUtils.provideVideoViewModelFactory(File(mPath))
        val viewModel = ViewModelProviders.of(this, factory).get(VideoListViewModel::class.java)
        viewModel.mVideoEntryListLiveData.observe(this, Observer {
            if (it != null) {
                videoListAdapter.swapList(it)
            }
        })
    }

    companion object {
        const val KEY_PATH = "path"
        fun startActivity(context: Context, path: String) {
            val intent = Intent(context, VideoListActivity::class.java)
            intent.putExtra(KEY_PATH, path)
            context.startActivity(intent)
        }
    }
}
