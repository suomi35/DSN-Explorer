package com.holdenisacat.dsnexplorer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.text.htmlEncode
import com.holdenisacat.dsnexplorer.model.DataStatus
import com.holdenisacat.dsnexplorer.model.navigation.ModeButton
import com.holdenisacat.dsnexplorer.ui.theme.DSNExplorerTheme
import com.holdenisacat.dsnexplorer.util.Constants
import com.holdenisacat.dsnexplorer.util.DsnExplorerPreferences
import com.holdenisacat.dsnexplorer.util.Utils
import com.holdenisacat.dsnexplorer.view.DishDetailView
import com.holdenisacat.dsnexplorer.view.DishView
import com.holdenisacat.dsnexplorer.view.SiteTime
import com.holdenisacat.dsnexplorer.view.SpacecraftDetailView
import com.holdenisacat.dsnexplorer.view.SpacecraftListView
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {

    private val viewModel: MainViewModel by inject()
    private val preferences: DsnExplorerPreferences by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var expanded by remember { mutableStateOf(false) }
            var showSpacecraftDetailView by remember { mutableStateOf(false) }
            var showDishDetailView by remember { mutableStateOf(false) }

            val currentDish = viewModel.currentDishFlow.collectAsState()
            val spacecraftDetail = viewModel.spacecraftDetailFlow.collectAsState()
            val dishDetail = viewModel.dishDetailFlow.collectAsState()
            var viewModeIndex by remember { mutableIntStateOf(preferences.lastModeIndex) }

            val dsn = viewModel.dsnFlow.collectAsState().value
            val selectedIndex = viewModel.currentModeIndexFlow.collectAsState().value
            val lastSiteIndex = viewModel.currentSiteIndexFlow.collectAsState().value
            val siteTime = viewModel.siteTimeFlow.collectAsState()

            DSNExplorerTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(WindowInsets(0.dp)),
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row {
                                        Text(
                                            modifier = Modifier
                                                .weight(1f),
                                            text = stringResource(R.string.app_name),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        SiteTime(
                                            datetime = siteTime.value
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(
                                        onClick = {
                                            expanded = !expanded
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "More",
                                        )
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = {
                                                expanded = false
                                            }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.legend)) },
                                                onClick = {
//                                                    showLegendDialog = true
                                                    expanded = false
                                                }
                                            )
//                                            DropdownMenuItem(
//                                                text = { Text(stringResource(R.string.start_sender_broadcast)) },
//                                                onClick = {
//                                                    showConfirmStartSenderBroadcastDialog = true
//                                                    expanded = false
//                                                }
//                                            )
//                                            DropdownMenuItem(
//                                                text = { Text(stringResource(R.string.stop_sender_broadcast)) },
//                                                onClick = {
//                                                    showConfirmStopSenderBroadcastDialog = true
//                                                    expanded = false
//                                                }
//                                            )
                                        }
                                    }
                                }
                            )
                        },
                        bottomBar = { ModeButtonsView(
                            modeClicked = {
                                mode -> viewModeIndex = mode
//                                viewModel.updateSiteTime()
                                          },
                            preferences,
                            viewModeIndex,
                        ) }
                    ) { innerPadding ->

                        if (showDishDetailView) {
                            DishDetailView(
                                dish = dishDetail.value,
                                onDishClick = {
//                                    val i = Intent(Intent.ACTION_VIEW)
//                                    i.setData(it some uri)
//                                    startActivity(i)
                                },
                                onSpacecraftClick = {
                                    showDishDetailView = false
                                    viewModel.triggerSpacecraftDetailView(dsn.getAllDishesForSpacecraft(it).first(), it)
                                },
                                onDismissDetailView = { showDishDetailView = false },
                            )
                        }

                        if (showSpacecraftDetailView) {
                            SpacecraftDetailView(
                                dish = currentDish.value,
                                spacecraft = spacecraftDetail.value,
                                onDismissDetailView = { showSpacecraftDetailView = false },
                                onDishClick = {
                                    viewModel.triggerDishDetailView(it)
                                    showDishDetailView = true
                                },
                                onSpacecraftClick = {
                                    val i = Intent(Intent.ACTION_VIEW)
                                    i.setData(Uri.parse("${Constants.SPACECRAFT_EXTERNAL_LINK}${it.htmlEncode()}"))
                                    startActivity(i)
                                }
                            )
                        }

                        Column(
                            modifier = Modifier
                                .padding(innerPadding),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            println(">>> Setting content...")
                            val initiallyLoaded = viewModel.dataStatusFlow.collectAsState().value

                            when (initiallyLoaded) {
                                DataStatus.LOADED -> {
                                    println(">>> selectedIndex: $selectedIndex")

                                    when (viewModeIndex) {
                                        0 -> {  // Sites view
                                            println("Sites view")
                                            viewModel.updateSiteTime(dsn.sites[lastSiteIndex].getNiceTime())

                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                            ) {
                                                LazyColumn(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .weight(1f)
                                                ) {
                                                    items(dsn.sites[lastSiteIndex].getDishes(false)) { dish ->
                                                        DishView(
                                                            dish,
                                                            onSpacecraftClick = {
                                                                viewModel.triggerSpacecraftDetailView(dish, it)
                                                                showSpacecraftDetailView = true
                                                            }
                                                        )
                                                    }
                                                }

                                                Row {
                                                    dsn.sites.forEachIndexed { index, site ->
                                                        println(">>> creating UI for ${site.name}")
                                                        NavigationBarItem(
                                                            selected = index == lastSiteIndex,
                                                            onClick = {
                                                                viewModel.updateSiteIndex(index)
//                                                                viewModel.updateSiteTime(site.getNiceTime())
                                                                      },
                                                            icon = {
                                                                Image(
                                                                    painter = painterResource(id = Utils.getSiteFlag(site.name)),
                                                                    contentDescription = "${site.friendlyName} button"
                                                                )
                                                            },
                                                            label = {
                                                                Text(site.friendlyName)
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        else -> { // Spacecraft view
                                            println("Spacecraft view")
                                            viewModel.updateSiteTime()
                                            SpacecraftListView(
                                                dsn.getAllActiveSpacecraft(true),
                                                onSpacecraftClick = {
                                                    viewModel.triggerSpacecraftDetailView(dsn.getAllDishesForSpacecraft(it).first(), it)
                                                    showSpacecraftDetailView = true
                                                }
                                            )
                                        }
                                    }
                                }
                                DataStatus.LOADING -> {
                                    Box(
                                        Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.requiredWidth(58.dp),
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Image(
                                            modifier = Modifier
                                                .padding(top = 16.dp, start = 2.dp),
                                            painter = painterResource(id = R.mipmap.ic_nasa_logo),
                                            contentDescription = "nasa icon"
                                        )
                                    }
                                }
                                DataStatus.ERROR -> {
                                    println(">>> DataStatus.ERROR")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchDsnConfiguration()
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelDataUpdates()
    }
}

@Composable
fun ModeButtonsView(
    modeClicked: (Int) -> Unit,
    preferences: DsnExplorerPreferences,
    viewModeIndex: Int,
) {

    NavigationBar {
        ModeButton.items.forEachIndexed { index, modeButton ->
            NavigationBarItem(
                selected = viewModeIndex == index,
                onClick = {
                    preferences.lastModeIndex = index
                    modeClicked(index)
                          },
                icon = {
                    Icon(
                        painter = painterResource(id = modeButton.icon),
                        contentDescription = "${modeButton.label} button"
                    )
                },
                label = {
                    Text(modeButton.label)
                }
            )
        }
    }
}