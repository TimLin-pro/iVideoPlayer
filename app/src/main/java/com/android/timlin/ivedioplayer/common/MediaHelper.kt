package com.android.timlin.ivedioplayer.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import java.io.File

/**
 * Created by linjintian on 2019/5/9.
 */
object MediaHelper{
    fun addMedia(c: Context, f: File) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(f)
        c.sendBroadcast(intent)
    }

    fun removeMedia(c: Context, f: File) {
        val resolver = c.contentResolver
        resolver.delete(MediaStore.Files.getContentUri("external"), MediaStore.Video.Media.DATA + "=?", arrayOf(f.absolutePath))
    }

}