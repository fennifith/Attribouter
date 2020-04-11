package me.jfenn.attribouter.provider.net.github

import android.util.Log
import me.jfenn.attribouter.provider.net.ServiceBuilder
import me.jfenn.attribouter.provider.net.github.data.GithubLicenseData
import me.jfenn.attribouter.provider.net.github.data.GithubRepoData
import me.jfenn.attribouter.provider.net.github.data.GithubUserData
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface GitHubService {

    @GET("users/{user}")
    suspend fun getUser(@Path("user") user: String): GithubUserData

    @GET("repos/{user}/{repo}")
    suspend fun getRepo(@Path("user") user: String, @Path("repo") repo: String): GithubRepoData

    @GET("repos/{user}/{repo}/contributors")
    suspend fun getRepoContributors(@Path("user") user: String, @Path("repo") repo: String): List<GithubUserData>

    @GET("licenses/{key}")
    suspend fun getLicense(@Path("key") key: String): GithubLicenseData

    companion object: ServiceBuilder<GitHubProvider> {
        override val key: String = "github"
        override var cache: Cache? = null
        private var token: String? = null

        fun withToken(token: String?): ServiceBuilder<GitHubProvider> {
            this.token = token
            return this
        }

        override fun create(context: String?): GitHubProvider {
            val retrofit = retrofit()
                    .baseUrl("https://${context ?: "api.github.com"}")
                    .build()

            return GitHubProvider(retrofit.create(GitHubService::class.java))
        }

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()

            // apply cache settings; prefer cached response to none at all
            request.cacheControl(CacheControl.Builder()
                    .maxAge(60, TimeUnit.SECONDS)
                    .maxStale(365, TimeUnit.DAYS)
                    .build())

            // add headers...
            request.header("Accept", "application/vnd.github.v3+json")
            token?.let { request.header("Authorization", "token $it") }

            val response = chain.proceed(request.build())
            return run {
                if (response.code() == 401) {
                    Log.e("Attribouter", "GitHub auth token error; invalid / out of date - falling back to unauthenticated requests")
                    // Close current response, discard token, retry request...
                    response.close()
                    token = null
                    intercept(chain)
                } else response
            }
        }
    }

}