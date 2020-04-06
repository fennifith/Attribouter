package me.jfenn.attribouter.provider.net.data

data class RepoData(
        val slug: String?,
        val source: SourceData,
        val description: String?,
        val websiteUrl: String?,
        val license: LicenseData?
)
