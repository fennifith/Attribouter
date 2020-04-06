package me.jfenn.attribouter.utils

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
