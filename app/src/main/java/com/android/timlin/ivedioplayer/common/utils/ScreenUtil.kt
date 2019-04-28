package com.android.timlin.ivedioplayer.common.utils

import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.view.Surface
import android.view.WindowManager

/**
 * Created by linjintian on 2019/4/22.
 */
object ScreenUtil {

    val screenHeightInPixel: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    val screenWidthInPixel: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    fun dip2px(dp: Int): Int {
        return dip2px(java.lang.Float.valueOf(dp.toFloat()))
    }

    fun dip2px(dp: Float): Int {
        return Math.round(dp * Resources.getSystem().displayMetrics.density + 0.5f)
    }

    fun px2dip(px: Int): Int {
        return Math.round(px / Resources.getSystem().displayMetrics.density + 0.5f)
    }

    fun sp2px(sp: Int): Int {
        return Math.round(sp * Resources.getSystem().displayMetrics.scaledDensity + 0.5f)
    }

    fun getScreenOrientation(windowManager: WindowManager): Int {
        val rotation = windowManager.defaultDisplay.rotation
        val dm = Resources.getSystem().displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        val orientation: Int
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && width > height) {
            orientation = when (rotation) {
                Surface.ROTATION_0 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                Surface.ROTATION_90 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                Surface.ROTATION_180 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                Surface.ROTATION_270 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        } else {
            orientation = when (rotation) {
                Surface.ROTATION_0 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                Surface.ROTATION_90 -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                Surface.ROTATION_180 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                Surface.ROTATION_270 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                else -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }// if the device's natural orientation is landscape or if the device
        // is square:
        return orientation
    }

}
