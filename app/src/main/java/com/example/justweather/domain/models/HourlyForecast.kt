package com.example.justweather.domain.models

import androidx.annotation.DrawableRes

/**
 * A class that contains a forecasted temperature for a particular [hour].
 * @param hour The hour of the forecast.
 * @param isAM Whether the hour is AM or not.
 * @param weatherIconResId The resource ID of the weather icon.
 * @param temperatureString The temperature string containing the temperature
 */
data class HourlyForecast(
    val hour: Int,
    val isAM: Boolean,
    @DrawableRes val weatherIconResId: Int,
    val temperatureString: String
)
