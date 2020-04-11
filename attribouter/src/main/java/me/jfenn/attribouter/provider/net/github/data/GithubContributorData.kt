package me.jfenn.attribouter.provider.net.github.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.jfenn.attribouter.provider.net.data.UserData

@Serializable
class GithubContributorData : UserData() {
    @SerialName("login")
    override val login: String? = null

    @SerialName("avatar_url")
    override val avatarUrl: String? = null
}
