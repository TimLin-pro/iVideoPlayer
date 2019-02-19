package com.android.timlin.ivedioplayer.list.file.data

import java.io.File
import java.net.URLConnection
import java.util.*


/**
 * Created by linjintian on 2019/2/18.
 */
object FileUtils {
    private const val PREFIX_VIDEO = "video/"
    private val VIDEO_EXTENSIONS: Array<String> = arrayOf("test", "3gp", "wmv", "ts", "3gp2", "rmvb", "mp4", "mov", "m4v", "avi", "3gpp", "3gpp2", "mkv", "flv", "divx", "f4v", "rm", "avb", "asf", "ram", "avs", "mpg", "v8", "swf", "m2v", "asx", "ra", "ndivx", "box", "xvid")
    private val mHashVideo: HashSet<String>

    init {
        mHashVideo = HashSet(Arrays.asList(*VIDEO_EXTENSIONS))
    }

    fun isVideoFile(file: File): Boolean {
        val fileSuffix = getFileSuffix(file)
        return mHashVideo.contains(fileSuffix)
//        val lastIndex = fileName.lastIndexOf(".")
//        fileName.substring(lastIndex).contains()
//        val mimeType = getMimeType(fileName)
//        return !TextUtils.isEmpty(fileName) && mimeType.contains(PREFIX_VIDEO)
    }


    /** 获取文件后缀  */
    private fun getFileSuffix(f: File?): String? {
        if (f != null) {
            val filename = f.name
            val i = filename.lastIndexOf('.')
            if (i > 0 && i < filename.length - 1) {
                return filename.substring(i + 1).toLowerCase()
            }
        }
        return null
    }


    private fun getMimeType(fileName: String): String {
        val fileNameMap = URLConnection.getFileNameMap()
        return fileNameMap.getContentTypeFor(fileName)
    }

}
