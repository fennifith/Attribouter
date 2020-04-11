package me.jfenn.attribouter.provider.net.github.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.jfenn.attribouter.provider.net.data.UserData

@Serializable
class GithubUserData : UserData() {

    @SerialName("login")
    override val login: String? = null

    @SerialName("name")
    override val name: String? = null

    @SerialName("avatar_url")
    override val avatarUrl: String? = null

    @SerialName("blog")
    override val websiteUrl: String? = null

    @SerialName("email")
    override val email: String? = null

    @SerialName("bio")
    override val bio: String? = null

}
