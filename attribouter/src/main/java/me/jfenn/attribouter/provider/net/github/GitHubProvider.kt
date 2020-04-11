package me.jfenn.attribouter.provider.net.github

import android.util.Log
import me.jfenn.attribouter.provider.net.ProviderString
import me.jfenn.attribouter.provider.net.RequestProvider
import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.provider.net.data.UserData
import me.jfenn.attribouter.utils.catchNull

class GitHubProvider(
        private val service: GitHubService
) : RequestProvider {

    override suspend fun getUser(str: ProviderString): UserData? = catchNull {
        service.getUser(str.id).apply { id = str }
    }

    override suspend fun getRepository(str: ProviderString): RepoData? = catchNull {
        val repoId = str.id.split("/")
        if (repoId.size != 2) {
            Log.e("Attribouter", "Invalid github repo id: $str")
            throw RuntimeException()
        }

        service.getRepo(repoId[0], repoId[1]).apply { id = str }
    }

    override suspend fun getContributors(str: ProviderString): List<UserData>? = catchNull {
        val repoId = str.id.split("/")
        if (repoId.size != 2) {
            Log.e("Attribouter", "Invalid github repo id: $str")
            throw RuntimeException()
        }

        service.getRepoContributors(repoId[0], repoId[1]).map { contributor ->
            contributor.apply { id = ProviderString(str.provider, str.context, contributor.login!!) }
        }
    }

    override suspend fun getLicense(str: ProviderString): LicenseData? = catchNull {
        service.getLicense(str.id).apply { id = str }
    }

}