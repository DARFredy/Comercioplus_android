package com.comercioplus.app.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Store(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("slug") val slug: String = "",
    @SerializedName("logo_url") val rawLogoUrl: String? = null,
    @SerializedName("banner_url") val rawBannerUrl: String? = null,
    @SerializedName("logoImage") val logoImage: String? = null,
    @SerializedName("bannerImage") val bannerImage: String? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("products_count") val productsCount: Int = 0,
    @SerializedName("_count") val count: StoreCount? = null,
    @SerializedName("followers_count") val followersCount: Int = 0,
    @SerializedName("category") val category: String? = "General",
    @SerializedName("verified") val verified: Boolean = false,
    @SerializedName("owner_id") val ownerId: String? = null,
    @SerializedName("ai_metadata") val aiMetadata: Map<String, String>? = null
) : Parcelable {
    val logoUrl: String? get() = rawLogoUrl ?: logoImage
    val bannerUrl: String? get() = rawBannerUrl ?: bannerImage
    val resolvedLogoUrl: String? get() = rawLogoUrl ?: logoImage
    val resolvedBannerUrl: String? get() = rawBannerUrl ?: bannerImage
    fun getProductCount(): Int = count?.products ?: productsCount
}

@Parcelize
data class Product(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("images") val images: List<String>? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("store_id") val storeId: String = "",
    @SerializedName("stock") val stock: Int? = null,
    @SerializedName("slug") val slug: String? = null,
    @SerializedName("condition") val condition: String? = "New",
    
    // --- INTELIGENCIA DE MERCADO GLOBAL ---
    @SerializedName("technical_specs") val specs: Map<String, String>? = null,
    @SerializedName("security_description") val securityDescription: String? = null,
    @SerializedName("digital_file_url") val digitalFileUrl: String? = null,
    @SerializedName("is_digital") val isDigital: Boolean = false,
    @SerializedName("license_type") val licenseType: String? = "Standard",
    
    // --- FLASH SALES (VENTAS RÁPIDAS) ---
    @SerializedName("flash_sale_price") val flashSalePrice: Double? = null,
    @SerializedName("flash_sale_end") val flashSaleEnd: Long? = null, // Timestamp
    @SerializedName("is_flash_sale") val isFlashSale: Boolean = false
) : Parcelable {
    fun getImage(): String? = imageUrl ?: images?.firstOrNull()
}

@Parcelize
data class BotConfig(
    @SerializedName("custom_image_url") val customImageUrl: String? = null,
    @SerializedName("bot_style") val botStyle: String = "CHIBI", // CHIBI, FULL_BODY, ANIMAL
    @SerializedName("has_limbs") val hasLimbs: Boolean = false,
    @SerializedName("animal_type") val animalType: String? = null,
    @SerializedName("name") val botName: String = "ComercioPlusBot"
) : Parcelable

@Parcelize
data class DirectMessage(
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("receiver_id") val receiverId: String,
    @SerializedName("content") val content: String,
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @SerializedName("ai_mood") val aiMood: String = "NEUTRAL",
    @SerializedName("is_verified") val isVerified: Boolean = false
) : Parcelable

@Parcelize
data class Dispute(
    @SerializedName("id") val id: String = "",
    @SerializedName("order_id") val orderId: String,
    @SerializedName("buyer_id") val buyerId: String,
    @SerializedName("seller_id") val sellerId: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("status") val status: String = "OPEN",
    @SerializedName("ai_resolution") val aiResolution: String? = null,
    @SerializedName("refund_amount") val refundAmount: Double = 0.0
) : Parcelable

@Parcelize
data class StoreCount(
    @SerializedName("products") val products: Int = 0,
    @SerializedName("followers") val followers: Int = 0
) : Parcelable

data class StoresResponse(
    @SerializedName("stores") val stores: List<Store>? = null,
    @SerializedName("data") val data: List<Store>? = null,
    @SerializedName("total") val total: Int? = null
) {
    val resolvedStores: List<Store> get() = stores ?: data ?: emptyList()
}

data class ProductsResponse(
    @SerializedName("products") val products: List<Product>? = null,
    @SerializedName("data") val data: List<Product>? = null,
    @SerializedName("total") val total: Int? = null
) {
    val resolvedProducts: List<Product> get() = products ?: data ?: emptyList()
}

@Parcelize
data class CartItem(
    val product: Product,
    var quantity: Int = 1
) : Parcelable
