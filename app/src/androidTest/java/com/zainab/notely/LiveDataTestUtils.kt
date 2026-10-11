package com.zainab.notely

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.getOrAwaitValue(timeoutSeconds: Long = 2): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            removeObserver(this)
        }
    }

    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        observeForever(observer)
    }

    if (!latch.await(timeoutSeconds, TimeUnit.SECONDS)) {
        throw RuntimeException("LiveData value was never set")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}