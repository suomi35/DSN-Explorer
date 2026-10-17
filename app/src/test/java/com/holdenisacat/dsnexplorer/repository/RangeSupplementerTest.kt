package com.holdenisacat.dsnexplorer.repository

import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.model.Site
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.util.Formatters
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class RangeSupplementerTest {

    private val client = FakeHorizonsRangeClient(
        mapOf("m01o" to 150_000_000f, "mvn" to 200_000_000f)
    )
    private val supplementer = RangeSupplementer(client)

    @Test
    fun supplementReturnsSameDsnWhenNoMissingRanges() {
        val odyssey = Spacecraft(
            name = "M01O",
            explorerName = "53",
            friendlyName = "Mars Odyssey",
            uplegRange = 100_000_000f,
            downlegRange = 100_000_000f,
        )
        val maven = Spacecraft(
            name = "MVN",
            explorerName = "202",
            friendlyName = "MAVEN",
            uplegRange = 200_000_000f,
            downlegRange = 200_000_000f,
        )
        val dsn = Dsn(
            sites = listOf(
                Site(
                    name = "mdscc",
                    friendlyName = "Madrid",
                    latitude = "40",
                    longitude = "-4",
                    dishes = listOf(
                        Dish(name = "DSS53", friendlyName = "DSS 53", type = "34M", spacecraftList = listOf(odyssey, maven)),
                    ),
                ),
            ),
        )

        val result = runBlocking { supplementer.supplement(dsn) }

        assertSame(dsn, result)
        assertEquals(0, client.callCount)
    }

    @Test
    fun supplementFillsMissingRangesFromHorizons() {
        val odyssey = Spacecraft(
            name = "M01O",
            explorerName = "53",
            friendlyName = "Mars Odyssey",
            uplegRange = -1f,
            downlegRange = -1f,
        )
        val dsn = Dsn(
            sites = listOf(
                Site(
                    name = "mdscc",
                    friendlyName = "Madrid",
                    latitude = "40",
                    longitude = "-4",
                    dishes = listOf(
                        Dish(name = "DSS53", friendlyName = "DSS 53", type = "34M", spacecraftList = listOf(odyssey)),
                    ),
                ),
            ),
        )

        val result = runBlocking { supplementer.supplement(dsn) }

        val updatedOdyssey = result.sites.first().dishes.first().spacecraftList.first()
        assertEquals(150_000_000f, updatedOdyssey.uplegRange, 0.001f)
        assertEquals(150_000_000f, updatedOdyssey.downlegRange, 0.001f)
        assertEquals(Formatters.roundTripLightTime(150_000_000f), updatedOdyssey.roundtripLightTime, 0.001f)
        assertTrue(updatedOdyssey.rangeIsEstimated)
        assertEquals(1, client.callCount)
    }

    @Test
    fun supplementSkipsUncachedTargets() {
        // "unknown" is not in the fake client's map, so its range stays missing
        val unknown = Spacecraft(
            name = "UNKNOWN",
            explorerName = "999",
            friendlyName = "Unknown Probe",
            uplegRange = -1f,
            downlegRange = -1f,
        )
        val dsn = Dsn(
            sites = listOf(
                Site(
                    name = "mdscc",
                    friendlyName = "Madrid",
                    latitude = "40",
                    longitude = "-4",
                    dishes = listOf(
                        Dish(name = "DSS53", friendlyName = "DSS 53", type = "34M", spacecraftList = listOf(unknown)),
                    ),
                ),
            ),
        )

        val result = runBlocking { supplementer.supplement(dsn) }

        val updated = result.sites.first().dishes.first().spacecraftList.first()
        assertEquals(-1f, updated.uplegRange, 0.001f)
        assertEquals(-1f, updated.downlegRange, 0.001f)
    }

    @Test
    fun supplementDeduplicatesSameSpacecraftAcrossDishes() {
        val odyssey = Spacecraft(
            name = "M01O",
            explorerName = "53",
            friendlyName = "Mars Odyssey",
            uplegRange = -1f,
            downlegRange = -1f,
        )
        val dsn = Dsn(
            sites = listOf(
                Site(
                    name = "mdscc",
                    friendlyName = "Madrid",
                    latitude = "40",
                    longitude = "-4",
                    dishes = listOf(
                        Dish(name = "DSS53", friendlyName = "DSS 53", type = "34M", spacecraftList = listOf(odyssey)),
                        Dish(name = "DSS54", friendlyName = "DSS 54", type = "70M", spacecraftList = listOf(odyssey)),
                    ),
                ),
            ),
        )

        runBlocking { supplementer.supplement(dsn) }

        // Horizons should be called only once even though M01O appears on two dishes
        assertEquals(1, client.callCount)
    }

    private class FakeHorizonsRangeClient(
        private val ranges: Map<String, Float>,
    ) : HorizonsRangeClient(null) {
        var callCount = 0

        override suspend fun getRangeKm(spacecraftId: String, spacecraftName: String): Float? {
            callCount++
            val key = spacecraftId.ifEmpty { spacecraftName }.lowercase()
            return ranges[key]
        }
    }
}
