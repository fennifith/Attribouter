package me.jfenn.attribouter.provider.net

import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.provider.net.data.UserData

interface RequestProvider {

    suspend fun getUser(str: ProviderString): UserData?

    suspend fun getRepository(str: ProviderString): RepoData?

    suspend fun getContributors(str: ProviderString): List<UserData>?

    suspend fun getLicense(str: ProviderString): LicenseData?

}