package me.jfenn.attribouter.provider.net

import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.provider.net.data.UserData

interface RequestProvider {

    val id: String

    suspend fun getUser(id: String): UserData?

    suspend fun getRepository(id: String): RepoData?

    suspend fun getContributors(id: String): List<UserData>?

    suspend fun getLicense(id: String): LicenseData?

    fun destroy()

}