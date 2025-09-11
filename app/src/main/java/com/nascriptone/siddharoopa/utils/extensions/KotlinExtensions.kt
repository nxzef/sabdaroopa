package com.nascriptone.siddharoopa.utils.extensions

fun Enum<*>.toPascalCase(): String {
    return name
        .lowercase()
        .split("_")
        .joinToString("") { it.replaceFirstChar { c -> c.titlecase() } }
}
