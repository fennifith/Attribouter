package me.jfenn.attribouter.provider.net.data

import me.jfenn.attribouter.provider.net.ProviderString

open class UserData {
    @Transient
    open var id: ProviderString? = null
    open val login: String? = null
    open val name: String? = null
    open val avatarUrl: String? = null
    open val websiteUrl: String? = null
    open val email: String? = null
    open val bio: String? = null
}