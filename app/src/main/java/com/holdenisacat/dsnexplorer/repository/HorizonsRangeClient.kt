package com.holdenisacat.dsnexplorer.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

class HorizonsRangeClient {

    private val httpClient = HttpClient(OkHttp)
    private val cache = ConcurrentHashMap<String, CachedRange>()

    private val commandFallbacks = mapOf(
        "mms1" to "MMS-1",
        "mms2" to "MMS-2",
        "mms3" to "MMS-3",
        "mms4" to "MMS-4",
    )

    suspend fun getRangeKm(spacecraftId: String, spacecraftName: String): Float? {
        val cacheKey = spacecraftId.ifEmpty { spacecraftName }
        if (cacheKey.isEmpty()) {
            return null
        }

        cache[cacheKey]?.takeIf { !it.isExpired() }?.let { return it.rangeKm }

        val commands = buildCommandCandidates(spacecraftId, spacecraftName)
        for (command in commands) {
            val rangeKm = fetchRangeKm(command) ?: continue
            cache[cacheKey] = CachedRange(rangeKm, System.currentTimeMillis())
            return rangeKm
        }

        return null
    }

    private fun buildCommandCandidates(spacecraftId: String, spacecraftName: String): List<String> {
        val candidates = mutableListOf<String>()
        spacecraftId.toIntOrNull()?.takeIf { it > 0 }?.let { candidates.add("-$it") }
        commandFallbacks[spacecraftName.lowercase()]?.let { candidates.add(it) }
        return candidates.distinct()
    }

    private suspend fun fetchRangeKm(command: String): Float? {
        return try {
            val now = LocalDateTime.now(ZoneOffset.UTC)
            val start = now.format(HORIZONS_TIME_FORMAT)
            val stop = now.plusMinutes(1).format(HORIZONS_TIME_FORMAT)
            val url = buildString {
                append(HORIZONS_API_URL)
                append("?format=json")
                append("&COMMAND='").append(command).append("'")
                append("&MAKE_EPHEM='YES'")
                append("&EPHEM_TYPE='OBSERVER'")
                append("&CENTER='500@399'")
                append("&START_TIME='").append(start).append("'")
                append("&STOP_TIME='").append(stop).append("'")
                append("&STEP_SIZE='1%20m'")
                append("&QUANTITIES='20'")
            }

            val response = httpClient.get(url).bodyAsText()
            val result = extractHorizonsResult(response) ?: return null
            parseDeltaAu(result)?.let { (it * AU_IN_KM).toFloat() }
        } catch (e: Exception) {
            Timber.w(e, "Horizons range lookup failed for %s", command)
            null
        }
    }

    private fun extractHorizonsResult(response: String): String? {
        val resultKey = "\"result\":"
        val start = response.indexOf(resultKey)
        if (start < 0) {
            return null
        }

        val valueStart = response.indexOf('"', start + resultKey.length) + 1
        val builder = StringBuilder()
        var index = valueStart
        while (index < response.length) {
            when (val ch = response[index]) {
                '\\' -> {
                    if (index + 1 < response.length) {
                        builder.append(response[index + 1])
                        index += 2
                        continue
                    }
                }
                '"' -> return builder.toString()
                else -> builder.append(ch)
            }
            index++
        }

        return null
    }

    private fun parseDeltaAu(result: String): Double? {
        if (result.contains("Cannot interpret") || result.contains("INPUT ERROR")) {
            return null
        }

        val table = result.substringAfter("\$\$SOE", "")
            .substringBefore("\$\$EOE")
        val dataLine = table.lineSequence()
            .map { it.trim() }
            .firstOrNull { it.isNotEmpty() && it.first().isDigit() }
            ?: return null

        val parts = dataLine.split(Regex("\\s+"))
        if (parts.size < 4) {
            return null
        }

        return parts[2].toDoubleOrNull()
    }

    private data class CachedRange(
        val rangeKm: Float,
        val fetchedAtMs: Long,
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() - fetchedAtMs > CACHE_TTL_MS
    }

    companion object {
        private const val HORIZONS_API_URL = "https://ssd.jpl.nasa.gov/api/horizons.api"
        private const val AU_IN_KM = 149597870.7
        private const val CACHE_TTL_MS = 10 * 60 * 1000L
        private val HORIZONS_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }
}
