package net.edara.edaracash.features.home.model

import retrofit2.http.GET

interface UserProfileApi {
    @GET("/api/Auth/GetProfile")
    suspend fun getUserProfile(): UserProfileReponse
}