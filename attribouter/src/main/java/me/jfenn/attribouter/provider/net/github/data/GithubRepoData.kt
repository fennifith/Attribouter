package me.jfenn.attribouter.provider.net.github.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.jfenn.attribouter.provider.net.data.RepoData

@Serializable
class GithubRepoData : RepoData() {

    @SerialName("full_name")
    override val slug: String? = null

    @SerialName("description")
    override val description: String? = null

    @SerialName("html_url")
    override val url: String? = null

    @SerialName("homepage")
    override val websiteUrl: String? = null

    @SerialName("license")
    override val license: GithubLicenseData? = null
}
