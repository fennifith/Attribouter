package me.jfenn.attribouter.provider.data.github

import io.reactivex.Observable
import me.jfenn.attribouter.data.github.ContributorsData
import me.jfenn.attribouter.data.github.LicenseData
import me.jfenn.attribouter.data.github.RepositoryData
import me.jfenn.attribouter.data.github.UserData
import me.jfenn.attribouter.provider.data.RequestProvider

class GitHubProvider(
        private val service: GitHubService
) : RequestProvider {

    override val id: String = "github"

    override fun getUser(id: String): Observable<UserData> {
        return service.getUser(id)
    }

    override fun getRepository(id: String): Observable<RepositoryData> {
        return service.getRepository(id)
    }

    override fun getContributors(id: String): Observable<ContributorsData> {
        return service.getContributors(id)
    }

    override fun getLicense(id: String): Observable<LicenseData> {
        return service.getLicense(id)
    }

    override fun destroy() {
    }

}