package com.holdenisacat.dsnexplorer

import com.holdenisacat.dsnexplorer.model.DataStatus
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.model.Mode
import com.holdenisacat.dsnexplorer.model.Site
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.repository.DsnRepository
import com.holdenisacat.dsnexplorer.util.PreferencesStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val repository = FakeDsnRepository()
    private val preferences = FakePreferences()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialModeComesFromPreferences() {
        preferences.lastModeIndex = Mode.SPACECRAFT.ordinal
        createViewModel()

        assertEquals(Mode.SPACECRAFT, viewModel.selectedMode.value)
    }

    @Test
    fun initialSiteIndexComesFromPreferences() {
        preferences.lastSiteIndex = 2
        createViewModel()

        assertEquals(2, viewModel.selectedSiteIndex.value)
    }

    @Test
    fun selectModeUpdatesStateAndPersists() {
        createViewModel()

        viewModel.selectMode(Mode.SPACECRAFT)

        assertEquals(Mode.SPACECRAFT, viewModel.selectedMode.value)
        assertEquals(Mode.SPACECRAFT.ordinal, preferences.lastModeIndex)
    }

    @Test
    fun selectSiteClampsIndexAndPersists() {
        repository.dsnValue = makeDsn(siteCount = 3)
        createViewModel()

        viewModel.selectSite(10)

        assertEquals(2, viewModel.selectedSiteIndex.value)
        assertEquals(2, preferences.lastSiteIndex)
    }

    @Test
    fun selectSiteWithNoDsnKeepsCurrentIndex() {
        repository.dsnValue = null
        preferences.lastSiteIndex = 1
        createViewModel()

        viewModel.selectSite(5)

        assertEquals(1, viewModel.selectedSiteIndex.value)
    }

    @Test
    fun showDishDetailSetsState() {
        createViewModel()
        val dish = makeDish()

        viewModel.showDishDetail(dish)

        val detail = viewModel.detail.value
        assertTrue(detail is DetailState.DishDetail)
        assertSame(dish, (detail as DetailState.DishDetail).dish)
    }

    @Test
    fun showSpacecraftDetailFindsDishFromRepository() {
        val dish = makeDish()
        repository.dsnValue = Dsn(
            sites = listOf(
                Site(name = "mdscc", friendlyName = "Madrid", latitude = "", longitude = "", dishes = listOf(dish)),
            ),
        )
        createViewModel()

        viewModel.showSpacecraftDetail(dish.spacecraftList.first())

        val detail = viewModel.detail.value
        assertTrue(detail is DetailState.SpacecraftDetail)
        assertEquals("DSS53", (detail as DetailState.SpacecraftDetail).dish.name)
    }

    @Test
    fun showSpacecraftDetailWithoutMatchingDishDoesNothing() {
        createViewModel()
        val spacecraft = Spacecraft(name = "X", explorerName = "1", friendlyName = "Probe")

        viewModel.showSpacecraftDetail(spacecraft)

        assertEquals(DetailState.None, viewModel.detail.value)
    }

    @Test
    fun showSpacecraftDetailWithExplicitDishSetsState() {
        createViewModel()
        val dish = makeDish()
        val spacecraft = dish.spacecraftList.first()

        viewModel.showSpacecraftDetail(dish, spacecraft)

        val detail = viewModel.detail.value
        assertTrue(detail is DetailState.SpacecraftDetail)
        assertSame(dish, (detail as DetailState.SpacecraftDetail).dish)
        assertSame(spacecraft, (detail as DetailState.SpacecraftDetail).spacecraft)
    }

    @Test
    fun dismissDetailResetsToNone() {
        createViewModel()
        viewModel.showDishDetail(makeDish())

        viewModel.dismissDetail()

        assertEquals(DetailState.None, viewModel.detail.value)
    }

    @Test
    fun uiStateStartsAsLoadingWhenRepositoryEmpty() {
        repository.dsnValue = null
        createViewModel()

        assertEquals(DsnUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiStateIsLoadedWhenRepositoryHasData() {
        repository.dsnValue = makeDsn()
        createViewModel()

        val state = viewModel.uiState.value
        assertTrue(state is DsnUiState.Loaded)
        assertEquals(1, (state as DsnUiState.Loaded).dsn.sites.size)
    }

    @Test
    fun refreshErrorReflectsRepositoryStatus() {
        createViewModel()
        assertFalse(viewModel.refreshError.value)

        repository.nextRefreshStatus = DataStatus.ERROR
        viewModel.startUpdates()

        assertTrue(viewModel.refreshError.value)
    }

    @Test
    fun refreshErrorClearsOnSubsequentSuccess() {
        repository.nextRefreshStatus = DataStatus.ERROR
        createViewModel()
        viewModel.startUpdates()
        assertTrue(viewModel.refreshError.value)
        viewModel.stopUpdates()

        repository.nextRefreshStatus = DataStatus.LOADED
        viewModel.startUpdates()

        assertFalse(viewModel.refreshError.value)
    }

    @Test
    fun startUpdatesRefreshesImmediately() {
        createViewModel()

        viewModel.startUpdates()

        assertEquals(1, repository.refreshCallCount)
    }

    @Test
    fun stopUpdatesCancelsPolling() {
        createViewModel()
        viewModel.startUpdates()
        val callsAfterStart = repository.refreshCallCount

        viewModel.stopUpdates()

        assertEquals(callsAfterStart, repository.refreshCallCount)
    }

    private fun createViewModel() {
        viewModel = MainViewModel(repository, preferences)
    }

    private fun makeDish(): Dish = Dish(
        name = "DSS53",
        friendlyName = "DSS 53",
        type = "34M",
        spacecraftList = listOf(
            Spacecraft(name = "M01O", explorerName = "53", friendlyName = "Mars Odyssey", id = "53"),
        ),
    )

    private fun makeDsn(siteCount: Int = 1): Dsn = Dsn(
        sites = (0 until siteCount).map { index ->
            Site(
                name = "site$index",
                friendlyName = "Site $index",
                latitude = "0",
                longitude = "0",
                dishes = emptyList(),
            )
        },
    )

    private class FakeDsnRepository : DsnRepository {
        private val _dsn = MutableStateFlow<Dsn?>(null)
        override val dsn: StateFlow<Dsn?> = _dsn.asStateFlow()

        var dsnValue: Dsn?
            get() = _dsn.value
            set(value) {
                _dsn.value = value
            }

        var nextRefreshStatus: DataStatus = DataStatus.LOADED
        var refreshCallCount: Int = 0
            private set

        override suspend fun refresh(): DataStatus {
            refreshCallCount++
            return nextRefreshStatus
        }
    }

    private class FakePreferences : PreferencesStore {
        override var lastSiteIndex: Int = 0
        override var lastModeIndex: Int = 0
    }
}
