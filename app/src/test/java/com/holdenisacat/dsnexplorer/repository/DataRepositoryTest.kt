package com.holdenisacat.dsnexplorer.repository

import com.holdenisacat.dsnexplorer.model.DataStatus
import com.holdenisacat.dsnexplorer.parser.ConfigXmlParser
import com.holdenisacat.dsnexplorer.parser.DataXmlParser
import com.holdenisacat.dsnexplorer.util.DsnApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

class DataRepositoryTest {

    private val configXml: ByteArray = readResource("config.xml")
    private val dsnXml: ByteArray = readResource("dsn.xml")

    @Test
    fun refreshLoadsConfigurationThenData() = runTest {
        val configRequests = AtomicInteger(0)
        val dataRequests = AtomicInteger(0)
        val engine = MockEngine { request ->
            when {
                request.url.toString() == DsnApi.CONFIGURATION_URL -> {
                    configRequests.incrementAndGet()
                    respondOk(configXml)
                }
                request.url.toString().startsWith(DsnApi.DATA_URL) -> {
                    dataRequests.incrementAndGet()
                    respondOk(dsnXml)
                }
                else -> respondError(HttpStatusCode.NotFound)
            }
        }
        val repository = makeRepository(engine)

        val status = repository.refresh()

        assertEquals(DataStatus.LOADED, status)
        assertEquals(1, configRequests.get())
        assertEquals(1, dataRequests.get())

        val configuration = repository.configuration.value
        assertNotNull(configuration)
        assertEquals(2, configuration!!.sites.size)
        assertEquals(2, configuration.spacecraft.size)

        val dsn = repository.dsn.value
        assertNotNull(dsn)
        assertEquals(2, dsn!!.sites.size)
        val madrid = dsn.sites.first { it.name == "mdscc" }
        assertEquals("DSS53", madrid.dishes.first().name)
        assertEquals("M01O", madrid.dishes.first().spacecraftList.first().name)
    }

    @Test
    fun refreshCachesConfigurationAcrossCycles() = runTest {
        val configRequests = AtomicInteger(0)
        val dataRequests = AtomicInteger(0)
        val engine = MockEngine { request ->
            when {
                request.url.toString() == DsnApi.CONFIGURATION_URL -> {
                    configRequests.incrementAndGet()
                    respondOk(configXml)
                }
                request.url.toString().startsWith(DsnApi.DATA_URL) -> {
                    dataRequests.incrementAndGet()
                    respondOk(dsnXml)
                }
                else -> respondError(HttpStatusCode.NotFound)
            }
        }
        val repository = makeRepository(engine)

        assertEquals(DataStatus.LOADED, repository.refresh())
        assertEquals(DataStatus.LOADED, repository.refresh())

        // Configuration is fetched exactly once; data is re-fetched every cycle.
        assertEquals(1, configRequests.get())
        assertEquals(2, dataRequests.get())
    }

    @Test
    fun refreshReturnsErrorWhenConfigFetchFails() = runTest {
        val engine = MockEngine { throw IOException("network down") }
        val repository = makeRepository(engine)

        val status = repository.refresh()

        assertEquals(DataStatus.ERROR, status)
        assertNull(repository.configuration.value)
        assertNull(repository.dsn.value)
    }

    @Test
    fun refreshReturnsErrorWhenDataFetchFailsAfterConfig() = runTest {
        val engine = MockEngine { request ->
            if (request.url.toString() == DsnApi.CONFIGURATION_URL) {
                respondOk(configXml)
            } else {
                throw IOException("network down")
            }
        }
        val repository = makeRepository(engine)

        val status = repository.refresh()

        assertEquals(DataStatus.ERROR, status)
        assertNotNull(repository.configuration.value)
        assertNull(repository.dsn.value)
    }

    private fun makeRepository(engine: MockEngine): DataRepository {
        val horizons = object : HorizonsRangeClient(null) {
            override suspend fun getRangeKm(spacecraftId: String, spacecraftName: String): Float? = null
        }
        return DataRepository(
            httpClient = HttpClient(engine),
            configParser = ConfigXmlParser(),
            dataParser = DataXmlParser(),
            rangeSupplementer = RangeSupplementer(horizons),
        )
    }

    private fun io.ktor.client.engine.mock.MockRequestHandleScope.respondOk(content: ByteArray) = respond(
        content = content,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, listOf("application/xml")),
    )

    private fun readResource(name: String): ByteArray =
        javaClass.classLoader!!.getResourceAsStream(name)!!.readBytes()
}
