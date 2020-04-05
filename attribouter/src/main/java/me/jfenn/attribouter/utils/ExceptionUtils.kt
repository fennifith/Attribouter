package me.jfenn.attribouter.utils

import android.util.Log
import me.jfenn.attribouter.BuildConfig
import retrofit2.HttpException

suspend fun <T> catchNull(body: suspend () -> T): T? {
    return try {
        body()
    } catch (t: Throwable) {
        if (t is HttpException) {
            Log.e("Attribouter", "${t.code()}: ${t.message()} - ${t.response()?.raw()?.request()?.url()?.url()?.toString()}")
        } else if (BuildConfig.DEBUG)
            t.printStackTrace()

        null
    }
}