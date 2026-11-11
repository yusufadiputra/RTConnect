package com.example.rtconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.data.model.StatusIuran
import com.example.rtconnect.data.model.StatusLaporan
import com.example.rtconnect.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RTTopAppBar(
    title: String = "RT CONNECT",
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    showNotification: Boolean = true,
    onNotificationClick: () -> Unit = {}
) {
    Surface(
        color = RTWhite,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showBackButton) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, RTBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = RTNavy
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = if (subtitle != null) 18.sp else 20.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = RTMuted,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            if (showNotification) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, RTBorder, RoundedCornerShape(12.dp))
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifikasi",
                        tint = RTNavy
                    )
                    // Notification dot
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(RTTeal)
                    )
                }
            }
        }
    }
}

@Composable
fun RTBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavigationItem("warga_beranda", "Beranda", Icons.Outlined.Home, Icons.Filled.Home),
        NavigationItem("warga_riwayat", "Riwayat", Icons.Outlined.ReceiptLong, Icons.Filled.ReceiptLong),
        NavigationItem("warga_laporan", "Laporan", Icons.Outlined.Campaign, Icons.Filled.Campaign),
        NavigationItem("warga_profil", "Profil", Icons.Outlined.Person, Icons.Filled.Person)
    )

    Surface(
        color = RTWhite,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = RTWhite,
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(72.dp)
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.route) },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RTTeal,
                        selectedTextColor = RTTeal,
                        unselectedIconColor = RTMuted,
                        unselectedTextColor = RTMuted,
                        indicatorColor = RTMint
                    )
                )
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

@Composable
fun RTButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) RTTeal else RTWhite,
            contentColor = if (isPrimary) RTWhite else RTNavy,
            disabledContainerColor = RTBorder,
            disabledContentColor = RTMuted
        ),
        border = if (!isPrimary) androidx.compose.foundation.BorderStroke(1.dp, RTBorder) else null
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
fun RTStatusBadge(
    statusIuran: StatusIuran? = null,
    statusLaporan: StatusLaporan? = null,
    modifier: Modifier = Modifier
) {
    val (text, bgColor, textColor) = when {
        statusIuran != null -> when (statusIuran) {
            StatusIuran.LUNAS -> Triple("LUNAS", RTMint, RTTeal)
            StatusIuran.BELUM_DIBAYAR, StatusIuran.BELUM_LUNAS -> Triple("BELUM BAYAR", RTDanger, RTRed)
            StatusIuran.MENUNGGU_PEMBAYARAN, StatusIuran.MENUNGGU_KONFIRMASI -> Triple("MENUNGGU", RTWarning, RTAmber)
            StatusIuran.GAGAL -> Triple("GAGAL", RTDanger, RTRed)
        }
        statusLaporan != null -> when (statusLaporan) {
            StatusLaporan.BARU -> Triple("BARU", RTDanger, RTRed)
            StatusLaporan.DIPROSES -> Triple("DIPROSES", RTWarning, RTAmber)
            StatusLaporan.SELESAI -> Triple("SELESAI", RTMint, RTTeal)
            StatusLaporan.DITOLAK -> Triple("DITOLAK", RTDanger, RTRed)
        }
        else -> Triple("", RTCanvas, RTMuted)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            maxLines = 1,
            softWrap = false,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}
