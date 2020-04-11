package me.jfenn.attribouter.provider.net.gitlab

import kotlinx.serialization.ImplicitReflectionSerializer
import me.jfenn.attribouter.provider.net.ProviderString
import me.jfenn.attribouter.provider.net.RequestProvider
import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.provider.net.data.UserData
import me.jfenn.attribouter.utils.catchNull

class GitlabProvider(
        private val service: GitlabService
) : RequestProvider {

    @ImplicitReflectionSerializer
    override suspend fun getUser(str: ProviderString): UserData? = catchNull {
        service.getUser(str.id).firstOrNull()?.apply { id = str }
    }

    override suspend fun getRepository(str: ProviderString): RepoData? = catchNull {
        service.getRepo(str.id).apply {
            id = str
            license?.apply { id = ProviderString(str.provider, str.context, this.key!!) }
        }
    }

    override suspend fun getContributors(str: ProviderString): List<UserData>? = catchNull {
        service.getRepoContributors(str.id).map { contributor ->
            contributor.apply { id = ProviderString(str.provider, str.context, contributor.login!!) }
        }
    }

    override suspend fun getLicense(str: ProviderString): LicenseData? = catchNull {
        service.getLicense(str.id).apply { id = str }
    }

}