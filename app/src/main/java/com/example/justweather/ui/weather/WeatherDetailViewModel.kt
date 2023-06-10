package com.example.justweather.ui.weather

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justweather.data.repositories.weather.WeatherRepository
import com.example.justweather.domain.models.WeatherDetails
import com.example.justweather.ui.navigation.JustWeatherNavigationDestinations
import com.example.justweather.ui.navigation.JustWeatherNavigationDestinations.WeatherDetailScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.milliseconds

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private val latitude: String =
        savedStateHandle[WeatherDetailScreen.NAV_ARG_LATITUDE]!!
    private val longitude: String =
        savedStateHandle[WeatherDetailScreen.NAV_ARG_LONGITUDE]!!

    private val _weatherDetailsOfChosenLocation =
        MutableStateFlow(WeatherDetails.EmptyWeatherDetails)
    val weatherDetailsOfChosenLocation =
        _weatherDetailsOfChosenLocation as StateFlow<WeatherDetails>

    private val initialValueOfIsSavedLocation: String =
        savedStateHandle[WeatherDetailScreen.NAV_ARG_WAS_LOCATION_PREVIOUSLY_SAVED]!!
    val isSavedLocation = weatherRepository.getWeatherStreamForPreviouslySavedLocations()
        .map { savedWeatherDetails ->
            savedWeatherDetails.any { it.nameOfLocation == weatherDetailsOfChosenLocation.value.nameOfLocation }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 250L),
            initialValue = initialValueOfIsSavedLocation.toBoolean()
        )


    private val _uiState = MutableStateFlow(UiState.IDLE)
    val uiState = _uiState as StateFlow<UiState>

    init {
        viewModelScope.launch { fetchWeatherInfo() }
    }

    /**
     * Fetches weather information from the repository, ensuring that the [uiState] is
     * correctly updated.
     */
    private suspend fun fetchWeatherInfo() {
        _uiState.value = UiState.LOADING
        val weatherDetails = weatherRepository.fetchWeatherForLocation(
            latitude = latitude,
            longitude = longitude
        ).getOrNull()?.also { _weatherDetailsOfChosenLocation.value = it }

        _uiState.value = if (weatherDetails == null) UiState.ERROR
        else UiState.IDLE
    }

    fun addLocationToSavedLocations() {
        if (weatherDetailsOfChosenLocation.value == WeatherDetails.EmptyWeatherDetails) return
        viewModelScope.launch {
            _uiState.value = UiState.LOADING
            weatherRepository.saveWeatherLocation(
                nameOfLocation = weatherDetailsOfChosenLocation.value.nameOfLocation,
                latitude = latitude,
                longitude = longitude
            )
            _uiState.value = UiState.IDLE
        }
    }

    /**
     * An enum class that contains all possible UI states.
     */
    enum class UiState {
        IDLE,
        LOADING,
        ERROR
    }
}