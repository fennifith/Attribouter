package me.jfenn.attribouter.provider.data.github

import io.reactivex.Observable
import me.jfenn.attribouter.data.github.ContributorsData
import me.jfenn.attribouter.data.github.LicenseData
import me.jfenn.attribouter.data.github.RepositoryData
import me.jfenn.attribouter.data.github.UserData
import me.jfenn.attribouter.provider.data.ServiceBuilder
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {

    @GET("users/{user}")
    fun getUser(@Path("user") user: String): Observable<UserData>

    @GET("repos/{repo}")
    fun getRepository(@Path("repo") repo: String): Observable<RepositoryData>

    @GET("repos/{repo}/contributors")
    fun getContributors(@Path("repo") repo: String): Observable<ContributorsData>

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