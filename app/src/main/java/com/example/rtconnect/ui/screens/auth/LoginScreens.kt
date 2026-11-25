package com.example.rtconnect.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.R
import com.example.rtconnect.data.model.UserRole
import com.example.rtconnect.data.repository.AppRepository
import com.example.rtconnect.ui.theme.*

enum class LoginRole {
    ADMIN, WARGA
}

/**
 * Unified Beautiful Login Screen with Smart Role Detection & Housing/Neighborhood Visual Theme
 */
@Composable
fun LoginScreen(
    initialRole: LoginRole = LoginRole.WARGA,
    onLoginAsWarga: () -> Unit,
    onLoginAsAdmin: () -> Unit
) {
    // Default form state
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showBantuanDialog by remember { mutableStateOf(false) }
    var showGantiPasswordDialog by remember { mutableStateOf(false) }
    var successNotification by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun executeLogin() {
        if (identifier.isBlank() || password.isBlank()) {
            errorMessage = "Harap masukkan akun dan kata sandi"
            return
        }
        errorMessage = null

        val result = AppRepository.login(identifier, password)
        if (result.isSuccess) {
            val user = result.getOrNull()!!
            if (user.role == com.example.rtconnect.data.model.UserRole.ADMIN) {
                onLoginAsAdmin()
            } else {
                onLoginAsWarga()
            }
        } else {
            errorMessage = result.exceptionOrNull()?.message ?: "Kombinasi akun atau kata sandi salah."
        }
    }

    Scaffold(
        containerColor = RTCanvas,
        contentWindowInsets = WindowInsets.ime
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ==========================================
            // HEADER ESTETIK: Nuansa Rumah & Pemukiman
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                RTNavy,
                                Color(0xFF13364D),
                                RTCanvas
                            )
                        )
                    )
                    .padding(top = 28.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Badge RT 06 & Ikon Lingkungan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(RTMint)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Lingkungan RT 06 Digital",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Logo mini RTConnect
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_rtconnect),
                                contentDescription = "Logo RTConnect",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "RT CONNECT",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.5.sp,
                            fontSize = 30.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Satu portal akses untuk seluruh warga dan pengurus lingkungan rukun tetangga.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            // ==========================================
            // KARTU FORM LOGIN (ELEVATED CARD)
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-10).dp),
                colors = CardDefaults.cardColors(containerColor = RTWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RTBorder)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp)
                ) {
                    Text(
                        text = "Masuk ke Akun Anda",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Gunakan nomor ponsel atau username yang telah terdaftar.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = RTMuted,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Field 1: Akun (No Ponsel / Username)
                    Text(
                        text = "Nomor Ponsel atau Username",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = RTText,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = {
                            identifier = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text(
                                "Masukkan nomor ponsel atau email",
                                color = RTMuted.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = RTNavy
                            )
                        },
                        trailingIcon = {
                            if (identifier.isNotEmpty()) {
                                IconButton(onClick = { identifier = "" }) {
                                    Icon(imageVector = Icons.Outlined.Clear, contentDescription = "Clear", tint = RTMuted)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RTTeal,
                            unfocusedBorderColor = RTBorder,
                            focusedContainerColor = RTWhite,
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field 2: Kata Sandi
                    Text(
                        text = "Kata Sandi",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = RTText,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        placeholder = { Text("Masukkan kata sandi", color = RTMuted.copy(alpha = 0.7f), fontSize = 13.sp) },
                        singleLine = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = RTNavy)
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Sembunyikan" else "Tampilkan",
                                    tint = RTMuted
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RTTeal,
                            unfocusedBorderColor = RTBorder,
                            focusedContainerColor = RTWhite,
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = RTRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bantuan & Ganti Kata Sandi Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bantuan Masuk",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = RTMuted,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier
                                .clickable { showBantuanDialog = true }
                                .padding(vertical = 4.dp)
                        )

                        Text(
                            text = "Ganti Kata Sandi?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = RTTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier
                                .clickable {
                                    errorMessage = null
                                    successNotification = null
                                    showGantiPasswordDialog = true
                                }
                                .padding(vertical = 4.dp)
                        )
                    }

                    if (successNotification != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(RTMint)
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null, tint = RTTeal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = successNotification ?: "",
                                    color = RTNavy,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Tombol Masuk Utama
                    Button(
                        onClick = { executeLogin() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Masuk",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RTWhite,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = RTWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // DEKORASI INFO & FITUR LINGKUNGAN (TIDAK KOSONG)
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Info Akses & Privasi
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = RTMint.copy(alpha = 0.45f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RTMint)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RTTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = null,
                                tint = RTWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Akses Aman & Terintegrasi",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RTNavy,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "Data keluarga, tagihan sampah, dan laporan terenkripsi khusus untuk warga RT 06.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = RTNavy.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
            Spacer(modifier = Modifier.height(200.dp))
        }
    }

    // Modal Dialog: Bantuan Masuk
    if (showBantuanDialog) {
        AlertDialog(
            onDismissRequest = { showBantuanDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RTMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Outlined.HelpOutline, contentDescription = null, tint = RTTeal)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Bantuan Masuk Akun",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy
                        )
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Nomor ponsel atau akun Anda didaftarkan langsung oleh Sekretariat RT 06.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = RTText, fontSize = 13.sp)
                    )
                    Text(
                        text = "Jika Anda lupa kata sandi atau nomor ponsel baru, silakan hubungi Sekretaris RT di kantor sekretariat atau via WhatsApp pengurus.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = RTMuted, fontSize = 13.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showBantuanDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RTNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Mengerti", color = RTWhite, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = RTWhite,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Modal Dialog: Ganti Kata Sandi dari Halaman Login
    if (showGantiPasswordDialog) {
        GantiKataSandiLoginDialog(
            initialIdentifier = identifier,
            onDismiss = { showGantiPasswordDialog = false },
            onPasswordChanged = { newPass, usedIdentifier ->
                password = newPass
                identifier = usedIdentifier
                showGantiPasswordDialog = false
                successNotification = "Kata sandi berhasil diperbarui! Silakan tekan tombol Masuk."
                errorMessage = null
            }
        )
    }
}

// Dialog Khusus Ganti Kata Sandi dari Halaman Login
@Composable
fun GantiKataSandiLoginDialog(
    initialIdentifier: String,
    onDismiss: () -> Unit,
    onPasswordChanged: (newPassword: String, identifier: String) -> Unit
) {
    var identifierInput by remember { mutableStateOf(initialIdentifier) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPassVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RTMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = RTTeal)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Ganti Kata Sandi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Perbarui sandi akun warga / pengurus RT",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (successMessage) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(RTMint)
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null, tint = RTTeal)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sandi Berhasil Diperbarui!",
                                    color = RTNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Kata sandi baru Anda telah aktif. Tekan tombol di bawah untuk langsung menggunakan kata sandi baru.",
                                color = RTText,
                                fontSize = 12.sp
                            )
                        }
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
                            Text(
                                text = errorMessage ?: "",
                                color = RTRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = "Email atau Nomor WhatsApp Terdaftar *",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = RTText)
                    )
                    OutlinedTextField(
                        value = identifierInput,
                        onValueChange = { identifierInput = it; errorMessage = null },
                        placeholder = { Text("Contoh: 081234567891 atau budi@warga.id", color = RTMuted, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Text(
                        text = "Kata Sandi Baru (Min. 6 Karakter) *",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = RTText)
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it; errorMessage = null },
                        placeholder = { Text("Masukkan kata sandi baru", color = RTMuted, fontSize = 12.sp) },
                        visualTransformation = if (newPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { newPassVisible = !newPassVisible }) {
                                Icon(
                                    imageVector = if (newPassVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = null,
                                    tint = RTMuted
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Text(
                        text = "Konfirmasi Kata Sandi Baru *",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = RTText)
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; errorMessage = null },
                        placeholder = { Text("Ulangi kata sandi baru", color = RTMuted, fontSize = 12.sp) },
                        visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                Icon(
                                    imageVector = if (confirmPassVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = null,
                                    tint = RTMuted
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            if (successMessage) {
                Button(
                    onClick = {
                        onPasswordChanged(newPassword, identifierInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RTTeal),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Masuk Sekarang", color = RTWhite, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        if (identifierInput.isBlank()) {
                            errorMessage = "Email atau nomor WhatsApp akun wajib diisi."
                            return@Button
                        }
                        if (newPassword.length < 6) {
                            errorMessage = "Kata sandi baru minimal 6 karakter."
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            errorMessage = "Konfirmasi kata sandi tidak cocok."
                            return@Button
                        }

                        val result = AppRepository.resetPasswordByAccountIdentifier(identifierInput, newPassword)
                        if (result.isSuccess) {
                            successMessage = true
                            errorMessage = null
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Gagal mengganti kata sandi."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RTNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Simpan Sandi", color = RTWhite, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!successMessage) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Batal", color = RTMuted)
                }
            }
        },
        containerColor = RTWhite,
        shape = RoundedCornerShape(18.dp)
    )
}

// Wrapper composables for compatibility
@Composable
fun LoginWargaScreen(
    onLoginSuccess: () -> Unit,
    onSwitchToAdmin: () -> Unit
) {
    LoginScreen(
        initialRole = LoginRole.WARGA,
        onLoginAsWarga = onLoginSuccess,
        onLoginAsAdmin = onSwitchToAdmin
    )
}

@Composable
fun LoginAdminScreen(
    onLoginSuccess: () -> Unit,
    onSwitchToWarga: () -> Unit
) {
    LoginScreen(
        initialRole = LoginRole.ADMIN,
        onLoginAsWarga = onSwitchToWarga,
        onLoginAsAdmin = onLoginSuccess
    )
}
