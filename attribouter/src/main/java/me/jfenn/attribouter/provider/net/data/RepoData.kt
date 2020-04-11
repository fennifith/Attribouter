package me.jfenn.attribouter.provider.net.data

import me.jfenn.attribouter.provider.net.ProviderString

open class RepoData {
    @Transient
    open var id: ProviderString? = null
    open val slug: String? = null
    open val description: String? = null
    open val url: String? = null
    open val websiteUrl: String? = null
    open val license: LicenseData? = null
}
