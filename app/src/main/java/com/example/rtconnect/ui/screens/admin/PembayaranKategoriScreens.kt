package com.example.rtconnect.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rtconnect.data.model.KategoriIuran
import com.example.rtconnect.data.model.StatusIuran
import com.example.rtconnect.data.model.TagihanIuran
import com.example.rtconnect.data.model.Warga
import com.example.rtconnect.data.repository.AppRepository
import com.example.rtconnect.ui.components.AdminTopAppBar
import com.example.rtconnect.ui.components.EmptyStateView
import com.example.rtconnect.ui.components.RTStatusBadge
import com.example.rtconnect.ui.theme.*
import java.util.UUID

@Composable
fun PembayaranSampahScreen(
    onBackClick: () -> Unit
) {
    val tagihanList by AppRepository.tagihanList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<StatusIuran?>(null) }
    var recordingTagihan by remember { mutableStateOf<TagihanIuran?>(null) }
    var viewingTagihan by remember { mutableStateOf<TagihanIuran?>(null) }

    // Summary
    val totalTagihan = tagihanList.size
    val totalLunas = tagihanList.filter { it.status == StatusIuran.LUNAS }.sumOf { it.nominal }
    val totalBelumLunas = tagihanList.filter { it.status == StatusIuran.BELUM_DIBAYAR }.sumOf { it.nominal }

    val filteredList = tagihanList.filter { tagihan ->
        val matchesQuery = tagihan.namaWarga.contains(searchQuery, ignoreCase = true) ||
                tagihan.blokRumah.contains(searchQuery, ignoreCase = true)
        val matchesStatus = selectedStatus == null || tagihan.status == selectedStatus
        matchesQuery && matchesStatus
    }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Pembayaran Sampah",
                subtitle = "Kelola tagihan & kasir tunai RT 06",
                showBack = false
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Rekap Kas Tagihan Bulanan
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(RTNavy),
                colors = CardDefaults.cardColors(containerColor = RTNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "REKAP IURAN BULAN INI", style = MaterialTheme.typography.labelSmall.copy(color = RTMint, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "Terkumpul (Lunas)", style = MaterialTheme.typography.bodySmall.copy(color = RTMint, fontSize = 11.sp))
                            Text(text = "Rp${String.format("%,d", totalLunas).replace(',', '.')}", style = MaterialTheme.typography.titleMedium.copy(color = RTWhite, fontWeight = FontWeight.Bold))
                        }
                        Column {
                            Text(text = "Belum Terbayar", style = MaterialTheme.typography.bodySmall.copy(color = RTMint, fontSize = 11.sp))
                            Text(text = "Rp${String.format("%,d", totalBelumLunas).replace(',', '.')}", style = MaterialTheme.typography.titleMedium.copy(color = RTWarning, fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari warga atau blok rumah...", color = RTMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null, tint = RTMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = RTWhite,
                        unfocusedContainerColor = RTWhite,
                        focusedBorderColor = RTNavy,
                        unfocusedBorderColor = RTBorder
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { selectedStatus = null },
                        label = { Text("Semua (${tagihanList.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatus == StatusIuran.LUNAS,
                        onClick = { selectedStatus = if (selectedStatus == StatusIuran.LUNAS) null else StatusIuran.LUNAS },
                        label = { Text("Lunas") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatus == StatusIuran.BELUM_DIBAYAR,
                        onClick = { selectedStatus = if (selectedStatus == StatusIuran.BELUM_DIBAYAR) null else StatusIuran.BELUM_DIBAYAR },
                        label = { Text("Belum Bayar") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transaction Cards List
            if (filteredList.isEmpty()) {
                EmptyStateView(
                    title = "Tidak Ada Tagihan",
                    message = "Tidak ada transaksi pembayaran yang sesuai dengan kriteria filter.",
                    icon = Icons.Outlined.ReceiptLong
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        TagihanCardItem(
                            tagihan = item,
                            onDetailClick = { viewingTagihan = item },
                            onCatatTunaiClick = { recordingTagihan = item }
                        )
                    }
                }
            }
        }
    }

    // Modal Catat Pembayaran Tunai
    if (recordingTagihan != null) {
        CatatTunaiDialog(
            tagihan = recordingTagihan!!,
            onDismiss = { recordingTagihan = null },
            onConfirm = { petugas ->
                AppRepository.catatPembayaranTunai(recordingTagihan!!.id, petugas)
                recordingTagihan = null
            }
        )
    }

    // Modal Detail Tagihan / Transaksi
    if (viewingTagihan != null) {
        DetailTagihanDialog(
            tagihan = viewingTagihan!!,
            onDismiss = { viewingTagihan = null }
        )
    }
}

@Composable
fun TagihanCardItem(
    tagihan: TagihanIuran,
    onDetailClick: () -> Unit,
    onCatatTunaiClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, RTBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = RTWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tagihan.namaWarga,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 15.sp)
                    )
                    Text(
                        text = "${tagihan.blokRumah} • Kategori ${tagihan.kategoriNama}",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp)
                    )
                }
                RTStatusBadge(statusIuran = tagihan.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Nominal Iuran:", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp))
                    Text(
                        text = "Rp${String.format("%,d", tagihan.nominal).replace(',', '.')}",
                        style = MaterialTheme.typography.titleMedium.copy(color = RTText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDetailClick,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text(text = "Rincian", fontSize = 12.sp, color = RTNavy)
                    }

                    if (tagihan.status == StatusIuran.BELUM_DIBAYAR) {
                        Button(
                            onClick = onCatatTunaiClick,
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RTTeal, contentColor = RTWhite),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = RTWhite)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Catat Tunai", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RTWhite)
                        }
                    }
                }
            }
        }
    }
}

// Dialog Catat Tunai
@Composable
fun CatatTunaiDialog(
    tagihan: TagihanIuran,
    onDismiss: () -> Unit,
    onConfirm: (petugas: String) -> Unit
) {
    var petugasNama by remember { mutableStateOf("H. Supriyadi (Ketua RT 06)") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = RTWhite)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Pencatatan Pembayaran Tunai", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy))
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Konfirmasi bahwa warga berikut telah menyetorkan uang iuran sampah secara tunai ke pengurus RT:",
                    style = MaterialTheme.typography.bodySmall.copy(color = RTMuted)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(RTMint).padding(12.dp)
                ) {
                    Column {
                        Text(text = tagihan.namaWarga, fontWeight = FontWeight.Bold, color = RTNavy)
                        Text(text = "${tagihan.blokRumah} • ${tagihan.periodeNama}", fontSize = 12.sp, color = RTTeal)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Nominal: Rp${String.format("%,d", tagihan.nominal).replace(',', '.')}", fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("Nama Petugas Pengurus RT:")
                OutlinedTextField(
                    value = petugasNama,
                    onValueChange = { petugasNama = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) { Text("Batal") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onConfirm(petugasNama) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal, contentColor = RTWhite)
                    ) {
                        Text("Konfirmasi Lunas", fontWeight = FontWeight.Bold, color = RTWhite)
                    }
                }
            }
        }
    }
}

// Dialog Detail Tagihan
@Composable
fun DetailTagihanDialog(
    tagihan: TagihanIuran,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = RTWhite)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Rincian Transaksi Iuran", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy))
                Spacer(modifier = Modifier.height(14.dp))

                DetailItemRow("ID Tagihan", tagihan.id.take(8))
                DetailItemRow("Nama Kepala Keluarga", tagihan.namaWarga)
                DetailItemRow("Alamat / Blok", tagihan.blokRumah)
                DetailItemRow("Kategori Warga", tagihan.kategoriNama)
                DetailItemRow("Periode", tagihan.periodeNama)
                DetailItemRow("Nominal Iuran", "Rp${String.format("%,d", tagihan.nominal).replace(',', '.')}")
                DetailItemRow("Status", tagihan.status.name)
                if (tagihan.metodeBayar != null) {
                    DetailItemRow("Metode Pembayaran", tagihan.metodeBayar ?: "-")
                }
                if (tagihan.tanggalBayar != null) {
                    DetailItemRow("Waktu Pembayaran", tagihan.tanggalBayar ?: "-")
                }
                if (tagihan.namaPetugasPencatat != null) {
                    DetailItemRow("Pencatat / Kasir RT", tagihan.namaPetugasPencatat ?: "-")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RTNavy, contentColor = RTWhite)
                ) {
                    Text("Tutup Rincian", color = RTWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailItemRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp))
        Text(text = value, style = MaterialTheme.typography.labelSmall.copy(color = RTText, fontWeight = FontWeight.Bold, fontSize = 12.sp))
    }
}

// ---------------------------------------------------------------------------------
// KATEGORI IURAN SCREEN (CRUD Master Kategori)
// ---------------------------------------------------------------------------------
@Composable
fun KategoriIuranScreen(
    onBackClick: () -> Unit
) {
    val kategoriList by AppRepository.kategoriList.collectAsState()
    val wargaList by AppRepository.wargaList.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Kategori Umum, 1: Tarif Khusus Warga
    var editingKategori by remember { mutableStateOf<KategoriIuran?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }

    var selectedWargaForTarif by remember { mutableStateOf<Warga?>(null) }
    var searchWargaQuery by remember { mutableStateOf("") }

    val filteredWarga = wargaList.filter {
        it.namaLengkap.contains(searchWargaQuery, ignoreCase = true) ||
                it.blokRumah.contains(searchWargaQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Kategori & Tarif Iuran",
                subtitle = "Kelola tarif umum & tarif khusus per warga RT 06",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = {
                        editingKategori = null
                        showFormDialog = true
                    },
                    containerColor = RTNavy,
                    contentColor = RTWhite,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kategori")
                }
            }
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Switcher
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = RTWhite,
                contentColor = RTNavy
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Kategori Umum (${kategoriList.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Tarif Khusus Warga (${wargaList.count { it.customTarifNominal != null }})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(RTMint)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Kebijakan tarif iuran dapat disesuaikan dengan musyawarah warga RT 06 tanpa merusak riwayat transaksi yang lalu.",
                                style = MaterialTheme.typography.bodySmall.copy(color = RTNavy, fontSize = 12.sp)
                            )
                        }
                    }

                    items(kategoriList, key = { it.id }) { item ->
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
                                        text = item.nama,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = RTNavy,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = "Rp${String.format("%,d", item.nominal).replace(',', '.')}/bulan",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = RTTeal
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.deskripsi,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = RTMuted,
                                        fontSize = 12.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            editingKategori = item
                                            showFormDialog = true
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = RTNavy
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Ubah Tarif", fontSize = 11.sp, color = RTNavy)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Tab 1: Tarif Khusus per Warga
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(RTMint)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Pak RT dapat menetapkan tarif khusus secara spesifik untuk warga tertentu (misal: luas tanah berbeda, usaha rumahan, atau kesepakatan khusus) yang akan otomatis berlaku di akun warga saat login.",
                            style = MaterialTheme.typography.bodySmall.copy(color = RTNavy, fontSize = 12.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchWargaQuery,
                        onValueChange = { searchWargaQuery = it },
                        placeholder = { Text("Cari warga atau blok...", color = RTMuted, fontSize = 13.sp) },
                        leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null, tint = RTMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = RTWhite,
                            unfocusedContainerColor = RTWhite,
                            focusedBorderColor = RTNavy,
                            unfocusedBorderColor = RTBorder
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredWarga, key = { it.id }) { warga ->
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
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = warga.namaLengkap,
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = RTNavy,
                                                    fontSize = 15.sp
                                                )
                                            )
                                            Text(
                                                text = "${warga.blokRumah} • KK: ${warga.nomorKk}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp)
                                            )
                                        }

                                        if (warga.customTarifNominal != null) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(RTMint)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "Tarif Khusus",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = RTTeal,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            val nominalTampil = warga.customTarifNominal ?: 25000L
                                            Text(
                                                text = "Rp${String.format("%,d", nominalTampil).replace(',', '.')}/bulan",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (warga.customTarifNominal != null) RTTeal else RTNavy,
                                                    fontSize = 16.sp
                                                )
                                            )
                                            if (!warga.catatanTarif.isNullOrBlank()) {
                                                Text(
                                                    text = "Alasan: ${warga.catatanTarif}",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = RTMuted,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            } else {
                                                Text(
                                                    text = if (warga.customTarifNominal != null) "Tarif nominal khusus RT" else "Mengikuti tarif standar",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        color = RTMuted,
                                                        fontSize = 11.sp
                                                    )
                                                )
                                            }
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            if (warga.customTarifNominal != null) {
                                                TextButton(
                                                    onClick = {
                                                        AppRepository.setCustomTarifWarga(warga.id, null, null)
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                    Text("Reset", color = RTRed, fontSize = 11.sp)
                                                }
                                            }
                                            Button(
                                                onClick = { selectedWargaForTarif = warga },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(34.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (warga.customTarifNominal != null) RTNavy else RTTeal,
                                                    contentColor = RTWhite
                                                ),
                                                contentPadding = PaddingValues(horizontal = 10.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(13.dp),
                                                    tint = RTWhite
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (warga.customTarifNominal != null) "Ubah Tarif" else "Atur Tarif",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = RTWhite
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFormDialog) {
        KategoriFormDialog(
            initial = editingKategori,
            onDismiss = { showFormDialog = false },
            onSave = { kat ->
                if (editingKategori == null) {
                    AppRepository.addKategori(kat)
                } else {
                    AppRepository.updateKategori(kat)
                }
                showFormDialog = false
            }
        )
    }

    if (selectedWargaForTarif != null) {
        AturTarifKhususDialog(
            warga = selectedWargaForTarif!!,
            onDismiss = { selectedWargaForTarif = null },
            onSave = { nominal, catatan, tanggalJatuhTempo ->
                AppRepository.setCustomTarifWarga(selectedWargaForTarif!!.id, nominal, catatan, tanggalJatuhTempo)
                selectedWargaForTarif = null
            }
        )
    }
}

// -----------------------------------------------------------------------------
// Dialog Atur Tarif Khusus Warga Tertentu
// -----------------------------------------------------------------------------
@Composable
fun AturTarifKhususDialog(
    warga: Warga,
    onDismiss: () -> Unit,
    onSave: (nominal: Long, catatan: String, tanggalJatuhTempo: Int) -> Unit
) {
    var nominalText by remember { mutableStateOf((warga.customTarifNominal ?: 25000L).toString()) }
    var catatanText by remember { mutableStateOf(warga.catatanTarif ?: "") }
    var tanggalJatuhTempoText by remember { mutableStateOf("15") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = RTWhite)
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(
                    text = "Atur Tarif Khusus Warga",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Menetapkan nominal iuran khusus untuk keluarga ini.",
                    style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(RTMint)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = warga.namaLengkap, fontWeight = FontWeight.Bold, color = RTNavy)
                        Text(text = "${warga.blokRumah} • No KK: ${warga.nomorKk}", fontSize = 12.sp, color = RTTeal)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("Nominal Iuran Khusus (Rp) *")
                OutlinedTextField(
                    value = nominalText,
                    onValueChange = { nominalText = it.filter { char -> char.isDigit() } },
                    placeholder = { Text("Contoh: 35000", fontSize = 13.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormLabel("Tanggal Jatuh Tempo Pembayaran (Setiap Bulan) *")
                OutlinedTextField(
                    value = tanggalJatuhTempoText,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.isEmpty() || (digits.toIntOrNull() ?: 0) in 1..31) {
                            tanggalJatuhTempoText = digits
                        }
                    },
                    placeholder = { Text("Contoh: 15 (artinya tanggal 15 tiap bulan)", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormLabel("Alasan / Catatan Penyesuaian:")
                OutlinedTextField(
                    value = catatanText,
                    onValueChange = { catatanText = it },
                    placeholder = { Text("Contoh: Rumah pojok hook / luas kavling lebih besar", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val nominal = nominalText.toLongOrNull() ?: 25000L
                            val tgl = tanggalJatuhTempoText.toIntOrNull() ?: 15
                            onSave(nominal, catatanText, tgl)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal, contentColor = RTWhite),
                        enabled = nominalText.isNotBlank() && (nominalText.toLongOrNull() ?: 0) > 0
                    ) {
                        Text("Simpan Tarif", fontWeight = FontWeight.Bold, color = RTWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun KategoriFormDialog(
    initial: KategoriIuran?,
    onDismiss: () -> Unit,
    onSave: (KategoriIuran) -> Unit
) {
    var nama by remember { mutableStateOf(initial?.nama ?: "") }
    var nominal by remember { mutableStateOf((initial?.nominal ?: 20000).toString()) }
    var deskripsi by remember { mutableStateOf(initial?.deskripsi ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)), colors = CardDefaults.cardColors(containerColor = RTWhite)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = if (initial == null) "Tambah Kategori Iuran" else "Ubah Tarif Kategori", fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("Nama Kategori:")
                OutlinedTextField(value = nama, onValueChange = { nama = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Nominal Iuran (Rp):")
                OutlinedTextField(value = nominal, onValueChange = { if (it.all { c -> c.isDigit() }) nominal = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Deskripsi:")
                OutlinedTextField(value = deskripsi, onValueChange = { deskripsi = it }, modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(10.dp), maxLines = 3)

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) { Text("Batal") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val k = KategoriIuran(
                                id = initial?.id ?: UUID.randomUUID().toString(),
                                nama = nama,
                                nominal = nominal.toLongOrNull() ?: 20000,
                                deskripsi = deskripsi
                            )
                            onSave(k)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTNavy, contentColor = RTWhite)
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold, color = RTWhite)
                    }
                }
            }
        }
    }
}
