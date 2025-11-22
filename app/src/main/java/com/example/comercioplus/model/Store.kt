package com.example.comercioplus.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val slug: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("logo_url") val logoUrl: String? = null,
    val rating: Double? = null,
    @SerialName("products_count") val productsCount: Int = 0,
    val category: String? = "General",
    val verified: Boolean = false
) {
    // Functions removed as they clash with Kotlin-generated getters for logoUrl and coverUrl
    fun getBannerUrl(): String? = coverUrl
}
