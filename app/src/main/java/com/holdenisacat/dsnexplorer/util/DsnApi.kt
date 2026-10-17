package com.holdenisacat.dsnexplorer.util

/**
 * Endpoints and asset roots for NASA's "Eyes on the Solar System" DSN feeds.
 *
 * These are unofficial, read-only HTTP endpoints powering eyes.nasa.gov/dsn.
 */
object DsnApi {

    private const val BASE_URL = "https://eyes.nasa.gov/dsn/"

    /** Static site/dish/spacecraft metadata; fetched once per session. */
    const val CONFIGURATION_URL = "${BASE_URL}config.xml"

    /** Live antenna/signal state; the `r` parameter is a cache-busting epoch second. */
    const val DATA_URL = "${BASE_URL}data/dsn.xml?r="

    /** Quantized dish renders keyed by name, azimuth, elevation and 15-minute time bucket. */
    const val DISH_IMAGES_BASE_URL = "${BASE_URL}dish/"

    /** Spacecraft thumbnails keyed by lowercase spacecraft name. */
    const val SPACECRAFT_IMAGES_BASE_URL = "${BASE_URL}images/spacecraft/"

    /** Fallback search link when a spacecraft has no dedicated page. */
    const val SPACECRAFT_SEARCH_URL = "https://www.google.com/search?q=NASA%20"
}
