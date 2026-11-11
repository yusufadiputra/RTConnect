package com.example.rtconnect.ui.screens.intro

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.R
import com.example.rtconnect.ui.theme.*
import kotlinx.coroutines.delay

object PreferenceHelper {
    private const val PREF_NAME = "rtconnect_app_prefs"
    private const val KEY_TOS_ACCEPTED = "key_has_accepted_tos"

    fun isTosAccepted(context: Context): Boolean {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sp.getBoolean(KEY_TOS_ACCEPTED, false)
    }

    fun setTosAccepted(context: Context, accepted: Boolean = true) {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sp.edit().putBoolean(KEY_TOS_ACCEPTED, accepted).apply()
    }
}

@Composable
fun SplashScreen(
    onNavigateNext: (targetRoute: String) -> Unit
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2200) // Tampilan splash sekitar 2.2 detik
        val hasAccepted = PreferenceHelper.isTosAccepted(context)
        if (hasAccepted) {
            onNavigateNext("login")
        } else {
            onNavigateNext("terms_of_service")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        RTNavy,
                        Color(0xFF0F2B3E),
                        RTTeal
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .alpha(alphaAnim)
        ) {
            // Visual Logo Tema Rumah / Smart Community
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(pulseScale)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White)
                    .border(2.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_rtconnect),
                    contentDescription = "Logo RTConnect",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "RT CONNECT",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 3.sp,
                    fontSize = 28.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sistem Lingkungan & Warga Terpadu",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = RTMint.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Indikator loading halus
            CircularProgressIndicator(
                color = RTMint,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(24.dp)
            )
        }

        // Tagline / Version di bawah
        Text(
            text = "Digitalisasi Rukun Tetangga • Versi 1.0",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
fun TermsOfServiceScreen(
    onAccepted: () -> Unit
) {
    val context = LocalContext.current
    var isChecked by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = RTCanvas,
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = RTWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = RTTeal,
                                checkmarkColor = RTWhite
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Saya telah membaca dan menyetujui Ketentuan Layanan & Kebijakan Privasi RTConnect.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = RTText,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (isChecked) {
                                PreferenceHelper.setTosAccepted(context, true)
                                onAccepted()
                            }
                        },
                        enabled = isChecked,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RTTeal,
                            disabledContainerColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Setuju & Mulai Aplikasi",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = RTWhite,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(RTMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = RTTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Ketentuan Layanan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Syarat penggunaan aplikasi RTConnect",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = RTMuted,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Notice Card
            Card(
                colors = CardDefaults.cardColors(containerColor = RTMint.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = RTTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Langkah ini hanya muncul satu kali saat aplikasi pertama kali dipasang untuk memastikan keamanan & kenyamanan data warga RT.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = RTNavy,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Content Sections
            TosSection(
                number = "1",
                title = "Penggunaan Akun & Identitas Warga",
                content = "Aplikasi RTConnect ditujukan khusus untuk warga terdaftar serta pengurus lingkungan RT. Pengguna bertanggung jawab menjaga kerahasiaan kata sandi dan seluruh aktivitas pada akun masing-masing."
            )

            TosSection(
                number = "2",
                title = "Transparansi Iuran & Laporan",
                content = "Setiap data pembayaran iuran sampah, iuran warga, dan permohonan bansos dicatat secara digital untuk akuntabilitas. Laporan pengaduan lingkungan harus disampaikan secara beretika dan berdasarkan fakta."
            )

            TosSection(
                number = "3",
                title = "Privasi dan Perlindungan Data",
                content = "Informasi keluarga, nomor ponsel, dan data pribadi tidak akan disebarluaskan kepada pihak luar tanpa persetujuan. Sistem menggunakan enkripsi standar untuk melindungi keamanan identitas warga."
            )

            TosSection(
                number = "4",
                title = "Tanggung Jawab Pengurus RT",
                content = "Pengurus RT berkewajiban mengelola data warga secara bijaksana, menanggapi laporan tepat waktu, dan memberikan pembaruan pengumuman berkala bagi kemaslahatan bersama."
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun TosSection(
    number: String,
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(bottom = 18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(RTNavy),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTNavy,
                    fontSize = 14.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = RTText,
                fontSize = 13.sp,
                lineHeight = 19.sp
            ),
            modifier = Modifier.padding(start = 32.dp)
        )
    }
}

