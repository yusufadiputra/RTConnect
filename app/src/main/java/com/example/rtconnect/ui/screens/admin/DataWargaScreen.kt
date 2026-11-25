package com.example.rtconnect.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rtconnect.data.model.StatusAkun
import com.example.rtconnect.data.model.StatusEkonomi
import com.example.rtconnect.data.model.StatusIuran
import com.example.rtconnect.data.model.UserAccount
import com.example.rtconnect.data.model.Warga
import com.example.rtconnect.data.repository.AppRepository
import com.example.rtconnect.ui.components.AdminTopAppBar
import com.example.rtconnect.ui.components.ConfirmationDialog
import com.example.rtconnect.ui.components.EmptyStateView
import com.example.rtconnect.ui.components.RTStatusBadge
import com.example.rtconnect.ui.theme.*
import com.example.rtconnect.util.WhatsAppHelper
import java.util.UUID

@Composable
fun DataWargaScreen(
    onBackClick: () -> Unit
) {
    val wargaList by AppRepository.wargaList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterEkonomi by remember { mutableStateOf<StatusEkonomi?>(null) }

    // Dialog state
    var showFormDialog by remember { mutableStateOf(false) }
    var editingWarga by remember { mutableStateOf<Warga?>(null) }
    var deletingWarga by remember { mutableStateOf<Warga?>(null) }
    var registeringAccountWarga by remember { mutableStateOf<Warga?>(null) }
    var showRegisterAccountDialog by remember { mutableStateOf(false) }

    // Filter Logic
    val filteredList = wargaList.filter { warga ->
        val matchesQuery = warga.namaKepalaKeluarga.contains(searchQuery, ignoreCase = true) ||
                warga.noKk.contains(searchQuery) ||
                warga.blokRumah.contains(searchQuery, ignoreCase = true)

        val matchesEkonomi = selectedFilterEkonomi == null || warga.statusEkonomi == selectedFilterEkonomi

        matchesQuery && matchesEkonomi
    }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Data Warga RT 06",
                subtitle = "${wargaList.size} Kepala Keluarga Terdaftar",
                showBack = false
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingWarga = null
                    showFormDialog = true
                },
                containerColor = RTNavy,
                contentColor = RTWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Warga")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Tambah Warga", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama kepala keluarga, no. KK, atau blok...", color = RTMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null, tint = RTMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = RTMuted)
                            }
                        }
                    },
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

            // Quick Action Card: Daftarkan Akun Login Warga
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        registeringAccountWarga = null
                        showRegisterAccountDialog = true
                    },
                colors = CardDefaults.cardColors(containerColor = RTMint.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, RTMint)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(RTTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = RTWhite, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Daftarkan Akun Login Warga", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy))
                            Text(text = "Buat akun login baru agar warga dapat masuk aplikasi", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp))
                        }
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = RTTeal, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Filter Chips Horizontal Scroll
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilterEkonomi == null,
                        onClick = {
                            selectedFilterEkonomi = null
                        },
                        label = { Text("Semua (${wargaList.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilterEkonomi == StatusEkonomi.REGULER,
                        onClick = {
                            selectedFilterEkonomi = if (selectedFilterEkonomi == StatusEkonomi.REGULER) null else StatusEkonomi.REGULER
                        },
                        label = { Text("Reguler") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilterEkonomi == StatusEkonomi.PENGUSAHA,
                        onClick = {
                            selectedFilterEkonomi = if (selectedFilterEkonomi == StatusEkonomi.PENGUSAHA) null else StatusEkonomi.PENGUSAHA
                        },
                        label = { Text("Pengusaha") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Daftar Kartu Warga
            if (filteredList.isEmpty()) {
                EmptyStateView(
                    title = "Data Tidak Ditemukan",
                    message = "Tidak ada data keluarga yang sesuai dengan kata kunci atau filter yang dipilih.",
                    icon = Icons.Outlined.PersonSearch
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList, key = { it.id }) { warga ->
                        WargaCardItem(
                            warga = warga,
                            onEdit = {
                                editingWarga = warga
                                showFormDialog = true
                            },
                            onDelete = {
                                deletingWarga = warga
                            },
                            onRegisterAccount = {
                                registeringAccountWarga = warga
                                showRegisterAccountDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal Form Tambah / Edit Warga
    if (showFormDialog) {
        WargaFormDialog(
            initialWarga = editingWarga,
            onDismiss = { showFormDialog = false },
            onSave = { warga, email, pass ->
                if (editingWarga == null) {
                    AppRepository.addWarga(warga)
                    if (!email.isNullOrBlank() && !pass.isNullOrBlank()) {
                        AppRepository.registerWargaAccount(warga, email, pass)
                    }
                } else {
                    AppRepository.updateWarga(warga)
                }
                showFormDialog = false
            }
        )
    }

    // Modal Daftarkan Akun Warga
    if (showRegisterAccountDialog) {
        DaftarAkunWargaDialog(
            initialWarga = registeringAccountWarga,
            wargaList = wargaList,
            onDismiss = {
                showRegisterAccountDialog = false
                registeringAccountWarga = null
            }
        )
    }

    // Dialog Konfirmasi Hapus
    if (deletingWarga != null) {
        ConfirmationDialog(
            title = "Hapus Data Keluarga?",
            message = "Apakah Anda yakin ingin menghapus data keluarga ${deletingWarga?.namaKepalaKeluarga} (${deletingWarga?.blokRumah})? Tindakan ini tidak dapat dibatalkan.",
            confirmText = "Hapus Data",
            isDestructive = true,
            onConfirm = {
                deletingWarga?.let { AppRepository.deleteWarga(it.id) }
                deletingWarga = null
            },
            onDismiss = { deletingWarga = null }
        )
    }
}

@Composable
fun WargaCardItem(
    warga: Warga,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRegisterAccount: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val accounts by AppRepository.accounts.collectAsState()
    val account = accounts.find { it.familyId == warga.id || it.phoneNumber == warga.noHp }

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, RTBorder, RoundedCornerShape(14.dp))
            .animateContentSize()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = RTWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(RTNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = warga.namaKepalaKeluarga.split(" ").map { it.take(1) }.joinToString("").take(2),
                            style = MaterialTheme.typography.labelLarge.copy(color = RTWhite, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = warga.namaKepalaKeluarga,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 15.sp)
                        )
                        Text(
                            text = "${warga.blokRumah} • ${warga.jumlahAnggota} Jiwa",
                            style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No. KK: ${warga.noKk}",
                    style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(RTMint)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = warga.statusEkonomi.name,
                        style = MaterialTheme.typography.labelSmall.copy(color = RTTeal, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    )
                }
            }

            // Expanded Details & Actions
            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = RTBorder)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "NIK Kepala Keluarga: ${warga.nikKepalaKeluarga}", style = MaterialTheme.typography.bodySmall.copy(color = RTText))
                    Text(text = "Nomor WhatsApp: ${warga.noHp}", style = MaterialTheme.typography.bodySmall.copy(color = RTText))
                    Text(text = "Kategori Iuran: ${warga.kategoriIuranNama}", style = MaterialTheme.typography.bodySmall.copy(color = RTText))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Status Akun: ", style = MaterialTheme.typography.bodySmall.copy(color = RTText))
                        Text(
                            text = if (account != null) "AKTIF (${account.email})" else "BELUM MEMILIKI AKUN",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (account != null) RTTeal else RTAmber,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tombol Hubungi via WhatsApp
                Button(
                    onClick = {
                        val templateMessage = "Halo Bapak/Ibu ${warga.namaKepalaKeluarga} (${warga.blokRumah}), kami dari Pengurus RT 06 ingin berkoordinasi terkait lingkungan RT..."
                        WhatsAppHelper.openWhatsApp(context, warga.noHp, templateMessage)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RTWhatsApp,
                        contentColor = RTWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(16.dp),
                        tint = RTWhite
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hubungi via WhatsApp (${warga.noHp})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RTWhite
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onRegisterAccount,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (account != null) RTMint else RTTeal,
                            contentColor = if (account != null) RTNavy else RTWhite
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(
                            imageVector = if (account != null) Icons.Default.VpnKey else Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (account != null) RTNavy else RTWhite
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (account != null) "Kelola Akun" else "Daftarkan Akun",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = RTNavy)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Edit", fontSize = 12.sp, color = RTNavy)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = RTDanger, contentColor = RTRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = RTRed)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Hapus", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog Form Tambah / Edit Warga Vertikal dengan Validasi Lengkap
@Composable
fun WargaFormDialog(
    initialWarga: Warga?,
    onDismiss: () -> Unit,
    onSave: (Warga, String?, String?) -> Unit
) {
    var nama by remember { mutableStateOf(initialWarga?.namaKepalaKeluarga ?: "") }
    var noKk by remember { mutableStateOf(initialWarga?.noKk ?: "") }
    var nik by remember { mutableStateOf(initialWarga?.nikKepalaKeluarga ?: "") }
    var blok by remember { mutableStateOf(initialWarga?.blokRumah ?: "") }
    var noHp by remember { mutableStateOf(initialWarga?.noHp ?: "") }
    var jumlahAnggota by remember { mutableStateOf((initialWarga?.jumlahAnggota ?: 1).toString()) }
    var statusEkonomi by remember { mutableStateOf(initialWarga?.statusEkonomi ?: StatusEkonomi.REGULER) }

    // Opsi Buat Akun Sekaligus (khusus tambah warga baru)
    var buatAkunSekaligus by remember { mutableStateOf(initialWarga == null) }
    var akunEmail by remember { mutableStateOf("") }
    var akunPassword by remember { mutableStateOf("warga123") }

    // Validation errors
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = RTWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (initialWarga == null) "Tambah Keluarga Baru" else "Edit Data Keluarga",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 18.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(RTDanger)
                            .padding(10.dp)
                    ) {
                        Text(text = errorMessage ?: "", style = MaterialTheme.typography.bodySmall.copy(color = RTRed, fontWeight = FontWeight.Bold))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Fields
                FormLabel("Nama Kepala Keluarga *")
                OutlinedTextField(
                    value = nama,
                    onValueChange = {
                        nama = it
                        if (akunEmail.isBlank() || akunEmail.endsWith("@warga.id")) {
                            akunEmail = if (it.isNotBlank()) it.lowercase().trim().replace(" ", "") + "@warga.id" else ""
                        }
                    },
                    placeholder = { Text("Contoh: Budi Santoso", color = RTMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Nomor Kartu Keluarga (16 Digit) *")
                OutlinedTextField(
                    value = noKk,
                    onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) noKk = it },
                    placeholder = { Text("Contoh: 3273010101900001", color = RTMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("NIK Kepala Keluarga (16 Digit) *")
                OutlinedTextField(
                    value = nik,
                    onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) nik = it },
                    placeholder = { Text("Contoh: 3273010101900011", color = RTMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Alamat / Blok Rumah *")
                OutlinedTextField(
                    value = blok,
                    onValueChange = { blok = it },
                    placeholder = { Text("Contoh: Blok B No. 12", color = RTMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Nomor WhatsApp *")
                OutlinedTextField(
                    value = noHp,
                    onValueChange = { noHp = it },
                    placeholder = { Text("Contoh: 081234567890", color = RTMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Jumlah Anggota Keluarga (Jiwa)")
                OutlinedTextField(
                    value = jumlahAnggota,
                    onValueChange = { if (it.all { c -> c.isDigit() }) jumlahAnggota = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormLabel("Kategori Status Ekonomi")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusEkonomi.values().forEach { status ->
                        FilterChip(
                            selected = statusEkonomi == status,
                            onClick = { statusEkonomi = status },
                            label = { Text(status.name, fontSize = 11.sp) }
                        )
                    }
                }

                // Opsi Daftarkan Akun Login Sekaligus
                if (initialWarga == null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = RTBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { buatAkunSekaligus = !buatAkunSekaligus },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = buatAkunSekaligus,
                            onCheckedChange = { buatAkunSekaligus = it }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "Sekaligus Buat Akun Login Warga", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy))
                            Text(text = "Warga dapat langsung login ke aplikasi", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp))
                        }
                    }

                    if (buatAkunSekaligus) {
                        Spacer(modifier = Modifier.height(8.dp))
                        FormLabel("Email Akun Login Warga *")
                        OutlinedTextField(
                            value = akunEmail,
                            onValueChange = { akunEmail = it },
                            placeholder = { Text("Contoh: budi@warga.id", color = RTMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        FormLabel("Kata Sandi Akun (Min. 6 Karakter) *")
                        OutlinedTextField(
                            value = akunPassword,
                            onValueChange = { akunPassword = it },
                            placeholder = { Text("Default: warga123", color = RTMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                        Text(text = "Batal", color = RTMuted)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            // Validations
                            if (nama.isBlank()) {
                                errorMessage = "Nama Kepala Keluarga wajib diisi."
                                return@Button
                            }
                            if (noKk.length != 16) {
                                errorMessage = "Nomor KK harus tepat 16 digit angka (saat ini: ${noKk.length})."
                                return@Button
                            }
                            if (nik.length != 16) {
                                errorMessage = "NIK harus tepat 16 digit angka (saat ini: ${nik.length})."
                                return@Button
                            }
                            if (blok.isBlank()) {
                                errorMessage = "Alamat / Blok Rumah wajib diisi."
                                return@Button
                            }
                            if (noHp.isBlank()) {
                                errorMessage = "Nomor WhatsApp wajib diisi."
                                return@Button
                            }

                            if (initialWarga == null && buatAkunSekaligus) {
                                if (akunEmail.isBlank() || !akunEmail.contains("@")) {
                                    errorMessage = "Format email akun tidak valid."
                                    return@Button
                                }
                                if (akunPassword.length < 6) {
                                    errorMessage = "Kata sandi akun minimal 6 karakter."
                                    return@Button
                                }
                            }

                            val newWarga = Warga(
                                id = initialWarga?.id ?: UUID.randomUUID().toString(),
                                noKk = noKk,
                                nikKepalaKeluarga = nik,
                                namaKepalaKeluarga = nama,
                                blokRumah = blok,
                                noHp = noHp,
                                jumlahAnggota = jumlahAnggota.toIntOrNull() ?: 1,
                                statusEkonomi = statusEkonomi,
                                kategoriIuranNama = when (statusEkonomi) {
                                    StatusEkonomi.REGULER -> "Reguler"
                                    StatusEkonomi.PENGUSAHA -> "Pengusaha"
                                    StatusEkonomi.SUBSIDI -> "Subsidi"
                                },
                                statusIuran = initialWarga?.statusIuran ?: StatusIuran.BELUM_DIBAYAR
                            )

                            val emailToSave = if (initialWarga == null && buatAkunSekaligus) akunEmail.trim() else null
                            val passToSave = if (initialWarga == null && buatAkunSekaligus) akunPassword else null
                            onSave(newWarga, emailToSave, passToSave)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTNavy)
                    ) {
                        Text(text = "Simpan Data", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog Khusus Daftarkan Akun Warga oleh Admin
@Composable
fun DaftarAkunWargaDialog(
    initialWarga: Warga?,
    wargaList: List<Warga>,
    onDismiss: () -> Unit
) {
    var selectedWarga by remember { mutableStateOf(initialWarga ?: wargaList.firstOrNull()) }
    var email by remember(selectedWarga) {
        val defaultEmail = selectedWarga?.let {
            it.namaKepalaKeluarga.lowercase().trim().replace(" ", "") + "@warga.id"
        } ?: ""
        mutableStateOf(defaultEmail)
    }
    var phone by remember(selectedWarga) {
        mutableStateOf(selectedWarga?.noHp ?: "")
    }
    var password by remember { mutableStateOf("warga123") }
    var confirmPassword by remember { mutableStateOf("warga123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successAccount by remember { mutableStateOf<UserAccount?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = RTWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RTMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = RTTeal)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Daftarkan Akun Warga",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 18.sp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (successAccount != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(RTMint)
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = RTTeal)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Akun Berhasil Didaftarkan!",
                                    style = MaterialTheme.typography.labelLarge.copy(color = RTNavy, fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Berikan kredensial login berikut kepada warga agar dapat masuk ke aplikasi RT Connect:",
                                style = MaterialTheme.typography.bodySmall.copy(color = RTText, fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "• Nama: ${successAccount?.fullName}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = RTNavy)
                            )
                            Text(
                                text = "• Email Login: ${successAccount?.email}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = RTNavy)
                            )
                            Text(
                                text = "• Kata Sandi: ${successAccount?.password}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = RTNavy)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTNavy)
                    ) {
                        Text("Selesai & Tutup", fontWeight = FontWeight.Bold)
                    }
                } else {
                    if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(RTDanger)
                                .padding(10.dp)
                        ) {
                            Text(text = errorMessage ?: "", color = RTRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Pilih Warga (jika initialWarga null)
                    if (initialWarga == null && wargaList.isNotEmpty()) {
                        FormLabel("Pilih Kepala Keluarga *")
                        var dropdownExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedWarga?.let { "${it.namaKepalaKeluarga} (${it.blokRumah})" } ?: "Pilih Warga",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { dropdownExpanded = true }) {
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { dropdownExpanded = true },
                                shape = RoundedCornerShape(10.dp)
                            )
                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                wargaList.forEach { w ->
                                    DropdownMenuItem(
                                        text = { Text("${w.namaKepalaKeluarga} - ${w.blokRumah}") },
                                        onClick = {
                                            selectedWarga = w
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    } else if (selectedWarga != null) {
                        // Info Warga
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(RTCanvas)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = selectedWarga!!.namaKepalaKeluarga,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy)
                                )
                                Text(
                                    text = "${selectedWarga!!.blokRumah} • WhatsApp: ${selectedWarga!!.noHp}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    FormLabel("Email Akun Warga *")
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        placeholder = { Text("Contoh: nama@warga.id", color = RTMuted, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormLabel("Nomor WhatsApp *")
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it; errorMessage = null },
                        placeholder = { Text("Contoh: 081234567890", color = RTMuted, fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormLabel("Kata Sandi Akun (Min. 6 Karakter) *")
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        placeholder = { Text("Minimal 6 karakter", color = RTMuted, fontSize = 12.sp) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = null,
                                    tint = RTMuted
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormLabel("Konfirmasi Kata Sandi *")
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorMessage = null },
                        placeholder = { Text("Ulangi kata sandi", color = RTMuted, fontSize = 12.sp) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                val target = selectedWarga
                                if (target == null) {
                                    errorMessage = "Pilih data warga terlebih dahulu."
                                    return@Button
                                }
                                if (email.isBlank() || !email.contains("@")) {
                                    errorMessage = "Format email tidak valid."
                                    return@Button
                                }
                                if (password.length < 6) {
                                    errorMessage = "Kata sandi minimal 6 karakter."
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    errorMessage = "Konfirmasi kata sandi tidak cocok."
                                    return@Button
                                }

                                val wargaToRegister = target.copy(noHp = phone.ifBlank { target.noHp })
                                val res = AppRepository.registerWargaAccount(wargaToRegister, email, password)
                                if (res.isSuccess) {
                                    successAccount = res.getOrNull()
                                    errorMessage = null
                                } else {
                                    errorMessage = res.exceptionOrNull()?.message ?: "Gagal mendaftarkan akun."
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RTNavy)
                        ) {
                            Text("Daftarkan Akun", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = RTText, fontSize = 12.sp),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}
