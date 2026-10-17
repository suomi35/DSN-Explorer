package com.holdenisacat.dsnexplorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holdenisacat.dsnexplorer.model.DataStatus
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.model.Mode
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.repository.DataRepository
import com.holdenisacat.dsnexplorer.util.DsnExplorerPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Single source of truth for everything the UI renders.
 *
 * Data refreshes on a foreground-only 5-second poll started with [startUpdates]
 * and cancelled with [stopUpdates] — no WorkManager needed for a live viewer
 * that only matters while visible.
 */
class MainViewModel(
    private val repository: DataRepository,
    private val preferences: DsnExplorerPreferences,
) : ViewModel() {

    /** Top-level screen state: loading, loaded-with-data, or error. */
    val uiState: StateFlow<DsnUiState> = repository.dsn
        .map { dsn -> if (dsn != null) DsnUiState.Loaded(dsn) else DsnUiState.Loading }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DsnUiState.Loading)

    private val _selectedMode = MutableStateFlow(Mode.fromIndex(preferences.lastModeIndex))
    val selectedMode: StateFlow<Mode> = _selectedMode.asStateFlow()

    private val _selectedSiteIndex = MutableStateFlow(preferences.lastSiteIndex)
    val selectedSiteIndex: StateFlow<Int> = _selectedSiteIndex.asStateFlow()

    private val _detail = MutableStateFlow<DetailState>(DetailState.None)
    val detail: StateFlow<DetailState> = _detail.asStateFlow()

    private val _refreshError = MutableStateFlow(false)
    val refreshError: StateFlow<Boolean> = _refreshError.asStateFlow()

    private var pollJob: Job? = null

    fun startUpdates() {
        if (pollJob?.isActive == true) return
        pollJob = viewModelScope.launch {
            while (isActive) {
                _refreshError.value = repository.refresh() == DataStatus.ERROR
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    fun stopUpdates() {
        pollJob?.cancel()
        pollJob = null
    }

    fun selectMode(mode: Mode) {
        preferences.lastModeIndex = mode.ordinal
        _selectedMode.value = mode
    }

    fun selectSite(index: Int) {
        val siteCount = (repository.dsn.value?.sites?.size ?: return).coerceAtLeast(1)
        val clamped = index.coerceIn(0, siteCount - 1)
        preferences.lastSiteIndex = clamped
        _selectedSiteIndex.value = clamped
    }

    fun showDishDetail(dish: Dish) {
        _detail.value = DetailState.DishDetail(dish)
    }

    fun showSpacecraftDetail(spacecraft: Spacecraft) {
        val dish = repository.dsn.value?.dishesForSpacecraft(spacecraft)?.firstOrNull() ?: return
        _detail.value = DetailState.SpacecraftDetail(dish, spacecraft)
    }

    fun showSpacecraftDetail(dish: Dish, spacecraft: Spacecraft) {
        _detail.value = DetailState.SpacecraftDetail(dish, spacecraft)
    }

    fun dismissDetail() {
        _detail.value = DetailState.None
    }

    companion object {
        private const val POLL_INTERVAL_MS = 5_000L
    }
}

/** Screen-level UI state derived from the repository's latest snapshot. */
sealed interface DsnUiState {
    data object Loading : DsnUiState
    data class Loaded(val dsn: Dsn) : DsnUiState
}

/** At most one detail overlay is shown at a time, replacing ad-hoc boolean pairs. */
sealed interface DetailState {
    data object None : DetailState
    data class DishDetail(val dish: Dish) : DetailState
    data class SpacecraftDetail(val dish: Dish, val spacecraft: Spacecraft) : DetailState
}
