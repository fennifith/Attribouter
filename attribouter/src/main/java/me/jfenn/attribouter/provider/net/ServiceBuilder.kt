package me.jfenn.attribouter.provider.net

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ServiceBuilder<T: RequestProvider> {

    var headers : MutableMap<String, String>

    fun withHeader(key: String, value: String): ServiceBuilder<T> {
        headers[key] = value
        return this
    }

    fun okhttp(): OkHttpClient.Builder {
        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor(object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder()
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

    fun create(): T

}