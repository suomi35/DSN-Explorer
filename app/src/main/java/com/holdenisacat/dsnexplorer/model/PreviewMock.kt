package com.holdenisacat.dsnexplorer.model

import com.holdenisacat.dsnexplorer.R

object PreviewMock {

    private fun signals(): ArrayList<Signal> {
        val signals = ArrayList<Signal>()
        signals.add(Signal(true, "data", "10", "9", "X", "-499", "M01O", "53", SignalDirection.UP))
        signals.add(Signal(true, "carrier", "5", "20", "X", "-100", "M01O", "53", SignalDirection.UP))
        signals.add(Signal(false, "none", "10", "245", "X", "-588", "MVN", "202", SignalDirection.UP))
        signals.add(Signal(true, "data", "10", "23", "X", "-899", "M01O", "53", SignalDirection.DOWN))
        signals.add(Signal(false, "none", "10", "12", "X", "-988", "MVN", "202", SignalDirection.DOWN))
        return signals
    }

    fun spacecraftList(): ArrayList<Spacecraft> {
        val spacecraftList = ArrayList<Spacecraft>()
        spacecraftList.add(Spacecraft("M01O", "53", "Mars Odyssey", false, "", 173000000F,
            173000000F, 1190F, R.drawable.flag_mdscc))
        spacecraftList.add(Spacecraft("MVN", "202", "MAVEN", false, "", 173000000F, 173000000F,
            4160F, R.drawable.flag_cdscc))
        return spacecraftList
    }

    fun noSpacecraft(): ArrayList<Spacecraft> {
        return ArrayList()
    }

    fun dish(isActive: Boolean = true): Dish {
        return Dish(
            name = "DSS53",
            friendlyName = "DSS 53",
            type = "34M",
            azimuthAngle = 276f,
            elevationAngle = 35f,
            windSpeed = "9",
            isMSPA = false,
            isArray = false,
            isDDOR = false,
            activity = "Spacecraft Telemetry, Tracking, and Command",
            isActive = isActive, // set by us during parsing
            dishLocation = "mdscc", // set to `site.name` during parsing
            signals = signals(),
            spacecraftList = spacecraftList(),
        )
    }

    fun spacecraft(): Spacecraft {
        return Spacecraft(
            "M01O",
            "53",
            "Mars Odyssey",
            false,
            "",
            173000000F,
            173000000F,
            1190F,
            R.drawable.flag_mdscc_128
        )
    }

    fun signalUp(): Signal {
        return Signal(true, "data", "10", "9", "X", "18", "M01O", "53", SignalDirection.UP)
    }

    fun signalDown(): Signal {
        return Signal(true, "carrier", "10", "12", "X", "-988", "MVN", "202", SignalDirection.DOWN)
    }

    fun dishArray(): LinkedHashSet<Dish> {
        val dishes = LinkedHashSet<Dish>()
        dishes.add(dish())

        return dishes
    }
}