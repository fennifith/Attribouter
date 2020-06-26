package me.jfenn.attribouter.utils

import me.jfenn.gitrest.model.ProviderString
import java.util.regex.Pattern

fun Array<String>.toListString(): String {
    val builder = StringBuilder()
    for (str in this) {
        if (str.length > 1) {
            builder.append(str[0].toString().toUpperCase())
                    .append(str.replace('-', ' ').substring(1))
                    .append("\n")
        }
    }

    return builder.substring(0, builder.length - 1)
}

fun String.toTitleString(): String {
    var name = this
    if (name.contains("/")) {
        val names = name.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        name = run {
            if (names.size > 1 && names[1].isNotEmpty())
                names[1]
            else names[0]
        }
    }

    name = name.replace('-', ' ')
            .replace('_', ' ')
            .replace("([a-z])([A-Z])".toRegex(), "$1 $2")
            .replace("([A-Z])([A-Z][a-z])".toRegex(), "$1 $2")
            .trim { it <= ' ' }

    val nameBuffer = StringBuffer()
    val pattern = Pattern.compile("\\b(\\w)")
    val matcher = pattern.matcher(name)
    while (matcher.find())
        matcher.appendReplacement(nameBuffer, matcher.group(1)?.toUpperCase() ?: "")

    return matcher.appendTail(nameBuffer).toString()
}

fun String?.equalsProvider(other: String?) : Boolean {
    return this?.let {
        other?.let { other ->
            ProviderString(it) == ProviderString(other)
        }
    } == true
}
