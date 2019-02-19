package com.android.timlin.ivedioplayer.list.file

import java.io.Serializable

/**
 * SD卡上的视频信息存放类
 * Created by linjintian on 2019/2/17.
 */
class FileEntry : Serializable {
    /**
     * 文件夹路径
     */
    var path: String
    /**
     * 文件夹里有多少个视频文件
     */
    var count: Int = 0
    var name: String

    constructor(path: String, name: String, count: Int) {
        this.path = path
        this.name = name
        this.count = count
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}