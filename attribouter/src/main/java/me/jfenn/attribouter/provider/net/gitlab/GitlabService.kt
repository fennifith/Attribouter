package me.jfenn.attribouter.provider.net.gitlab

import android.util.Log
import me.jfenn.attribouter.provider.net.ServiceBuilder
import me.jfenn.attribouter.provider.net.gitlab.data.GitlabLicenseData
import me.jfenn.attribouter.provider.net.gitlab.data.GitlabRepoData
import me.jfenn.attribouter.provider.net.gitlab.data.GitlabUserData
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GitlabService {

    @GET("users")
    suspend fun getUser(@Query("username") user: String): List<GitlabUserData>

    @GET("projects/{id}?license=true")
    suspend fun getRepo(@Path("id") projectId: String): GitlabRepoData

    @GET("projects/{id}/users")
    suspend fun getRepoContributors(@Path("id") projectId: String): List<GitlabUserData>

    @GET("licenses/{key}")
    suspend fun getLicense(@Path("key") key: String): GitlabLicenseData

    companion object: ServiceBuilder<GitlabProvider> {
        override val key: String = "gitlab"
        override var cache: Cache? = null
        private var token: String? = null

        fun withToken(token: String?): ServiceBuilder<GitlabProvider> {
            this.token = token
            return this
        }

        override fun create(context: String?): GitlabProvider {
            val retrofit = retrofit()
                    .baseUrl("https://${context ?: "gitlab.com"}/api/v4/")
                    .build()

            return GitlabProvider(retrofit.create(GitlabService::class.java))
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()

            // apply cache settings; prefer cached response to none at all
            request.cacheControl(CacheControl.Builder()
                    .maxAge(60, TimeUnit.SECONDS)
                    .maxStale(365, TimeUnit.DAYS)
                    .build())

            // add headers...
            token?.let { request.header("Authorization", "Bearer $it") }

            val response = chain.proceed(request.build())
            return run {
                if (response.code() == 401) {
                    Log.e("Attribouter", "GitLab auth token error; invalid / out of date - falling back to unauthenticated requests")
                    // Close current response, discard token, retry request...
                    response.close()
                    token = null
                    intercept(chain)
                } else response
            }
        }
    }

}