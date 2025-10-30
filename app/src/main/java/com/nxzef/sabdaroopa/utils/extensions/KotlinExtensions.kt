package com.nxzef.sabdaroopa.utils.extensions

fun Enum<*>.toPascalCase(): String {
    return name
        .lowercase()
        .split("_")
        .joinToString("") { it.replaceFirstChar { c -> c.titlecase() } }
}

fun <T> Set<T>.toggleInSet(id: T): Set<T> = if (id in this) this - id else this + id