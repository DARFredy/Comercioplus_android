package com.example.comercioplus

import com.example.comercioplus.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface ApiService {

    // --- PRODUCTOS ---
    @GET("products")
    suspend fun getProducts(@Query("cb") cacheBuster: String): PaginatedResponse<Product>

    @POST("products")
    suspend fun addProduct(@Body product: Product): Response<ApiResponse<Product>>

    @POST("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String, 
        @Body product: Product, 
        @Header("X-HTTP-Method-Override") method: String = "PUT"
    ): Response<ApiResponse<Product>>

    @POST("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String, 
        @Header("X-HTTP-Method-Override") method: String = "DELETE"
    ): Response<ApiResponse<Unit>>

    @Multipart
    @POST("uploads/products")
    suspend fun uploadProductImage(
        @Part image: MultipartBody.Part,
        @Header("X-HTTP-Method-Override") method: String? = null
    ): Response<ImageUploadResponse>

    @POST("products/{id}/rate")
    suspend fun rateProduct(
        @Path("id") productId: Long,
        @Body ratingRequest: RatingRequest
    ): Response<ApiResponse<Unit>>

    // --- TIENDA ---
    @GET("stores")
    suspend fun getStores(@Query("cb") cacheBuster: String): PaginatedResponse<Store>

    @GET("stores/{id}")
    suspend fun getStoreById(@Path("id") id: Long, @Query("cb") cacheBuster: String): Response<Store>

    @GET("my/store")
    suspend fun getMyStore(@Query("cb") cacheBuster: String): Response<Store>

    @GET("my/profile")
    suspend fun getMyProfile(): Response<UserProfile>

    @Multipart
    @POST("stores")
    suspend fun createStore(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part cover: MultipartBody.Part? = null,
        @Part logo: MultipartBody.Part? = null
    ): Response<Store>

    @Multipart
    @POST("stores/{id}")
    suspend fun updateStoreWithImages(
        @Path("id") id: Long,
        @Part("_method") method: RequestBody,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part cover: MultipartBody.Part? = null,
        @Part logo: MultipartBody.Part? = null
    ): Response<Store>

    @POST("stores/{id}/rate")
    suspend fun rateStore(
        @Path("id") storeId: Long,
        @Body ratingRequest: RatingRequest
    ): Response<ApiResponse<Unit>>

    // --- IA Y REPORTES ---
    @GET("reports/ai-summary")
    suspend fun getAiSummary(@Query("cb") cb: String): Response<AiReportResponse>

    @POST("products/adjust-tax")
    suspend fun adjustIVA(@Body request: IVARequest): Response<ApiResponse<Unit>>

    @GET("products/sku/{sku}")
    suspend fun getProductBySku(@Path("sku") sku: String): Response<Product>

    // --- OTROS ---
    @GET("categories")
    suspend fun getCategories(): List<Category>

    @POST("categories")
    suspend fun addCategory(@Body request: CategoryRequest): Response<ApiResponse<Category>>

    @POST("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body category: Category,
        @Header("X-HTTP-Method-Override") method: String = "PUT"
    ): Response<ApiResponse<Category>>

    @POST("categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: String,
        @Header("X-HTTP-Method-Override") method: String = "DELETE"
    ): Response<ApiResponse<Unit>>

    @POST("orders")
    suspend fun createOrder(@Body order: Order): Response<ApiResponse<Order>>

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<LoginResponse>

    @POST("auth/google")
    suspend fun googleLogin(@Body idToken: Map<String, String>): Response<LoginResponse>
}

@Serializable
data class AiReportResponse(
    val sales_total: Double = 0.0,
    val tax_total: Double = 0.0,
    val ai_analysis: String = "",
    val recommendations: List<String> = emptyList()
)

@Serializable
data class IVARequest(val percentage: Double)

@Serializable
data class RatingRequest(
    val rating: Int,
    val comment: String? = null
)

@Serializable
data class ImageUploadResponse(
    val data: UploadedImageData? = null,
    val message: String? = null
) {
    fun getAnyUrl(): String = data?.url ?: data?.path ?: data?.secureUrl ?: ""
}

@Serializable
data class UploadedImageData(
    val url: String? = null,
    val path: String? = null,
    @SerialName("secure_url") val secureUrl: String? = null
)
