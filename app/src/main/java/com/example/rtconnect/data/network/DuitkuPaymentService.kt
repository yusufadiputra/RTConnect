package com.example.rtconnect.data.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

data class DuitkuInquiryRequest(
    @SerializedName("merchantCode") val merchantCode: String,
    @SerializedName("paymentAmount") val paymentAmount: Long,
    @SerializedName("merchantOrderId") val merchantOrderId: String,
    @SerializedName("productDetails") val productDetails: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("additionalParam") val additionalParam: String = "",
    @SerializedName("paymentMethod") val paymentMethod: String = "SP", // ShopeePay QRIS
    @SerializedName("signature") val signature: String,
    @SerializedName("customerVaName") val customerVaName: String,
    @SerializedName("callbackUrl") val callbackUrl: String = "https://rtconnect.id/callback",
    @SerializedName("returnUrl") val returnUrl: String = "https://rtconnect.id/return",
    @SerializedName("expiryPeriod") val expiryPeriod: Int = 60
)

data class DuitkuInquiryResponse(
    @SerializedName("merchantCode") val merchantCode: String? = null,
    @SerializedName("reference") val reference: String? = null,
    @SerializedName("paymentUrl") val paymentUrl: String? = null,
    @SerializedName("qrString") val qrString: String? = null,
    @SerializedName("vaNumber") val vaNumber: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("statusCode") val statusCode: String? = null,
    @SerializedName("statusMessage") val statusMessage: String? = null
)

object DuitkuPaymentService {
    private const val TAG = "DuitkuPayment"
    const val MERCHANT_CODE = "DS38519"
    const val API_KEY = "a24f2a2d7845567e4780919ea5f0c7ef"
    private const val SANDBOX_INQUIRY_URL = "https://sandbox.duitku.com/webapi/api/merchant/v2/inquiry"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    fun generateSignature(merchantCode: String, orderId: String, amount: Long, apiKey: String): String {
        val raw = "$merchantCode$orderId$amount$apiKey"
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(raw.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun createQrisTransaction(
        orderId: String,
        amount: Long,
        customerName: String,
        customerPhone: String = "081234567891",
        customerEmail: String = "warga@rtconnect.id",
        productDetails: String = "Iuran Sampah RT 06",
        paymentMethod: String = "SP"
    ): Result<DuitkuInquiryResponse> = withContext(Dispatchers.IO) {
        try {
            val signature = generateSignature(MERCHANT_CODE, orderId, amount, API_KEY)
            val requestDto = DuitkuInquiryRequest(
                merchantCode = MERCHANT_CODE,
                paymentAmount = amount,
                merchantOrderId = orderId,
                productDetails = productDetails,
                email = customerEmail,
                phoneNumber = customerPhone,
                signature = signature,
                paymentMethod = paymentMethod,
                customerVaName = customerName
            )

            val json = gson.toJson(requestDto)
            val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(SANDBOX_INQUIRY_URL)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            Log.d(TAG, "Duitku Inquiry Response: $responseBody")

            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val parsed = gson.fromJson(responseBody, DuitkuInquiryResponse::class.java)
                if (parsed.statusCode == "00") {
                    Result.success(parsed)
                } else {
                    Result.failure(Exception(parsed.statusMessage ?: "Gagal membuat transaksi Duitku"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error createQrisTransaction", e)
            Result.failure(e)
        }
    }
}

