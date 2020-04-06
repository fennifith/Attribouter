package me.jfenn.attribouter.provider.net

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

interface ServiceBuilder<T: RequestProvider> {

    val key : String
    var cache : Cache?

    fun withCache(cacheDir: File, size: Long = 10 * 1024 * 1024) {
        cache = Cache(cacheDir, size)
    }

    fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

    fun okhttp(): OkHttpClient.Builder {
        val httpClient = OkHttpClient.Builder()

        cache?.let { httpClient.cache(cache) }

        httpClient.addInterceptor { chain -> intercept(chain) }

        return httpClient
    }

    fun retrofit(): Retrofit.Builder {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(okhttp().build())
    }

    fun create(context: String?): T

}