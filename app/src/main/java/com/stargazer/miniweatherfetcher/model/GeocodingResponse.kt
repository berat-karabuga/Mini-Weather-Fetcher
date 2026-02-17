package com.stargazer.miniweatherfetcher.model

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponse(
    val results: List<LocationResult>? = null
)

@Serializable
data class LocationResult(
    val name: String,
    val country: String? = null,
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double
)