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
    var identifier by remember { mutableStateOf("0812 3456 7890") }
    var password by remember { mutableStateOf("warga123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showBantuanDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Auto detect role berdasarkan identifier
    val detectedRole = remember(identifier) {
        val lower = identifier.lowercase().trim()
        if (lower.contains("admin") || lower.contains("rt") || lower.startsWith("ketua")) {
            LoginRole.ADMIN
        } else {
            LoginRole.WARGA
        }
    }

    fun executeLogin() {
        if (identifier.isBlank() || password.isBlank()) {
            errorMessage = "Harap masukkan akun dan kata sandi"
            return
        }
        errorMessage = null

        if (detectedRole == LoginRole.ADMIN) {
            AppRepository.switchUserRole(UserRole.ADMIN)
            onLoginAsAdmin()
        } else {
            AppRepository.switchUserRole(UserRole.WARGA)
            onLoginAsWarga()
        }
    }

    Scaffold(
        containerColor = RTCanvas
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
                    // Badge RT 10 & Ikon Lingkungan
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
                                    text = "Lingkungan RT 10 Digital",
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
                                "Contoh: 081234567890 atau ketua.rt10",
                                color = RTMuted.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (detectedRole == LoginRole.ADMIN) Icons.Outlined.AdminPanelSettings else Icons.Outlined.PhoneAndroid,
                                contentDescription = null,
                                tint = if (detectedRole == LoginRole.ADMIN) RTTeal else RTNavy
                            )
                        },
                        trailingIcon = {
                            if (detectedRole == LoginRole.ADMIN) {
                                Surface(
                                    color = RTMint,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = "Admin RT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = RTTeal,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
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

                    // Lupa Kata Sandi Link
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Lupa kata sandi?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = RTTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier
                                .clickable { showBantuanDialog = true }
                                .padding(vertical = 4.dp)
                        )
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
                                text = if (detectedRole == LoginRole.ADMIN) "Masuk ke Panel Admin RT" else "Masuk ke Beranda Warga",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RTWhite,
                                    fontSize = 14.sp
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
                                text = "Data keluarga, tagihan sampah, dan laporan terenkripsi khusus untuk warga RT 10.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = RTNavy.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Demo Preset Selector (Untuk mempermudah testing saat buka)
                Text(
                    text = "PILIHAN CEPAT (MODE PENGUJIAN):",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = RTMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            identifier = "0812 3456 7890"
                            password = "warga123"
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (detectedRole == LoginRole.WARGA) RTWhite else Color.Transparent
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (detectedRole == LoginRole.WARGA) RTTeal else RTBorder
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Akun Warga",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (detectedRole == LoginRole.WARGA) RTTeal else RTText
                                )
                            )
                            Text(
                                text = "0812 3456 7890",
                                style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 10.sp)
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            identifier = "ketua.rt10"
                            password = "admin123"
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (detectedRole == LoginRole.ADMIN) RTWhite else Color.Transparent
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (detectedRole == LoginRole.ADMIN) RTTeal else RTBorder
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Akun Pengurus RT",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (detectedRole == LoginRole.ADMIN) RTTeal else RTText
                                )
                            )
                            Text(
                                text = "ketua.rt10",
                                style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 10.sp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
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
                        text = "Nomor ponsel atau akun Anda didaftarkan langsung oleh Sekretariat RT 10.",
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
