package me.jfenn.attribouter.provider.net

import me.jfenn.attribouter.provider.net.data.LicenseData
import me.jfenn.attribouter.provider.net.data.RepoData
import me.jfenn.attribouter.provider.net.data.UserData

class RequestProviderDelegate(private val services: List<ServiceBuilder<*>>) : RequestProvider {

    private val providers: MutableMap<String, RequestProvider> = HashMap()

    private fun get(str: ProviderString) : RequestProvider {
        val providerId = str.toProviderString()
        return providers[providerId] ?: run {
            services.firstOrNull { it.key == str.provider }?.create(str.context)?.also {
                providers[providerId] = it
            } ?: throw RuntimeException("Provider not found: $providerId")
        }
    }

    override suspend fun getUser(str: ProviderString): UserData? = get(str).getUser(str)
    override suspend fun getRepository(str: ProviderString): RepoData? = get(str).getRepository(str)
    override suspend fun getContributors(str: ProviderString): List<UserData>? = get(str).getContributors(str)
    override suspend fun getLicense(str: ProviderString): LicenseData? = get(str).getLicense(str)

}