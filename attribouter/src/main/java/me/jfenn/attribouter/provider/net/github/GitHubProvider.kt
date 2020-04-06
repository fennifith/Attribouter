package me.jfenn.attribouter.provider.net.github

import android.util.Log
import me.jfenn.attribouter.provider.net.ProviderString
import me.jfenn.attribouter.provider.net.RequestProvider
import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.provider.net.data.SourceData
import me.jfenn.attribouter.provider.net.data.UserData
import me.jfenn.attribouter.utils.catchNull

class GitHubProvider(
        private val service: GitHubService
) : RequestProvider {

    override suspend fun getUser(str: ProviderString): UserData? = catchNull {
        val data = service.getUser(str.id)

        UserData(
                login = data.login,
                name = data.name,
                avatarUrl = data.avatar_url,
                websiteUrl = data.blog,
                source = SourceData(data.html_url, str.id),
                email = data.email,
                bio = data.bio
        )
    }

    override suspend fun getRepository(str: ProviderString): RepoData? = catchNull {
        val repoId = str.id.split("/")
        if (repoId.size != 2) {
            Log.e("Attribouter", "Invalid github repo id: $str")
            throw RuntimeException()
        }

        val data = service.getRepo(repoId[0], repoId[1])
        RepoData(
                slug = data.full_name,
                source = SourceData(data.html_url, str.id),
                description = data.description,
                websiteUrl = data.homepage,
                license = data.license?.let {
                    LicenseData(
                            key = it.key,
                            source = SourceData(service = str.id),
                            name = it.name
                    )
                }
        )
    }

    override suspend fun getContributors(str: ProviderString): List<UserData>? = catchNull {
        val repoId = str.id.split("/")
        if (repoId.size != 2) {
            Log.e("Attribouter", "Invalid github repo id: $str")
            throw RuntimeException()
        }

        service.getRepoContributors(repoId[0], repoId[1]).map { contributor ->
            UserData(
                    login = contributor.login,
                    source = SourceData(contributor.html_url, str.id),
                    avatarUrl = contributor.avatar_url
            )
        }
    }

    override suspend fun getLicense(str: ProviderString): LicenseData? = catchNull {
        val data = service.getLicense(str.id)
        LicenseData(
                key = data.key,
                source = SourceData(service = str.id),
                name = data.name,
                description = data.description,
                body = data.body,
                infoUrl = data.html_url,
                permissions = data.permissions,
                conditions = data.conditions,
                limitations = data.limitations
        )
    }

}