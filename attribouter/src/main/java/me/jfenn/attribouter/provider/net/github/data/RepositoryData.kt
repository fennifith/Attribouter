package me.jfenn.attribouter.provider.net.github.data

data class RepositoryData(
        val full_name: String?,
        val html_url: String?,
        val description: String?,
        val homepage: String?,
        val license: RepositoryLicenseData?
)
