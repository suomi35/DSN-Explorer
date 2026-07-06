package com.holdenisacat.dsnexplorer.repository

import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.util.Utils

class RangeSupplementer(
    private val horizonsRangeClient: HorizonsRangeClient,
) {

    suspend fun supplement(dsn: Dsn): Boolean {
        val spacecraftNeedingRange = linkedSetOf<Spacecraft>()
        for (site in dsn.sites) {
            for (dish in site.dishes) {
                for (spacecraft in dish.spacecraftList) {
                    if (spacecraft.uplegRange == -1f) {
                        spacecraftNeedingRange.add(spacecraft)
                    }
                }
            }
        }

        var updated = false
        for (spacecraft in spacecraftNeedingRange) {
            val rangeKm = horizonsRangeClient.getRangeKm(spacecraft.id, spacecraft.name) ?: continue
            spacecraft.uplegRange = rangeKm
            spacecraft.downlegRange = rangeKm
            spacecraft.roundtripLightTime = Utils.computeRoundTripLightTime(rangeKm)
            spacecraft.rangeIsEstimated = true
            updated = true
        }

        return updated
    }
}
