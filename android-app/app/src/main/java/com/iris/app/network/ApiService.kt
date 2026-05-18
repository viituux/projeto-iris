package com.iris.app.network

import com.iris.app.data.ApiEnvelope
import com.iris.app.data.CreateContactRequest
import com.iris.app.data.EmergencyContact
import com.iris.app.data.LocationPoint
import com.iris.app.data.LoginRequest
import com.iris.app.data.LoginResponse
import com.iris.app.data.SosRequest
import com.iris.app.data.UserProfile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("api/users/login/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/contacts/")
    fun getContacts(): Call<ApiEnvelope<List<EmergencyContact>>>

    @POST("api/contacts/")
    fun createContact(@Body request: CreateContactRequest): Call<ApiEnvelope<EmergencyContact>>

    @GET("api/locations/")
    fun getLocations(): Call<ApiEnvelope<List<LocationPoint>>>

    @Multipart
    @POST("api/emergencies/activate/")
    fun activateSos(
        @Part("description") description: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part audio: MultipartBody.Part?
    ): Call<Map<String, Any>>

    @GET("api/users/me/")
    fun getMe(): Call<UserProfile>
}
