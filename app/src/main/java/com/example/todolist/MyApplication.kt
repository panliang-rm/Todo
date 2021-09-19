package com.example.todolist

import android.app.Application
import cn.leancloud.LCObject
import cn.leancloud.LeanCloud


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        LeanCloud.initialize(
            this,
            "rG0YdI2JrEpnUpkJcIDbcIOS-gzGzoHsz",
            "fwY97QeFHH3qugMiyt1toRSN",
            "https://rg0ydi2j.lc-cn-n1-shared.com"
        )

        val testObject = LCObject("TestObject")
        testObject.put("words", "Hello world!")
        testObject.saveInBackground().blockingSubscribe()
    }
}
