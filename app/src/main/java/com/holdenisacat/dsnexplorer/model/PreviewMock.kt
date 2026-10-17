package com.holdenisacat.dsnexplorer.model

/** Sample data for @Preview composables; never used at runtime. */
object PreviewMock {

    fun signals(): List<Signal> = listOf(
        Signal(true, SignalType.DATA, "10", "8420031518", "X", "-499", "m01o", "53", SignalDirection.UP),
        Signal(true, SignalType.CARRIER, "5", "8420031518", "X", "-100", "m01o", "53", SignalDirection.UP),
        Signal(false, SignalType.NONE, "10", "8497800000", "X", "-588", "mvn", "202", SignalDirection.UP),
        Signal(true, SignalType.DATA, "10", "8420031518", "X", "-899", "m01o", "53", SignalDirection.DOWN),
        Signal(false, SignalType.NONE, "10", "8497800000", "X", "-988", "mvn", "202", SignalDirection.DOWN),
    )

    fun spacecraftList(): List<Spacecraft> = listOf(
        Spacecraft(
            name = "M01O",
            explorerName = "53",
            friendlyName = "Mars Odyssey",
            uplegRange = 173_000_000f,
            downlegRange = 173_000_000f,
            roundtripLightTime = 1190f,
            siteName = "mdscc",
        ),
        Spacecraft(
            name = "MVN",
            explorerName = "202",
            friendlyName = "MAVEN",
            uplegRange = 173_000_000f,
            downlegRange = 173_000_000f,
            roundtripLightTime = 4160f,
            siteName = "cdscc",
        ),
    )

    fun spacecraft(): Spacecraft = Spacecraft(
        name = "M01O",
        explorerName = "53",
        friendlyName = "Mars Odyssey",
        uplegRange = 173_000_000f,
        downlegRange = 173_000_000f,
        roundtripLightTime = 1190f,
        siteName = "mdscc",
    )

    fun dish(isActive: Boolean = true): Dish = Dish(
        name = "DSS53",
        friendlyName = "DSS 53",
        type = "34M",
        azimuthAngle = 276f,
        elevationAngle = 35f,
        windSpeed = "9",
        activity = "Spacecraft Telemetry, Tracking, and Command",
        siteName = "mdscc",
        signals = if (isActive) signals() else emptyList(),
        spacecraftList = spacecraftList(),
    )

    fun signalUp(): Signal = signals().first { it.direction == SignalDirection.UP && it.active }

    fun site(): Site = Site(
        name = "mdscc",
        friendlyName = "Madrid",
        latitude = "40.2413554",
        longitude = "-4.2480085",
        dishes = listOf(dish()),
        timeUTC = System.currentTimeMillis(),
        timeZoneOffset = 3_600_000L,
    )

    fun dsn(): Dsn = Dsn(sites = listOf(site()))
}
