package com.holdenisacat.dsnexplorer.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.holdenisacat.dsnexplorer.DetailState
import com.holdenisacat.dsnexplorer.DsnUiState
import com.holdenisacat.dsnexplorer.MainViewModel
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Mode
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.ui.components.SiteTime
import com.holdenisacat.dsnexplorer.ui.screens.DishDetailDialog
import com.holdenisacat.dsnexplorer.ui.screens.SpacecraftDetailDialog
import com.holdenisacat.dsnexplorer.ui.screens.SpacecraftScreen
import com.holdenisacat.dsnexplorer.ui.screens.SitesScreen
import com.holdenisacat.dsnexplorer.util.DsnApi

/**
 * Root screen: top bar, mode navigation, content, and detail overlays.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val selectedMode = viewModel.selectedMode.collectAsState().value
    val siteIndex = viewModel.selectedSiteIndex.collectAsState().value
    val detail = viewModel.detail.collectAsState().value

    Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    val siteTime = (uiState as? DsnUiState.Loaded)
                        ?.dsn?.sites?.getOrNull(siteIndex)?.niceTime().orEmpty()
                    TopAppBar(
                        title = {
                            Row {
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = stringResource(R.string.app_name),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                SiteTime(datetime = siteTime)
                            }
                        },
                    )
                },
                bottomBar = {
                    ModeNavigationBar(
                        selectedMode = selectedMode,
                        onModeSelected = viewModel::selectMode,
                    )
                },
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (val state = uiState) {
                        DsnUiState.Loading -> LoadingState()
                        is DsnUiState.Loaded -> when (selectedMode) {
                            Mode.SITES -> SitesScreen(
                                dsn = state.dsn,
                                siteIndex = siteIndex,
                                onSiteSelected = viewModel::selectSite,
                                onDishClick = viewModel::showDishDetail,
                                onSpacecraftClick = viewModel::showSpacecraftDetail,
                            )

                            Mode.SPACECRAFT -> SpacecraftScreen(
                                spacecraft = state.dsn.activeSpacecraft(),
                                onSpacecraftClick = viewModel::showSpacecraftDetail,
                            )
                        }
                    }
                }
            }

            DetailOverlays(
                detail = detail,
                onDishClick = viewModel::showDishDetail,
                onSpacecraftClick = viewModel::showSpacecraftDetail,
                onDismiss = viewModel::dismissDetail,
            )
        }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.requiredWidth(58.dp),
            color = MaterialTheme.colorScheme.secondary,
        )
        Image(
            modifier = Modifier.padding(top = 16.dp, start = 2.dp),
            painter = painterResource(R.mipmap.ic_nasa_logo),
            contentDescription = null,
        )
    }
}

@Composable
private fun DetailOverlays(
    detail: DetailState,
    onDishClick: (Dish) -> Unit,
    onSpacecraftClick: (Dish, Spacecraft) -> Unit,
    onDismiss: () -> Unit,
) {
    when (detail) {
        DetailState.None -> Unit

        is DetailState.DishDetail -> DishDetailDialog(
            dish = detail.dish,
            onSpacecraftClick = { onSpacecraftClick(detail.dish, it) },
            onDismiss = onDismiss,
        )

        is DetailState.SpacecraftDetail -> {
            val context = LocalContext.current
            SpacecraftDetailDialog(
                dish = detail.dish,
                spacecraft = detail.spacecraft,
                onDishClick = onDishClick,
                onOpenExternal = { name ->
                    val url = "${DsnApi.SPACECRAFT_SEARCH_URL}${name.replace(" ", "%20")}"
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                },
                onDismiss = onDismiss,
            )
        }
    }
}

@Composable
private fun ModeNavigationBar(
    selectedMode: Mode,
    onModeSelected: (Mode) -> Unit,
) {
    NavigationBar {
        Mode.entries.forEach { mode ->
            NavigationBarItem(
                selected = mode == selectedMode,
                onClick = { onModeSelected(mode) },
                icon = {
                    Icon(
                        painter = painterResource(mode.iconRes()),
                        contentDescription = stringResource(mode.labelRes()),
                    )
                },
                label = { Text(stringResource(mode.labelRes())) },
            )
        }
    }
}

private fun Mode.iconRes(): Int = when (this) {
    Mode.SITES -> R.drawable.ic_tower
    Mode.SPACECRAFT -> R.drawable.ic_spacecraft
}

private fun Mode.labelRes(): Int = when (this) {
    Mode.SITES -> R.string.mode_sites
    Mode.SPACECRAFT -> R.string.mode_spacecraft
}
