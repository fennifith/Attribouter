package me.jfenn.attribouter.provider.net.github.data

data class LicenseData(
        val key: String,
        val name: String?,
        val html_url: String?,
        val description: String?,
        val permissions: Array<String>?,
        val conditions: Array<String>?,
        val limitations: Array<String>?,
        val body: String?
)
