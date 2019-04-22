package com.android.timlin.ivedioplayer.common.utils

import android.content.res.Resources

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
}
