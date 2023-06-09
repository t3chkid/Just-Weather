package com.example.justweather.domain.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A data class that represents the precipitation probability.
 */
data class PrecipitationProbability(
    val latitude: String,
    val longitude: String,
    val dateTime: LocalDateTime,
    val probabilityPercentage: Int
)


