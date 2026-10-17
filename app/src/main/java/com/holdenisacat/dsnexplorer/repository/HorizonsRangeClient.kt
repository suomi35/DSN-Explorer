package com.holdenisacat.dsnexplorer.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

/**
 * Looks up one-way range estimates from the JPL Horizons API for spacecraft
 * whose range is missing from the DSN feed.
 *
 * Results are cached in memory for [CACHE_TTL_MS] so the 5-second DSN poll does
 * not hammer Horizons. Concurrent lookups for the same target are collapsed by
 * a per-key mutex.
 */
open class HorizonsRangeClient(
    private val httpClient: HttpClient? = null,
) {

    private val json = Json { ignoreUnknownKeys = true }
    private val cache = ConcurrentHashMap<String, CachedRange>()
    private val locks = ConcurrentHashMap<String, Mutex>()

    /**
     * @return range in km, or null when Horizons has no ephemeris for the target.
     */
    open suspend fun getRangeKm(spacecraftId: String, spacecraftName: String): Float? {
        val cacheKey = spacecraftId.ifEmpty { spacecraftName }.lowercase()
        if (cacheKey.isEmpty()) return null

        cache[cacheKey]?.takeIf { !it.isExpired() }?.let { return it.rangeKm }

        val mutex = locks.getOrPut(cacheKey) { Mutex() }
        return mutex.withLock {
            // Double-checked: another coroutine may have populated the cache while we waited.
            cache[cacheKey]?.takeIf { !it.isExpired() }?.let { return it.rangeKm }

            for (command in commandCandidates(spacecraftId, spacecraftName)) {
                val rangeKm = fetchRangeKm(command) ?: continue
                cache[cacheKey] = CachedRange(rangeKm, System.currentTimeMillis())
                return rangeKm
            }
            null
        }
    }

    private fun commandCandidates(spacecraftId: String, spacecraftName: String): List<String> =
        buildList {
            spacecraftId.toIntOrNull()?.takeIf { it > 0 }?.let { add("-$it") }
            FALLBACK_COMMANDS[spacecraftName.lowercase()]?.let { add(it) }
        }.distinct()

    private suspend fun fetchRangeKm(command: String): Float? = try {
        val response = httpClient!!.get(buildRequestUrl(command)).bodyAsText()
        val payload = json.decodeFromString<HorizonsResponse>(response)
        val result = payload.result
        if (result == null || payload.error != null) {
            Timber.d("Horizons has no ephemeris for %s: %s", command, payload.error)
            null
        } else {
            parseDeltaAu(result)?.let { it.toFloat() * AU_IN_KM.toFloat() }
        }
    } catch (e: Exception) {
        Timber.w(e, "Horizons range lookup failed for %s", command)
        null
    }

    private fun buildRequestUrl(command: String): String = buildString {
        val now = LocalDateTime.now(ZoneOffset.UTC)
        append(HORIZONS_API_URL)
        append("?format=json")
        append("&COMMAND='").append(command).append("'")
        append("&MAKE_EPHEM='YES'")
        append("&EPHEM_TYPE='OBSERVER'")
        append("&CENTER='500@399'")
        append("&START_TIME='").append(formatTime(now)).append("'")
        append("&STOP_TIME='").append(formatTime(now.plusMinutes(1))).append("'")
        append("&STEP_SIZE='1%20m'")
        append("&QUANTITIES='20'")
    }

    private fun formatTime(ldt: LocalDateTime): String =
        ldt.format(HORIZONS_TIME_FORMAT)

    /** Extracts the delta (au) column from the first row of the $$SOE..$$EOE table. */
    private fun parseDeltaAu(result: String): Double? {
        val table = result.substringAfter("\u0024\u0024SOE", missingDelimiterValue = "")
            .substringBefore("\u0024\u0024EOE", missingDelimiterValue = "")
        val dataLine = table.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.isNotEmpty() && it.first().isDigit() }
            ?: return null

        val parts = dataLine.split(WHITESPACE)
        if (parts.size < 3) return null
        return parts[2].toDoubleOrNull()
    }

    private data class CachedRange(
        val rangeKm: Float,
        val fetchedAtMs: Long,
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() - fetchedAtMs > CACHE_TTL_MS
    }

    @Serializable
    data class HorizonsResponse(
        val result: String? = null,
        val error: String? = null,
        @SerialName("signature") val signature: Signature? = null,
    ) {
        @Serializable
        data class Signature(val source: String? = null, val version: String? = null)
    }

    companion object {
        private const val HORIZONS_API_URL = "https://ssd.jpl.nasa.gov/api/horizons.api"
        private const val AU_IN_KM = 149_597_870.7
        private const val CACHE_TTL_MS = 10 * 60 * 1000L
        private val HORIZONS_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        private val WHITESPACE = Regex("\\s+")

        /** Horizons target names for a few spacecraft whose numeric ids resolve poorly. */
        private val FALLBACK_COMMANDS = mapOf(
            "mms1" to "MMS-1",
            "mms2" to "MMS-2",
            "mms3" to "MMS-3",
            "mms4" to "MMS-4",
        )
    }
}
