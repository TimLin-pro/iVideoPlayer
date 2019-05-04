package com.android.timlin.ivedioplayer.app

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/**
 * Created by linjintian on 2019/5/4.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        installLeakCanary()
    }

    private fun installLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this);
    }
}