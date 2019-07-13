package me.jfenn.attribouter.provider.net.data

class LicenseData(
        val key: String?,
        val source: SourceData,
        val name: String?,
        val description: String? = null,
        val body: String? = null,
        val infoUrl: String? = null,
        val permissions: Array<String>? = null,
        val conditions: Array<String>? = null,
        val limitations: Array<String>? = null
)