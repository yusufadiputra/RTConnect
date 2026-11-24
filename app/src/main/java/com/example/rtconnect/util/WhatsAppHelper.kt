package com.example.rtconnect.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object WhatsAppHelper {

    fun formatPhoneNumber(phone: String): String {
        val cleanNumber = phone.replace(Regex("[^0-9]"), "")
        return when {
            cleanNumber.startsWith("0") -> "62" + cleanNumber.substring(1)
            cleanNumber.startsWith("62") -> cleanNumber
            cleanNumber.startsWith("8") -> "62$cleanNumber"
            else -> cleanNumber
        }
    }

    fun openWhatsApp(context: Context, phoneNumber: String, message: String = "") {
        val formatted = formatPhoneNumber(phoneNumber)
        if (formatted.length < 9) {
            Toast.makeText(context, "Nomor telepon tidak valid", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val encodedMessage = if (message.isNotBlank()) URLEncoder.encode(message, "UTF-8") else ""
            val url = if (encodedMessage.isNotBlank()) {
                "https://wa.me/$formatted?text=$encodedMessage"
            } else {
                "https://wa.me/$formatted"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membuka WhatsApp: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareTextReceipt(context: Context, title: String, content: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val chooser = Intent.createChooser(intent, "Bagikan Kuitansi").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membagikan kuitansi: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
