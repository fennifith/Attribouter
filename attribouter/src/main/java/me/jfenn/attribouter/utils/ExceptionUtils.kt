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

            val limit = t.response()?.raw()?.header("X-RateLimit-Limit") ?: "null"
            val remaining = t.response()?.raw()?.header("X-RateLimit-Remaining") ?: "null"

            Log.e("Attribouter", "Remaining requests: $remaining / $limit")

            Log.e("Attribouter", t.response()?.errorBody()?.string() ?: "no response body")
        } else if (BuildConfig.DEBUG)
            t.printStackTrace()

        null
    }
}