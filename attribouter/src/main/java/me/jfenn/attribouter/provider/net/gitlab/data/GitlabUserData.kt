package me.jfenn.attribouter.provider.net.gitlab.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.jfenn.attribouter.provider.net.data.UserData

@Serializable
class GitlabUserData : UserData() {

    @SerialName("username")
    override val login: String? = null

    @SerialName("name")
    override val name: String? = null

    @SerialName("web_url")
    override val url: String? = null

    @SerialName("avatar_url")
    override val avatarUrl: String? = null

    @SerialName("website_url")
    override val websiteUrl: String? = null

    @SerialName("public_email")
    override val email: String? = null

    @SerialName("bio")
    override val bio: String? = null

}
