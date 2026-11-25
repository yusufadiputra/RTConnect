package com.example.rtconnect.ui.screens.warga

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rtconnect.data.model.StatusIuran
import com.example.rtconnect.data.model.TagihanIuran
import com.example.rtconnect.data.repository.AppRepository
import com.example.rtconnect.data.repository.MockData
import com.example.rtconnect.ui.components.RTButton
import com.example.rtconnect.ui.components.RTStatusBadge
import com.example.rtconnect.ui.components.RTTopAppBar
import com.example.rtconnect.ui.theme.*
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.rtconnect.data.network.DuitkuInquiryResponse
import com.example.rtconnect.data.network.DuitkuPaymentService
import com.example.rtconnect.util.NotificationHelper
import com.example.rtconnect.util.WhatsAppHelper
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.io.File

@Composable
fun BayarIuranScreen(
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by AppRepository.currentUser.collectAsState()
    val wargaList by AppRepository.wargaList.collectAsState()
    val currentWarga = wargaList.find {
        it.noHp.replace(" ", "") == currentUser.phoneNumber.replace(" ", "") ||
                it.namaLengkap.equals(currentUser.fullName, ignoreCase = true)
    }
    val tagihanList by AppRepository.tagihanList.collectAsState()
    val rawTagihan = tagihanList.find { it.status == StatusIuran.BELUM_DIBAYAR } ?: MockData.currentTagihan
    val tagihan = if (currentWarga?.customTarifNominal != null) rawTagihan.copy(nominal = currentWarga.customTarifNominal) else rawTagihan

    var selectedMethod by remember { mutableStateOf("qris") }
    var fotoBuktiUri by remember { mutableStateOf<Uri?>(null) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var showQrisSandboxDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isLoadingDuitku by remember { mutableStateOf(false) }
    var duitkuResponse by remember { mutableStateOf<DuitkuInquiryResponse?>(null) }

    // Dialog Loading saat request ke Duitku Sandbox
    if (isLoadingDuitku) {
        Dialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RTWhite),
                modifier = Modifier.padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = RTTeal)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Menghubungkan ke Duitku Sandbox...", color = RTNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Membuat QRIS resmi...", color = RTMuted, fontSize = 11.sp)
                }
            }
        }
    }

    // Launcher: galeri foto bukti
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fotoBuktiUri = uri
    }

    // Launcher: kamera foto bukti
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            fotoBuktiUri = cameraImageUri
        }
    }

    // Launcher: izin kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val photoFile = File.createTempFile("bukti_bayar_", ".jpg", context.externalCacheDir)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    fun openCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            val photoFile = File.createTempFile("bukti_bayar_", ".jpg", context.externalCacheDir)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Dialog Sumber Foto (Kamera / Galeri)
    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Unggah Bukti Bayar Tunai", fontWeight = FontWeight.Bold, color = RTNavy) },
            text = { Text("Pilih sumber foto bukti kuitansi atau tanda terima pembayaran dari pengurus RT.") },
            confirmButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    openCamera()
                }) {
                    Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = RTTeal)
                    Spacer(Modifier.width(6.dp))
                    Text("Kamera", color = RTTeal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPhotoDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, tint = RTMuted)
                    Spacer(Modifier.width(6.dp))
                    Text("Galeri", color = RTMuted)
                }
            },
            containerColor = RTWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog Simulasi Sandbox QRIS Resmi Duitku
    if (showQrisSandboxDialog) {
        Dialog(onDismissRequest = { showQrisSandboxDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = RTWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header QRIS Duitku
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = RTTeal.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "QRIS DUITKU SANDBOX RESMI",
                                color = RTTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(onClick = { showQrisSandboxDialog = false }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Tutup", tint = RTMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "RT 06 CONNECT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = RTNavy,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Merchant: ${DuitkuPaymentService.MERCHANT_CODE} • Kas RT 06",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp)
                    )
                    if (!duitkuResponse?.reference.isNullOrEmpty()) {
                        Text(
                            text = "Ref: ${duitkuResponse?.reference}",
                            style = MaterialTheme.typography.bodySmall.copy(color = RTTeal, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // QR Frame dengan Real QR Generator dari Duitku qrString
                    val qrString = duitkuResponse?.qrString
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, RTTeal, RoundedCornerShape(16.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!qrString.isNullOrEmpty()) {
                            val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&margin=10&data=${URLEncoder.encode(qrString, "UTF-8")}"
                            AsyncImage(
                                model = qrUrl,
                                contentDescription = "QRIS Duitku Sandbox",
                                modifier = Modifier.fillMaxSize().padding(10.dp)
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                CircularProgressIndicator(color = RTTeal, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Menghubungkan ke Duitku QRIS...",
                                    color = RTMuted,
                                    fontSize = 11.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = RTCanvas,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tagihan:", color = RTMuted, fontSize = 12.sp)
                                Text(tagihan.judul, color = RTText, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total:", color = RTMuted, fontSize = 12.sp)
                                Text(
                                    "Rp${String.format("%,d", tagihan.nominal).replace(',', '.')}",
                                    color = RTTeal,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    if (!duitkuResponse?.paymentUrl.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(duitkuResponse!!.paymentUrl))
                                context.startActivity(browserIntent)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(38.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Outlined.OpenInBrowser, contentDescription = null, tint = RTNavy, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Buka Simulator Pembayaran Duitku", fontSize = 11.sp, color = RTNavy, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tombol Simulasi Pembayaran Sukses Sandbox - teks terlihat utuh
                    Button(
                        onClick = {
                            showQrisSandboxDialog = false
                            AppRepository.updateStatusTagihan(tagihan.id, StatusIuran.LUNAS)
                            NotificationHelper.showPaymentNotification(
                                context,
                                title = "Pembayaran QRIS Berhasil!",
                                message = "Tagihan ${tagihan.judul} sebesar Rp${String.format("%,d", tagihan.nominal).replace(',', '.')} telah lunas via Duitku QRIS."
                            )
                            onPaymentSuccess()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 54.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = RTWhite, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Simulasikan Bayar Sukses (Sandbox)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = RTWhite,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { showQrisSandboxDialog = false }) {
                        Text("Batal", color = RTMuted, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "Bayar Iuran",
                showBackButton = true,
                onBackClick = onBackClick,
                showNotification = false
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Rincian Tagihan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Rincian Tagihan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTText,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Jenis Iuran", style = MaterialTheme.typography.bodyMedium.copy(color = RTMuted))
                        Text(text = tagihan.judul, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = RTText))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Periode", style = MaterialTheme.typography.bodyMedium.copy(color = RTMuted))
                        Text(text = tagihan.periode, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = RTText))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Jatuh Tempo", style = MaterialTheme.typography.bodyMedium.copy(color = RTMuted))
                        Text(text = tagihan.batasWaktu, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = RTRed))
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = RTBorder)

                    // Total Pembayaran - pastikan tidak pernah wrap ke baris baru
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Pembayaran",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTText)
                        )
                        Text(
                            text = "Rp${String.format("%,d", tagihan.nominal).replace(',', '.')}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = RTNavy,
                                fontSize = 20.sp
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Metode Pembayaran (HANYA 2 METODE: QRIS & Bayar Langsung ke Pengurus RT)
            Text(
                text = "Pilih Metode Pembayaran",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTText,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Opsi 1: QRIS
            PaymentMethodItem(
                title = "QRIS (Semua E-Wallet & M-Banking)",
                subtitle = "GoPay, OVO, Dana, BCA, Mandiri, BRI, ShopeePay",
                isSelected = selectedMethod == "qris",
                onClick = { selectedMethod = "qris" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Opsi 2: Bayar Langsung ke Pengurus RT (Wajib foto bukti)
            PaymentMethodItem(
                title = "Bayar Langsung ke Pengurus RT",
                subtitle = "Bayar tunai ke Ketua / Bendahara RT 06 (Wajib unggah bukti kuitansi)",
                isSelected = selectedMethod == "tunai_rt",
                onClick = { selectedMethod = "tunai_rt" }
            )

            // Bagian Upload Bukti Foto Khusus Bayar Langsung ke Pengurus RT
            if (selectedMethod == "tunai_rt") {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, if (fotoBuktiUri != null) RTTeal else Color(0xFFF59E0B), RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = if (fotoBuktiUri != null) RTMint.copy(alpha = 0.3f) else Color(0xFFFFFBEB))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (fotoBuktiUri != null) Icons.Outlined.CheckCircle else Icons.Outlined.Info,
                                contentDescription = null,
                                tint = if (fotoBuktiUri != null) RTTeal else Color(0xFFD97706),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Bukti Pembayaran Tunai (Wajib)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (fotoBuktiUri != null) RTNavy else Color(0xFF92400E)
                                )
                            )
                        }

                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Harap lampirkan foto kuitansi atau dokumentasi serah terima pembayaran ke Ketua / Bendahara RT 06.",
                            style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp)
                        )

                        Spacer(Modifier.height(12.dp))

                        if (fotoBuktiUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, RTTeal, RoundedCornerShape(10.dp))
                                    .background(RTWhite)
                            ) {
                                AsyncImage(
                                    model = fotoBuktiUri,
                                    contentDescription = "Foto Bukti Pembayaran",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { fotoBuktiUri = null },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Hapus foto",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showPhotoDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RTTeal),
                                color = RTWhite
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AddAPhoto,
                                        contentDescription = null,
                                        tint = RTTeal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Ambil Foto / Pilih dari Galeri",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = RTTeal,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Tombol Aksi Pembayaran
            val isBayarEnabled = selectedMethod == "qris" || (selectedMethod == "tunai_rt" && fotoBuktiUri != null)

            RTButton(
                text = when {
                    selectedMethod == "qris" -> "Konfirmasi & Buka QRIS (Sandbox)"
                    fotoBuktiUri != null -> "Kirim Bukti Pembayaran ke Pengurus RT"
                    else -> "Wajib Unggah Bukti Foto Terlebih Dahulu"
                },
                enabled = isBayarEnabled,
                onClick = {
                    if (selectedMethod == "qris") {
                        coroutineScope.launch {
                            isLoadingDuitku = true
                            val orderId = "RT06-${System.currentTimeMillis()}"
                            val result = DuitkuPaymentService.createQrisTransaction(
                                orderId = orderId,
                                amount = tagihan.nominal,
                                customerName = currentUser.fullName.ifEmpty { "Warga RT 06" },
                                customerPhone = currentUser.phoneNumber.ifEmpty { "081234567891" },
                                customerEmail = currentUser.email.ifEmpty { "warga@rtconnect.id" },
                                productDetails = tagihan.judul,
                                paymentMethod = "SP"
                            )
                            isLoadingDuitku = false
                            val response = result.getOrNull()
                            if (response != null && !response.qrString.isNullOrEmpty()) {
                                duitkuResponse = response
                                showQrisSandboxDialog = true
                            } else {
                                val errorMsg = result.exceptionOrNull()?.message ?: "Gagal mendapatkan QRIS dari Duitku"
                                android.widget.Toast.makeText(context, errorMsg, android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        // Bayar langsung ke RT dengan foto bukti -> status LUNAS terverifikasi
                        AppRepository.updateStatusTagihan(tagihan.id, StatusIuran.LUNAS)
                        NotificationHelper.showPaymentNotification(
                            context,
                            title = "Bukti Pembayaran Diterima!",
                            message = "Bukti pembayaran tunai untuk ${tagihan.judul} telah dikirim ke Pengurus RT dan status tagihan diperbarui."
                        )
                        onPaymentSuccess()
                    }
                }
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun PaymentMethodItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) RTTeal else RTBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .background(if (isSelected) RTMint.copy(alpha = 0.4f) else RTWhite)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = RTTeal)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = RTText,
                    fontSize = 14.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = RTMuted,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun PembayaranBerhasilScreen(
    onDoneClick: () -> Unit
) {
    var showKuitansi by remember { mutableStateOf(false) }
    val latestPaid = remember {
        AppRepository.tagihanList.value.firstOrNull { it.status == StatusIuran.LUNAS }
            ?: MockData.currentTagihan.copy(status = StatusIuran.LUNAS)
    }

    if (showKuitansi) {
        KuitansiPembayaranDialog(tagihan = latestPaid, onDismiss = { showKuitansi = false })
    }

    Scaffold(containerColor = RTCanvas) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(45.dp))
                    .background(RTMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = RTTeal,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pembayaran Berhasil!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTText,
                    fontSize = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Iuran Sampah RT 06 bulan November 2026 sebesar Rp20.000 telah lunas tercatat di sistem RT CONNECT.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = RTMuted,
                    fontSize = 14.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { showKuitansi = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RTTeal, contentColor = RTWhite)
            ) {
                Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Lihat Kuitansi Sah Digital", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onDoneClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Kembali ke Beranda", color = RTText, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun RiwayatIuranScreen(
    onBackClick: () -> Unit = {}
) {
    val tagihanList by AppRepository.tagihanList.collectAsState()
    val riwayat = tagihanList.filter { it.status == StatusIuran.LUNAS }
    var selectedKuitansi by remember { mutableStateOf<TagihanIuran?>(null) }

    if (selectedKuitansi != null) {
        KuitansiPembayaranDialog(
            tagihan = selectedKuitansi!!,
            onDismiss = { selectedKuitansi = null }
        )
    }

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "Riwayat Iuran",
                subtitle = "Catatan pembayaran keluarga",
                showBackButton = false,
                showNotification = false
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Daftar Transaksi (Ketuk untuk Kuitansi)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = RTText,
                        fontSize = 16.sp
                    )
                )
            }

            items(riwayat) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, RTBorder, RoundedCornerShape(14.dp))
                        .clickable { selectedKuitansi = item },
                    colors = CardDefaults.cardColors(containerColor = RTWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.judul,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RTText,
                                    fontSize = 15.sp
                                )
                            )
                            RTStatusBadge(statusIuran = item.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Periode ${item.periode} • Bayar via ${item.metodeBayar ?: "QRIS"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = RTMuted,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tanggal: ${item.tanggalBayar ?: item.batasWaktu}",
                                style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rp${String.format("%,d", item.nominal).replace(',', '.')}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = RTNavy,
                                        fontSize = 16.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "Lihat Kuitansi",
                                    tint = RTTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// -----------------------------------------------------------------------------
// KUITANSI PEMBAYARAN DIGITAL SAH RT 06
// -----------------------------------------------------------------------------
@Composable
fun KuitansiPembayaranDialog(
    tagihan: TagihanIuran,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val refNumber = "RTC-06-${tagihan.id.take(8).uppercase()}"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = RTWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(RTMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = RTTeal, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "KUITANSI SAH PEMBAYARAN",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = RTMuted, letterSpacing = 1.sp)
                )
                Text(
                    text = "RT 06 / RW 04 KELURAHAN",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 16.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Badge Stempel Lunas
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RTMint)
                        .border(1.dp, RTTeal, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "★ LUNAS TERCATAT SISTEM ★",
                        style = MaterialTheme.typography.labelSmall.copy(color = RTTeal, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = RTBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Rincian
                KuitansiRow("No. Referensi", refNumber)
                KuitansiRow("Jenis Iuran", tagihan.judul)
                KuitansiRow("Periode", tagihan.periode)
                KuitansiRow("Metode Bayar", tagihan.metodeBayar ?: "QRIS / Transfer")
                KuitansiRow("Tanggal Lunas", tagihan.tanggalBayar ?: "Hari ini")
                KuitansiRow("Petugas Pengurus", tagihan.namaPetugasPencatat ?: "Bendahara RT 06")

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = RTBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "TOTAL DIBAYAR", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RTText))
                    Text(
                        text = "Rp${String.format("%,d", tagihan.nominal).replace(',', '.')}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = RTNavy, fontSize = 20.sp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tombol Bagikan ke WhatsApp
                Button(
                    onClick = {
                        val receiptText = """
                            *KUITANSI SAH PEMBAYARAN IURAN RT 06*
                            ------------------------------------
                            No. Ref: $refNumber
                            Jenis: ${tagihan.judul}
                            Periode: ${tagihan.periode}
                            Nominal: Rp${String.format("%,d", tagihan.nominal).replace(',', '.')}
                            Status: LUNAS TERCATAT
                            Metode: ${tagihan.metodeBayar ?: "Transfer / QRIS"}
                            Tanggal: ${tagihan.tanggalBayar ?: "Hari ini"}
                            ------------------------------------
                            _Diterbitkan secara digital oleh Sistem RT CONNECT RT 06 / RW 04._
                        """.trimIndent()
                        WhatsAppHelper.shareTextReceipt(context, "Kuitansi Iuran RT 06", receiptText)
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RTWhatsApp, contentColor = RTWhite)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Bagikan Kuitansi (WhatsApp)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Tutup Kuitansi", color = RTText, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun KuitansiRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(color = RTText, fontWeight = FontWeight.SemiBold, fontSize = 12.sp))
    }
}
