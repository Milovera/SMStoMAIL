package com.example.smstomail.data.repository

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic

fun mockAndroidLog() {
    mockkStatic(Log::class)

    every { Log.d(any(), any()) } returns 0
    every { Log.e(any(), any()) } returns 0
    every { Log.v(any(), any()) } returns 0
    every { Log.i(any(), any()) } returns 0
}

fun unmockAndroidLog() {
    unmockkStatic(Log::class)
}