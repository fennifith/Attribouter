package me.jfenn.attribouter.provider.net.github

import io.reactivex.Observable
import me.jfenn.attribouter.provider.net.RequestProvider
import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.provider.net.data.SourceData
import me.jfenn.attribouter.provider.net.data.UserData

class GitHubProvider(
        private val service: GitHubService
) : RequestProvider {

    override val id: String = "github"

    override fun getUser(id: String): Observable<UserData> {
        return service.getUser(id).map { data ->
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
    }

    override fun getRepository(id: String): Observable<RepoData> {
        return service.getRepo(id).map { data ->
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
    }

    override fun getContributors(id: String): Observable<Array<UserData>> {
        return service.getRepoContributors(id).map { data ->
            data.contributors?.map { contributor ->
                UserData(
                        login = contributor.login,
                        source = SourceData(contributor.html_url, id),
                        avatarUrl = contributor.avatar_url
                )
            }?.toTypedArray()
        }
    }

    override fun getLicense(id: String): Observable<LicenseData> {
        return service.getLicense(id).map { data ->
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
    }

    override fun destroy() {
    }

}