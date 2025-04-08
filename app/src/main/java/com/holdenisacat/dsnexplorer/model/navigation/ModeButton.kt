package com.holdenisacat.dsnexplorer.model.navigation

import com.holdenisacat.dsnexplorer.R

data class ModeButton(
    val label: String = "",
    val icon: Int
) {

    companion object {
        private val Sites = ModeButton("Sites", R.drawable.ic_tower)
        private val Spacecraft = ModeButton("Spacecraft", R.drawable.ic_spacecraft)

        val items
            get() = listOf(
                Sites,
                Spacecraft
            )
    }
}