package me.jfenn.attribouter.interfaces

interface Mergeable<T> {
    fun merge(mergee: T): T
    fun hasAll(): Boolean
    val isHidden: Boolean
}