package com.drdisagree.interactivegridui

import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        contextReference = WeakReference(
            applicationContext
        )
    }

    companion object {
        private lateinit var instance: MyApplication
        private lateinit var contextReference: WeakReference<Context>

        val appContext: Context
            get() {
                if (!this::contextReference.isInitialized || contextReference.get() == null) {
                    contextReference = WeakReference(
                        getInstance().applicationContext
                    )
                }
                return contextReference.get()!!
            }

        private fun getInstance(): MyApplication {
            if (!this::instance.isInitialized) {
                instance = MyApplication()
            }
            return instance
        }
    }
}