package me.jfenn.attribouter.provider.net.github

import android.util.Log
import me.jfenn.attribouter.provider.net.RequestProvider
import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.provider.net.data.SourceData
import me.jfenn.attribouter.provider.net.data.UserData
import me.jfenn.attribouter.utils.catchNull

class GitHubProvider(
        private val service: GitHubService
) : RequestProvider {

    override val id: String = "github"

    override suspend fun getUser(id: String): UserData? = catchNull {
        val data = service.getUser(id)

        UserData(
                login = data.login,
                name = data.name,
                avatarUrl = data.avatar_url,
                websiteUrl = data.blog,
                source = SourceData(data.html_url, id),
                email = data.email,
                bio = data.bio
        )
    }

    override suspend fun getRepository(id: String): RepoData? = catchNull {
        val repoId = id.split(":").last().split("/")
        if (repoId.size != 2) {
            Log.e("Attribouter", "Invalid github repo id: $id")
            throw RuntimeException()
        }

        val data = service.getRepo(repoId[0], repoId[1])
        RepoData(
                slug = data.full_name,
                source = SourceData(data.html_url, id),
                description = data.description,
                websiteUrl = data.homepage,
                license = data.license?.let {
                    LicenseData(
                            key = it.key,
                            source = SourceData(service = id),
                            name = it.name
                    )
                }
        )
    }

    override suspend fun getContributors(id: String): List<UserData>? = catchNull {
        val repoId = id.split(":").last().split("/")
        if (repoId.size != 2) {
            Log.e("Attribouter", "Invalid github repo id: $id")
            throw RuntimeException()
        }

        service.getRepoContributors(repoId[0], repoId[1]).map { contributor ->
            UserData(
                    login = contributor.login,
                    source = SourceData(contributor.html_url, id),
                    avatarUrl = contributor.avatar_url
            )
        }
    }

    override suspend fun getLicense(id: String): LicenseData? = catchNull {
        val data = service.getLicense(id)
        LicenseData(
                key = data.key,
                source = SourceData(service = id),
                name = data.name,
                description = data.description,
                body = data.body,
                infoUrl = data.html_url,
                permissions = data.permissions,
                conditions = data.conditions,
                limitations = data.limitations
        )
    }

    override fun destroy() {
    }

}