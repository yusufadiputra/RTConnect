package com.example.rtconnect.data.network

import retrofit2.Response
import retrofit2.http.*

interface SupabaseApiService {

    // =========================================================================
    // 1. FAMILIES (DATA WARGA / KELUARGA RT 06) - CRUD
    // =========================================================================
    @GET("rest/v1/families?select=*&order=created_at.desc")
    suspend fun getFamilies(): List<FamilyDto>

    @POST("rest/v1/families")
    suspend fun createFamily(
        @Body family: FamilyDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<FamilyDto>

    @PATCH("rest/v1/families")
    suspend fun updateFamily(
        @Query("id") idFilter: String, // format: "eq.<UUID>"
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<Unit>

    @DELETE("rest/v1/families")
    suspend fun deleteFamily(
        @Query("id") idFilter: String // format: "eq.<UUID>"
    ): Response<Unit>

    // =========================================================================
    // 2. WASTE CATEGORIES (KATEGORI IURAN SAMPAH) - CRUD
    // =========================================================================
    @GET("rest/v1/waste_categories?select=*&order=amount.asc")
    suspend fun getWasteCategories(): List<WasteCategoryDto>

    @POST("rest/v1/waste_categories")
    suspend fun createWasteCategory(
        @Body category: WasteCategoryDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<WasteCategoryDto>

    @PATCH("rest/v1/waste_categories")
    suspend fun updateWasteCategory(
        @Query("id") idFilter: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<Unit>

    @DELETE("rest/v1/waste_categories")
    suspend fun deleteWasteCategory(
        @Query("id") idFilter: String
    ): Response<Unit>

    // =========================================================================
    // 3. WASTE BILLS (TAGIHAN IURAN BULANAN) - CRUD
    // =========================================================================
    @GET("rest/v1/waste_bills?select=*&order=created_at.desc")
    suspend fun getWasteBills(): List<WasteBillDto>

    @POST("rest/v1/waste_bills")
    suspend fun createWasteBill(
        @Body bill: WasteBillDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<WasteBillDto>

    @PATCH("rest/v1/waste_bills")
    suspend fun updateWasteBill(
        @Query("id") idFilter: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<Unit>

    // =========================================================================
    // 4. REPORTS (LAPORAN & PENGADUAN WARGA) - CRUD
    // =========================================================================
    @GET("rest/v1/reports?select=*&order=created_at.desc")
    suspend fun getReports(): List<ReportDto>

    @POST("rest/v1/reports")
    suspend fun createReport(
        @Body report: ReportDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<ReportDto>

    @PATCH("rest/v1/reports")
    suspend fun updateReportStatus(
        @Query("id") idFilter: String, // format: "eq.<UUID>"
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<Unit>

    @DELETE("rest/v1/reports")
    suspend fun deleteReport(
        @Query("id") idFilter: String // format: "eq.<UUID>"
    ): Response<Unit>

    // =========================================================================
    // 5. ANNOUNCEMENTS (PENGUMUMAN RT) - CRUD
    // =========================================================================
    @GET("rest/v1/announcements?select=*&order=created_at.desc")
    suspend fun getAnnouncements(): List<AnnouncementDto>

    @POST("rest/v1/announcements")
    suspend fun createAnnouncement(
        @Body announcement: AnnouncementDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<AnnouncementDto>

    @PATCH("rest/v1/announcements")
    suspend fun updateAnnouncement(
        @Query("id") idFilter: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<Unit>

    @DELETE("rest/v1/announcements")
    suspend fun deleteAnnouncement(
        @Query("id") idFilter: String
    ): Response<Unit>

    // =========================================================================
    // 6. PAYMENTS (PEMBAYARAN IURAN) - CRUD
    // =========================================================================
    @GET("rest/v1/payments?select=*&order=created_at.desc")
    suspend fun getPayments(): List<PaymentDto>

    @POST("rest/v1/payments")
    suspend fun createPayment(
        @Body payment: PaymentDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<PaymentDto>

    // =========================================================================
    // 7. SOCIAL ASSISTANCE (PROGRAM & PENERIMA BANSOS) - CRUD
    // =========================================================================
    @GET("rest/v1/social_assistance_programs?select=*&order=created_at.desc")
    suspend fun getSocialAssistancePrograms(): List<SocialAssistanceProgramDto>

    @POST("rest/v1/social_assistance_programs")
    suspend fun createSocialAssistanceProgram(
        @Body program: SocialAssistanceProgramDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<SocialAssistanceProgramDto>

    @PATCH("rest/v1/social_assistance_programs")
    suspend fun updateSocialAssistanceProgram(
        @Query("id") idFilter: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<Unit>

    @DELETE("rest/v1/social_assistance_programs")
    suspend fun deleteSocialAssistanceProgram(
        @Query("id") idFilter: String
    ): Response<Unit>

    @GET("rest/v1/social_assistance_recipients?select=*&order=created_at.desc")
    suspend fun getSocialAssistanceRecipients(): List<SocialAssistanceRecipientDto>

    @POST("rest/v1/social_assistance_recipients")
    suspend fun createSocialAssistanceRecipient(
        @Body recipient: SocialAssistanceRecipientDto,
        @Header("Prefer") prefer: String = "return=representation"
    ): List<SocialAssistanceRecipientDto>

    @PATCH("rest/v1/social_assistance_recipients")
    suspend fun updateSocialAssistanceRecipient(
        @Query("id") idFilter: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<Unit>

    @DELETE("rest/v1/social_assistance_recipients")
    suspend fun deleteSocialAssistanceRecipient(
        @Query("id") idFilter: String
    ): Response<Unit>

    // =========================================================================
    // 8. AUTH & USER REGISTRATION (SUPABASE AUTH)
    // =========================================================================
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    @POST("auth/v1/token")
    suspend fun signInWithPassword(
        @Query("grant_type") grantType: String = "password",
        @Body request: SignInRequest
    ): Response<SignInResponse>

    @POST("auth/v1/token")
    suspend fun refreshToken(
        @Query("grant_type") grantType: String = "refresh_token",
        @Body request: RefreshTokenRequest
    ): Response<SignInResponse>
}
