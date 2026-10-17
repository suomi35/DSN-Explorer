package com.holdenisacat.dsnexplorer.model

/**
 * Immutable snapshot of the live Deep Space Network state across all three complexes.
 */
data class Dsn(
    val sites: List<Site> = emptyList(),
) {
    /**
     * Every distinct spacecraft currently tracked by a dish, sorted by display name.
     *
     * The DSN feed can report the same spacecraft from multiple antennas; the first
     * occurrence wins and carries the complex flag of the site it was seen at.
     */
    fun activeSpacecraft(): List<Spacecraft> =
        sites.flatMap { site ->
            site.dishes.flatMap { dish ->
                dish.spacecraftList.map { it.copy(siteName = site.name) }
            }
        }
            .filter { it.name.isNotBlank() && !UNKNOWN_TARGETS.contains(it.name) }
            .distinctBy { it.name.lowercase() }
            .sortedBy { it.friendlyName.lowercase() }

    /** All dishes currently pointing at [spacecraft], across every site. */
    fun dishesForSpacecraft(spacecraft: Spacecraft): List<Dish> =
        sites.flatMap { it.dishes }
            .filter { dish -> dish.spacecraftList.any { it.id == spacecraft.id && it.id.isNotBlank() } }

    companion object {
        /** Placeholder entries the feed emits for unidentified or internal targets. */
        private val UNKNOWN_TARGETS = setOf("UFO", "dsn")
    }
}
