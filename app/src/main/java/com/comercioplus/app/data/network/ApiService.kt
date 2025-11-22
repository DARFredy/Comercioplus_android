package com.comercioplus.app.data.network

import com.comercioplus.app.data.models.Product
import com.comercioplus.app.data.models.ProductsResponse
import com.comercioplus.app.data.models.Store
import com.comercioplus.app.data.models.StoresResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api/stores")
    suspend fun getStores(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("sort") sort: String? = null
    ): Response<StoresResponse>

    @GET("api/stores/{slug}")
    suspend fun getStoreBySlug(@Path("slug") slug: String): Response<Store>

    @GET("stores")
    suspend fun getStoresFallback(
        @Query("search") search: String? = null
    ): Response<List<Store>>

    @GET("api/stores/{slug}/products")
    suspend fun getStoreProducts(
        @Path("slug") slug: String,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("sort") sort: String? = null
    ): Response<ProductsResponse>

    @GET("api/products")
    suspend fun getAllProducts(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null
    ): Response<ProductsResponse>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<Product>
}
