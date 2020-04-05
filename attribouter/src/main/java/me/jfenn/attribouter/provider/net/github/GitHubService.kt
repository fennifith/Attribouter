package me.jfenn.attribouter.provider.net.github

import me.jfenn.attribouter.provider.net.ServiceBuilder
import me.jfenn.attribouter.provider.net.github.data.ContributorData
import me.jfenn.attribouter.provider.net.github.data.LicenseData
import me.jfenn.attribouter.provider.net.github.data.RepositoryData
import me.jfenn.attribouter.provider.net.github.data.UserData
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {

    @GET("users/{user}")
    suspend fun getUser(@Path("user") user: String): UserData

    @GET("repos/{user}/{repo}")
    suspend fun getRepo(@Path("user") user: String, @Path("repo") repo: String): RepositoryData

    @GET("repos/{user}/{repo}/contributors")
    suspend fun getRepoContributors(@Path("user") user: String, @Path("repo") repo: String): List<ContributorData>

    @GET("licenses/{key}")
    suspend fun getLicense(@Path("key") key: String): LicenseData

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