package me.jfenn.attribouter.provider.net.gitlab.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.jfenn.attribouter.provider.net.data.RepoData

@Serializable
class GitlabRepoData : RepoData() {

    @SerialName("path_with_namespace")
    override val slug: String? = null

    @SerialName("description")
    override val description: String? = null

    @SerialName("web_url")
    override val url: String? = null

    @SerialName("license")
    override val license: GitlabLicenseData? = null
}
