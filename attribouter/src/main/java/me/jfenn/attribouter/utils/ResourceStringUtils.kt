package me.jfenn.attribouter.utils

fun String?.isResourceMutable() : Boolean {
    return this?.let {
        !it.startsWith('^')
    } ?: true
}

fun String.getProvider() : String {
    return if (contains(':'))
        split(':')[0]
    else throw RuntimeException("String service provider not specified: \"$this\".")
}

fun String?.getProviderOrNull() : String? {
    return this?.let {
        try {
            getProvider()
        } catch (e : RuntimeException) {
            null
        }
    }
}
