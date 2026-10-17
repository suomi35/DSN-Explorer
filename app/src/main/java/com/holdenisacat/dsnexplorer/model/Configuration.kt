package com.holdenisacat.dsnexplorer.model

/**
 * Static site/dish/spacecraft metadata loaded once from the DSN `config.xml` feed.
 */
data class Configuration(
    val sites: List<Site> = emptyList(),
    val spacecraft: List<Spacecraft> = emptyList(),
) {
    fun siteByName(name: String): Site? =
        sites.firstOrNull { it.name.equals(name, ignoreCase = true) }

    fun spacecraftByName(name: String): Spacecraft? =
        spacecraft.firstOrNull { it.name.equals(name, ignoreCase = true) }
}
