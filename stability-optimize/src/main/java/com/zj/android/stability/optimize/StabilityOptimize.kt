package com.zj.android.stability.optimize

import android.os.Looper
import android.util.Log

object StabilityOptimize {
    fun setUpJavaExceptionHandler() {
        val preDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            handleException(preDefaultExceptionHandler, thread, exception)
            if (thread == Looper.getMainLooper().thread) {
                while (true) {
                    try {
                        Looper.loop()
                    } catch (e: Throwable) {
                        handleException(preDefaultExceptionHandler, Thread.currentThread(), e)
                    }
                }
            }
        }
    }

    fun setUpNativeAirBag(signal: Int, soName: String, backtrace: String) {
        Log.i("StabilityOptimize", "Native 安全气囊已开启")
        StabilityNativeLib().openNativeAirBag(signal, soName, backtrace)
    }

    private fun handleException(
        preDefaultExceptionHandler: Thread.UncaughtExceptionHandler,
        thread: Thread,
        exception: Throwable
    ) {
        Log.w("StabilityOptimize", "FATAL EXCEPTION: ${thread.name}")
        Log.w("StabilityOptimize", exception.message ?: "")
    }
}