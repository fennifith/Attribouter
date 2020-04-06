package me.jfenn.attribouter.utils

fun String?.isResourceMutable() : Boolean {
    return this?.let {
        !it.startsWith('^')
    } ?: true
}
