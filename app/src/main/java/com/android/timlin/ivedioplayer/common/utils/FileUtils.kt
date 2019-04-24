package com.android.timlin.ivedioplayer.common.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import android.util.Log
import java.io.File
import java.net.URLConnection
import java.util.*

private const val TAG = "FileUtils"

/**
 * Created by linjintian on 2019/2/18.
 */
object FileUtils {
    private const val PREFIX_VIDEO = "video/"
    private const val KB = 1024.0
    private const val MB = KB * KB
    private const val GB = KB * KB * KB
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

    fun formatFileSize(size: Long): String {
        return when {
            size < KB -> size.toString() + "B"
            size < MB -> String.format("%.1f", size / KB) + "KB"
            size < GB -> String.format("%.1f", size / MB) + "MB"
            else -> String.format("%.1f", size / GB) + "GB"
        }
    }

    fun getPathFromUri(context: Context, uri: Uri): String {
        var path: String?
        if (Build.VERSION.SDK_INT < 11)
            path = getRealPathFromURI_BelowAPI11(context, uri)
        else if (Build.VERSION.SDK_INT < 19)
            path = getRealPathFromURI_API11to18(context, uri)
        else
            path = getRealPathFromURI_API19(context, uri)// SDK > 19 (Android 4.4)
        Log.d(TAG, "File Path: $path")
        return path!!
    }

    fun getRealPathFromURI_API19(context: Context, uri: Uri): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"

        val cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null)

        val columnIndex = cursor.getColumnIndex(column[0])

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
        return filePath
    }


    @SuppressLint("NewApi")
    fun getRealPathFromURI_API11to18(context: Context, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        var result: String? = null

        val cursorLoader = CursorLoader(
                context,
                contentUri, proj, null, null, null)
        val cursor = cursorLoader.loadInBackground()
        if (cursor != null) {
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            result = cursor!!.getString(column_index)
        }
        return result
    }

    fun getRealPathFromURI_BelowAPI11(context: Context, contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.getContentResolver().query(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme = uri.scheme
        var realFilePath: String? = null
        if (scheme == null) {
            realFilePath = uri.path
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            realFilePath = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        realFilePath = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return realFilePath
    }
}
