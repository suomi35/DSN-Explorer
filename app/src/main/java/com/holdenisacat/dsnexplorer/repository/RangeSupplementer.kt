package com.holdenisacat.dsnexplorer.repository

import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.util.Formatters

/**
 * Fills in range/light-time estimates from JPL Horizons for spacecraft whose
 * DSN feed values are missing.
 *
 * Works on immutable models: returns a new [Dsn] with updated copies rather
 * than mutating shared state, so collectors of the previous snapshot are
 * unaffected.
 */
class RangeSupplementer(
    private val horizonsRangeClient: HorizonsRangeClient,
) {

    suspend fun supplement(dsn: Dsn): Dsn {
        val missingRangeBySpacecraft = dsn.missingRangeTargets()
        if (missingRangeBySpacecraft.isEmpty()) return dsn

        val estimatedRanges = mutableMapOf<String, Float>()
        for ((cacheKey, spacecraft) in missingRangeBySpacecraft) {
            if (cacheKey in estimatedRanges) continue
            horizonsRangeClient.getRangeKm(spacecraft.id, spacecraft.name)
                ?.let { estimatedRanges[cacheKey] = it }
        }
        if (estimatedRanges.isEmpty()) return dsn

        return dsn.copy(
            sites = dsn.sites.map { site ->
                site.copy(
                    dishes = site.dishes.map { dish ->
                        dish.withSupplementedRanges(estimatedRanges)
                    }
                )
            }
        )
    }

    /**
     * Distinct spacecraft needing a range estimate, keyed the same way the
     * client caches them so each target is fetched at most once per cycle.
     */
    private fun Dsn.missingRangeTargets(): Map<String, Spacecraft> =
        buildMap {
            for (site in sites) {
                for (dish in site.dishes) {
                    for (spacecraft in dish.spacecraftList) {
                        if (spacecraft.uplegRange > 0f) continue
                        val key = spacecraft.id.ifEmpty { spacecraft.name }.lowercase()
                        if (key.isNotEmpty()) putIfAbsent(key, spacecraft)
                    }
                }
            }
        }

    private fun Dish.withSupplementedRanges(estimatedRanges: Map<String, Float>): Dish {
        val supplemented = spacecraftList.map { spacecraft ->
            val key = spacecraft.id.ifEmpty { spacecraft.name }.lowercase()
            val rangeKm = estimatedRanges[key] ?: return@map spacecraft
            spacecraft.copy(
                uplegRange = rangeKm,
                downlegRange = rangeKm,
                roundtripLightTime = Formatters.roundTripLightTime(rangeKm),
                rangeIsEstimated = true,
            )
        }
        return if (supplemented == spacecraftList) this else copy(spacecraftList = supplemented)
    }
}
