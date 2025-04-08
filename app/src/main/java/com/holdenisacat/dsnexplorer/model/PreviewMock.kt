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

    fun targets(): ArrayList<Target> {
        val targets = ArrayList<Target>()
        targets.add(Target("M01O", "53", 173000000F, 173000000F, 1190F, "Mars Odyssey", false, R.drawable.flag_mdscc))
        targets.add(Target("MVN", "202", 173000000F, 173000000F, 4160F, "MAVEN", false, R.drawable.flag_cdscc))
        return targets
    }

    fun noTargets(): ArrayList<Target> {
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
            targets = targets()
        )
    }

    fun target(): Target {
        return Target(
            "M01O",
            "53",
            173000000F,
            173000000F,
            1190F,
            "Mars Odyssey",
            false
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