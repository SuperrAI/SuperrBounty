package com.superr.bounty

import android.content.Context
import android.os.Build
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.onyx.android.sdk.rx.RxManager
import com.superr.bounty.data.AppDatabase
import org.lsposed.hiddenapibypass.HiddenApiBypass

private const val TAG = "Superr.MainApplication"

class MainApplication : MultiDexApplication() {
    val database by lazy { AppDatabase.getDatabase(this) }

    companion object {
        private var sInstance: MainApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        initConfig()
        RxManager.Builder.initAppContext(this)
        checkHiddenApiBypass()
    }

    override fun onTerminate() {
        super.onTerminate()
        sInstance = null
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this@MainApplication)
    }

    private fun initConfig() {
        try {
            sInstance = this
        } catch (e: Exception) {
        }
    }

    private fun checkHiddenApiBypass() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            HiddenApiBypass.addHiddenApiExemptions("")
        }
    }

}