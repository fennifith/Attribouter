package me.jfenn.attribouter.provider.net

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.*
import retrofit2.Retrofit
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
        val json = Json(JsonConfiguration.Default.copy(ignoreUnknownKeys = true))
        val mediaType = MediaType.get("application/json")

        return Retrofit.Builder()
                .addConverterFactory(json.asConverterFactory(mediaType))
                .client(okhttp().build())
    }

    fun create(context: String?): T

}