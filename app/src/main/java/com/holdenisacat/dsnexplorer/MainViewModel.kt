package com.holdenisacat.dsnexplorer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holdenisacat.dsnexplorer.model.DataStatus
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.model.Target
import com.holdenisacat.dsnexplorer.repository.DataRepository
import com.holdenisacat.dsnexplorer.repository.WorkHandler
import com.holdenisacat.dsnexplorer.util.DsnExplorerPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MainViewModel : ViewModel(), KoinComponent {

    private val dataRepository: DataRepository by inject<DataRepository>()
    private val preferences: DsnExplorerPreferences by inject()

    private val _initiallyLoaded = MutableStateFlow(DataStatus.LOADING)
    val dataStatusFlow = _initiallyLoaded.asStateFlow()

    private val _siteTime = MutableStateFlow("")
    val siteTimeFlow = _siteTime.asStateFlow()

    private val _currentModeIndex = MutableStateFlow(preferences.lastModeIndex)
    val currentModeIndexFlow: StateFlow<Int> = _currentModeIndex.asStateFlow()

    private val currentSiteIndex: Int by mutableIntStateOf(preferences.lastSiteIndex)
    private val _currentSiteIndexFlow = MutableStateFlow(currentSiteIndex)
    val currentSiteIndexFlow: StateFlow<Int> get() = _currentSiteIndexFlow

    private val dsn: Dsn by mutableStateOf(Dsn())
    private val _dsnFlow = MutableStateFlow(dsn)
    val dsnFlow: StateFlow<Dsn> get() = _dsnFlow

    private val currentDish: Dish by mutableStateOf(Dish())
    private val _currentDishFlow = MutableStateFlow(currentDish)
    val currentDishFlow: StateFlow<Dish> get() = _currentDishFlow

//    private val dishes: LinkedHashSet<Dish> by mutableStateOf(LinkedHashSet())
//    private val _dishesFlow = MutableStateFlow(dishes)
//    val dishesFlow: StateFlow<LinkedHashSet<Dish>> get() = _dishesFlow

    private val dishDetail: Dish by mutableStateOf(Dish())
    private val _dishDetailFlow = MutableStateFlow(dishDetail)
    val dishDetailFlow: StateFlow<Dish> get() = _dishDetailFlow

    private val spacecraftDetail: Target by mutableStateOf(Target())
    private val _spacecraftDetailFlow = MutableStateFlow(spacecraftDetail)
    val spacecraftDetailFlow: StateFlow<Target> get() = _spacecraftDetailFlow

    init {
        println(">>> MainViewModel init() ...")
        viewModelScope.launch {
            dataRepository.configurationFlow.collect {
                Timber.d(">>> got configuration response")
                if (it.sites.size > 0) {
                    _initiallyLoaded.value = dataRepository.fetchDsnData(it)
                } else {
                    Timber.d(">>> not fetching DSN as config is empty")
                }
            }
        }
        viewModelScope.launch {
            dataRepository.dsnFlow.collect {
                Timber.d(">>> got DSN response")
                if (it.sites.size > 0) {
                    _dsnFlow.value = it
                } else {
                    Timber.d(">>> DSN is empty")
                }
            }
        }
    }

    fun fetchDsnConfiguration() {
        viewModelScope.launch {
//            dataRepository.fetchConfig()
            WorkHandler.createWork(0)
        }
    }

    fun cancelDataUpdates() {
        viewModelScope.launch {
            WorkHandler.cancelWork()
        }
    }

    fun fetchDsnData() {
        viewModelScope.launch {
            dataRepository.fetchDsnData(dataRepository.configurationFlow.value)
        }
    }

    fun updateSiteIndex(index: Int) {
        preferences.lastSiteIndex = index
        _currentSiteIndexFlow.value = index
    }

    fun updateMode(index: Int) {
        println("updateMode: $index")
        preferences.lastModeIndex = index
//        _currentModeIndex.value = index
    }

    fun triggerSpacecraftDetailView(dish: Dish, target: Target) {
        _currentDishFlow.value = dish
        _spacecraftDetailFlow.value = target
    }

    fun triggerDishDetailView(dish: Dish) {
        _dishDetailFlow.value = dish
    }

    fun updateSiteTime(time: String = "") {
        _siteTime.value = time
    }
}