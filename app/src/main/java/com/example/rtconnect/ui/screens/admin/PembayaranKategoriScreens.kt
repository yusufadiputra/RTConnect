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
                subtitle = "Kelola tagihan & kasir tunai RT 10",
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
                            colors = ButtonDefaults.buttonColors(containerColor = RTTeal),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Catat Tunai", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
    var petugasNama by remember { mutableStateOf("H. Supriyadi (Ketua RT 10)") }

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
                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal)
                    ) {
                        Text("Konfirmasi Lunas", fontWeight = FontWeight.Bold)
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
                    colors = ButtonDefaults.buttonColors(containerColor = RTNavy)
                ) {
                    Text("Tutup Rincian")
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
    var editingKategori by remember { mutableStateOf<KategoriIuran?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Kategori Iuran",
                subtitle = "Kelola tarif iuran sampah RT 10",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
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
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(RTMint).padding(14.dp)
                ) {
                    Text(
                        text = "Kebijakan tarif iuran dapat disesuaikan dengan musyawarah warga RT 10 tanpa merusak riwayat transaksi yang lalu.",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTNavy, fontSize = 12.sp)
                    )
                }
            }

            items(kategoriList, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(1.dp, RTBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = RTWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = item.nama, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 16.sp))
                            Text(
                                text = "Rp${String.format("%,d", item.nominal).replace(',', '.')}/bulan",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTTeal)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = item.deskripsi, style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(
                                onClick = {
                                    editingKategori = item
                                    showFormDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = RTNavy)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Ubah Tarif", fontSize = 11.sp, color = RTNavy)
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
                        colors = ButtonDefaults.buttonColors(containerColor = RTNavy)
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
