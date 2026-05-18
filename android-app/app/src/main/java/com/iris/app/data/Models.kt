package com.iris.app.data

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val access: String,
    val refresh: String
)

data class ApiEnvelope<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

data class EmergencyContact(
    val id: Int,
    val name: String,
    val phone: String,
    val email: String?,
    val relationship: String?,
    val is_primary: Boolean?
)

data class CreateContactRequest(
    val name: String,
    val phone: String,
    val email: String?,
    val relationship: String?,
    val is_primary: Boolean
)

data class LocationPoint(
    val id: Int,
    val name: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?
)

data class SosRequest(
    val description: String,
    val latitude: Double,
    val longitude: Double
)

data class UserProfile(
    val id: Int,
    val email: String,
    val username: String
)
