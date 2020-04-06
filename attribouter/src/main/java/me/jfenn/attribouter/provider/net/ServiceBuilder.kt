package me.jfenn.attribouter.provider.net

import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

interface ServiceBuilder<T: RequestProvider> {

    val key : String
    var headers : MutableMap<String, String>
    var cache : Cache?

    fun withHeader(key: String, value: String): ServiceBuilder<T> {
        headers[key] = value
        return this
    }

    fun withCache(cacheDir: File, size: Long = 10 * 1024 * 1024) {
        cache = Cache(cacheDir, size)
    }

    fun okhttp(): OkHttpClient.Builder {
        val httpClient = OkHttpClient.Builder()

        cache?.let { httpClient.cache(cache) }

        httpClient.addInterceptor(object: Interceptor {

            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder()

                request.cacheControl(CacheControl.Builder()
                        .maxAge(60, TimeUnit.SECONDS)
                        .maxStale(365, TimeUnit.DAYS)
                        .build())

                for ((key, value) in headers)
                    request.addHeader(key, value)

                return chain.proceed(request.build())
            }
        })

        return httpClient
    }

    fun retrofit(): Retrofit.Builder {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(okhttp().build())
    }

    fun create(context: String?): T

}