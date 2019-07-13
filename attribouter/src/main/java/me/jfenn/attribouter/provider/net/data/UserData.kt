package me.jfenn.attribouter.provider.net.data

data class UserData(
        val login: String?,
        val source: SourceData,
        val name: String? = null,
        val avatarUrl: String? = null,
        val websiteUrl: String? = null,
        val email: String? = null,
        val bio: String? = null
)