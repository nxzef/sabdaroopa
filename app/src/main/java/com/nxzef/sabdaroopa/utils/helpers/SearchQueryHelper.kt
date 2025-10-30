package com.nxzef.sabdaroopa.utils.helpers

object SearchQueryHelper {
    /**
     * Prepare FTS query with wildcard for prefix matching
     * Just adds asterisk for partial word matching
     */
    fun prepareFtsQuery(input: String): String {
        val cleaned = input.trim()
        if (cleaned.isEmpty()) return ""

        // For multi-word queries
        return if (cleaned.contains(" ")) {
            cleaned.split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { "$it*" }
        } else {
            "$cleaned*"
        }
    }

    /**
     * Prepare exact match for ranking (LIKE query)
     */
    fun prepareExactMatch(input: String): String {
        return "${input.trim()}%"
    }
}