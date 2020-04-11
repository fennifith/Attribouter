package me.jfenn.attribouter.provider.net.data

import me.jfenn.attribouter.provider.net.ProviderString

open class LicenseData {
    @Transient
    open var id: ProviderString? = null
    open val key: String? = null
    open val name: String? = null
    open val description: String? = null
    open val body: String? = null
    open val infoUrl: String? = null
    open val permissions: Array<String>? = null
    open val conditions: Array<String>? = null
    open val limitations: Array<String>? = null
}