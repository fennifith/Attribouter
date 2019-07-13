package me.jfenn.attribouter.provider.net.github

import io.reactivex.Observable
import me.jfenn.attribouter.provider.net.ServiceBuilder
import me.jfenn.attribouter.provider.net.github.data.ContributorsData
import me.jfenn.attribouter.provider.net.github.data.LicenseData
import me.jfenn.attribouter.provider.net.github.data.RepositoryData
import me.jfenn.attribouter.provider.net.github.data.UserData
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {

    @GET("users/{user}")
    fun getUser(@Path("user") user: String): Observable<UserData>

    @GET("repos/{repo}")
    fun getRepo(@Path("repo") repo: String): Observable<RepositoryData>

    @GET("repos/{repo}/contributors")
    fun getRepoContributors(@Path("repo") repo: String): Observable<ContributorsData>

    @GET("licenses/{key}")
    fun getLicense(@Path("key") key: String): Observable<LicenseData>

    companion object: ServiceBuilder<GitHubProvider> {
        override var headers: MutableMap<String, String> = HashMap()

        fun withToken(token: String?): ServiceBuilder<GitHubProvider> {
            return token?.let {
                withHeader("Authorization", "token $it")
            } ?: this
        }

        override fun create(): GitHubProvider {
            withHeader("Accept", "application/vnd.github.v3+json")

            val retrofit = retrofit()
                    .baseUrl("https://api.github.com")
                    .build()

            return GitHubProvider(retrofit.create(GitHubService::class.java))
        }
    }

}