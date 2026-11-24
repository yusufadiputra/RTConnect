package com.example.rtconnect.data.network

import com.google.gson.annotations.SerializedName

data class FamilyDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("kk_number")
    val kkNumber: String,

    @SerializedName("head_of_family_name")
    val headOfFamilyName: String,

    @SerializedName("address_block")
    val addressBlock: String,

    @SerializedName("rt_rw")
    val rtRw: String? = "006/004",

    @SerializedName("economic_status")
    val economicStatus: String = "REGULER", // REGULER, SUBSIDI, PENGUSAHA

    @SerializedName("member_count")
    val memberCount: Int = 1,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("is_active")
    val isActive: Boolean = true,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class WasteCategoryDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean = true
)

data class ReportDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("reporter_id")
    val reporterId: String? = null,

    @SerializedName("family_id")
    val familyId: String? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("category")
    val category: String, // SAMPAH, KEKERINGAN, KEAMANAN, KEBERSIHAN, LAINNYA

    @SerializedName("description")
    val description: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("photo_url")
    val photoUrl: String? = null,

    @SerializedName("status")
    val status: String = "BARU", // BARU, DIPROSES, SELESAI, DITOLAK

    @SerializedName("created_at")
    val createdAt: String? = null
)

data class AnnouncementDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("author_id")
    val authorId: String? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("event_date")
    val eventDate: String? = null,

    @SerializedName("event_time")
    val eventTime: String? = null,

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("photo_url")
    val photoUrl: String? = null,

    @SerializedName("is_important")
    val isImportant: Boolean = false,

    @SerializedName("is_published")
    val isPublished: Boolean = true
)

data class WasteBillDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("family_id")
    val familyId: String? = null,

    @SerializedName("category_id")
    val categoryId: String? = null,

    @SerializedName("period_month")
    val periodMonth: Int,

    @SerializedName("period_year")
    val periodYear: Int,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("due_date")
    val dueDate: String,

    @SerializedName("status")
    val status: String = "BELUM_DIBAYAR"
)

data class SignUpRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("data")
    val data: Map<String, @JvmSuppressWildcards Any?>? = null
)

data class SignUpResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("role")
    val role: String? = null
)

data class SignInRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

data class SignInResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,

    @SerializedName("token_type")
    val tokenType: String? = null,

    @SerializedName("expires_in")
    val expiresIn: Int? = null,

    @SerializedName("refresh_token")
    val refreshToken: String? = null,

    @SerializedName("user")
    val user: SupabaseUserDto? = null
)

data class SupabaseUserDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("user_metadata")
    val userMetadata: Map<String, @JvmSuppressWildcards Any?>? = null
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)
