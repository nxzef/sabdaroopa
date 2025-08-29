package com.nascriptone.siddharoopa.core.utils

fun Enum<*>.toPascalCase(): String {
    return name
        .lowercase()
        .split("_")
        .joinToString("") { it.replaceFirstChar { c -> c.titlecase() } }
}
