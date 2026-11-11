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
import com.example.rtconnect.util.WhatsAppHelper

@Composable
fun BayarIuranScreen(
    onBackClick: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val tagihan = MockData.currentTagihan
    var selectedMethod by remember { mutableStateOf("qris") }

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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total Pembayaran", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTText))
                        Text(
                            text = "Rp${String.format("%,d", tagihan.nominal).replace(',', '.')}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Metode Pembayaran
            Text(
                text = "Pilih Metode Pembayaran",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTText,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentMethodItem(
                title = "QRIS (Semua E-Wallet & M-Banking)",
                subtitle = "GoPay, OVO, Dana, BCA, Mandiri, BRI, dll.",
                isSelected = selectedMethod == "qris",
                onClick = { selectedMethod = "qris" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            PaymentMethodItem(
                title = "Transfer Virtual Account",
                subtitle = "BCA, Mandiri, BNI, BRI",
                isSelected = selectedMethod == "va",
                onClick = { selectedMethod = "va" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            PaymentMethodItem(
                title = "Tunai ke Bendahara RT",
                subtitle = "Bayar langsung ke Pos RT 06",
                isSelected = selectedMethod == "cash",
                onClick = { selectedMethod = "cash" }
            )

            Spacer(modifier = Modifier.height(32.dp))

            RTButton(
                text = "Konfirmasi & Bayar Sekarang",
                onClick = {
                    AppRepository.updateStatusTagihan(tagihan.id, StatusIuran.LUNAS)
                    onPaymentSuccess()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
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
