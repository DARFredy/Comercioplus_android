package com.example.comercioplus.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val product: Product,
    var quantity: Int
)
