package com.holdenisacat.dsnexplorer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.ui.components.DishView
import com.holdenisacat.dsnexplorer.util.SiteFlags

/**
 * Per-complex view: scrollable dish list above a site selector bar.
 */
@Composable
fun SitesScreen(
    dsn: Dsn,
    siteIndex: Int,
    onSiteSelected: (Int) -> Unit,
    onDishClick: (Dish) -> Unit,
    onSpacecraftClick: (Spacecraft) -> Unit,
    modifier: Modifier = Modifier,
) {
    val site = dsn.sites.getOrNull(siteIndex) ?: return

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(site.dishes, key = { it.name }) { dish ->
                DishView(
                    dish = dish,
                    onDishClick = onDishClick,
                    onSpacecraftClick = onSpacecraftClick,
                )
            }
        }

        Row {
            dsn.sites.forEachIndexed { index, siteEntry ->
                NavigationBarItem(
                    selected = index == siteIndex,
                    onClick = { onSiteSelected(index) },
                    icon = {
                        Image(
                            painter = painterResource(SiteFlags.flagFor(siteEntry.name)),
                            contentDescription = stringResource(R.string.complex_flag, siteEntry.friendlyName),
                        )
                    },
                    label = { Text(siteEntry.friendlyName) },
                )
            }
        }
    }
}
