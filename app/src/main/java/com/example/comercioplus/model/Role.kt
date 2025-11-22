package com.example.comercioplus.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    @SerialName("guest")
    INVITADO,
    @SerialName("client")
    CLIENTE,
    @SerialName("merchant")
    COMERCIANTE
}
