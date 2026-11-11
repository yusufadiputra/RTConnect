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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.data.model.TagihanIuran
import com.example.rtconnect.data.repository.MockData
import com.example.rtconnect.ui.components.RTButton
import com.example.rtconnect.ui.components.RTStatusBadge
import com.example.rtconnect.ui.components.RTTopAppBar
import com.example.rtconnect.ui.theme.*

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
                subtitle = "Bayar langsung ke Pos RT 10",
                isSelected = selectedMethod == "cash",
                onClick = { selectedMethod = "cash" }
            )

            Spacer(modifier = Modifier.height(32.dp))

            RTButton(
                text = "Konfirmasi & Bayar Sekarang",
                onClick = onPaymentSuccess
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
                text = "Iuran Sampah RT 10 bulan November 2026 sebesar Rp20.000 telah lunas tercatat di sistem RT CONNECT.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = RTMuted,
                    fontSize = 14.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            RTButton(
                text = "Kembali ke Beranda",
                onClick = onDoneClick
            )
        }
    }
}

@Composable
fun RiwayatIuranScreen(
    onBackClick: () -> Unit = {}
) {
    val riwayat = MockData.riwayatTagihan

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
                    text = "Daftar Transaksi",
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
                        .border(1.dp, RTBorder, RoundedCornerShape(14.dp)),
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
                            Text(
                                text = "Rp${String.format("%,d", item.nominal).replace(',', '.')}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RTNavy,
                                    fontSize = 16.sp
                                )
                            )
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
