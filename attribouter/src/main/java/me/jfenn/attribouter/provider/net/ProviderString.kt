package me.jfenn.attribouter.provider.net

class ProviderString {

    val provider: String
    val context : String?
    val id: String

    constructor(provider: String, context: String? = null, id: String) {
        this.provider = provider
        this.context = context
        this.id = id
    }

    constructor(str: String) {
        if (str.isEmpty())
            throw RuntimeException("Attribouter - Empty ProviderString")

        val arr = str.split("@", ":")
        when (arr.size) {
            1 -> {
                provider = "github" // TODO: find a less ugly way of specifying this...
                context = null
                id = arr[0]
            }
            2 -> {
                provider = arr[0]
                context = null
                id = arr[1]
            }
            3 -> {
                provider = arr[0]
                context = arr[1]
                id = arr[2]
            }
            else -> throw RuntimeException("Attribouter - ProviderString with too many parts! '$str' Format: 'provider@context:id'")
        }
    }

    fun toProviderString(): String {
        return "$provider@${context ?: "null"}"
    }

    override fun toString(): String {
        return id
    }

    override fun equals(other: Any?): Boolean {
        return (other as? ProviderString)?.let {
            provider == it.provider && id == it.id
        } ?: super.equals(other)
    }

}