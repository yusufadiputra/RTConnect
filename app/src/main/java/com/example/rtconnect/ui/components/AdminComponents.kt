package com.example.rtconnect.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.navigation.Screen
import com.example.rtconnect.ui.theme.*

// CompositionLocal to open drawer from any admin screen without prop-drilling
val LocalAdminMenuAction = compositionLocalOf<(() -> Unit)?> { null }

// 1. Admin Bottom Navigation Bar (5 Tabs)
@Composable
fun AdminBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavigationItem("admin_dashboard", "Beranda", Icons.Outlined.Dashboard, Icons.Filled.Dashboard),
        NavigationItem("admin_warga", "Warga", Icons.Outlined.People, Icons.Filled.People),
        NavigationItem("admin_pembayaran", "Iuran", Icons.Outlined.Payments, Icons.Filled.Payments),
        NavigationItem("admin_laporan", "Laporan", Icons.Outlined.Campaign, Icons.Filled.Campaign),
        NavigationItem("admin_profil", "Profil", Icons.Outlined.Person, Icons.Filled.Person)
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
                .height(68.dp)
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
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            maxLines = 1,
                            softWrap = false,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RTNavy,
                        selectedTextColor = RTNavy,
                        unselectedIconColor = RTMuted,
                        unselectedTextColor = RTMuted,
                        indicatorColor = RTMint
                    )
                )
            }
        }
    }
}

// 2. Mobile Admin Top App Bar with Chairman Profile and Sidebar Drawer Trigger
@Composable
fun AdminTopAppBar(
    title: String = "RT CONNECT",
    subtitle: String = "Panel Ketua RT 10",
    unreadNotifCount: Int = 2,
    onNotificationClick: () -> Unit = {},
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onMenuClick: (() -> Unit)? = null
) {
    val drawerAction = onMenuClick ?: LocalAdminMenuAction.current

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
                if (showBack) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, RTBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali", tint = RTNavy)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                } else {
                    IconButton(
                        onClick = { drawerAction?.invoke() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, RTBorder, RoundedCornerShape(10.dp))
                            .background(RTWhite)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Buka Menu Sidebar",
                            tint = RTNavy,
                            modifier = Modifier.size(22.dp)
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
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(color = RTMuted, fontSize = 11.sp)
                    )
                }
            }

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
                if (unreadNotifCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(RTRed)
                    )
                }
            }
        }
    }
}

// 2b. Modern Admin Sidebar / Navigation Drawer Content
@Composable
fun AdminNavigationDrawerContent(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = RTWhite,
        drawerTonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Dark Navy Profile Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RTNavy)
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RTMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RT",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RTTeal,
                                    fontSize = 18.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RTTeal.copy(alpha = 0.3f))
                                .border(1.dp, RTTeal, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ADMIN AKTIF",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = RTMint,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "RT CONNECT",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTWhite,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "Bpk. Bambang • Ketua RT 10",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = RTWhite.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = "Lingkungan RT 10 / RW 05",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = RTMuted.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Section 1: Menu Utama (Sesuai Navbar)
            DrawerSectionHeader("MENU UTAMA (NAVBAR)")

            DrawerItem(
                label = "Dashboard",
                icon = Icons.Outlined.Dashboard,
                selected = currentRoute == Screen.AdminDashboard.route,
                onClick = { onNavigate(Screen.AdminDashboard.route) }
            )
            DrawerItem(
                label = "Data Warga",
                icon = Icons.Outlined.People,
                selected = currentRoute == Screen.AdminWarga.route,
                badge = "53 KK",
                onClick = { onNavigate(Screen.AdminWarga.route) }
            )
            DrawerItem(
                label = "Pembayaran Iuran",
                icon = Icons.Outlined.Payments,
                selected = currentRoute == Screen.AdminPembayaran.route,
                badge = "Kasir",
                onClick = { onNavigate(Screen.AdminPembayaran.route) }
            )
            DrawerItem(
                label = "Laporan & Aduan",
                icon = Icons.Outlined.Campaign,
                selected = currentRoute == Screen.AdminLaporan.route,
                badge = "3 Baru",
                badgeColor = RTDanger,
                badgeTextColor = RTRed,
                onClick = { onNavigate(Screen.AdminLaporan.route) }
            )
            DrawerItem(
                label = "Profil Pengurus",
                icon = Icons.Outlined.Person,
                selected = currentRoute == Screen.AdminProfil.route,
                onClick = { onNavigate(Screen.AdminProfil.route) }
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = RTBorder, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))

            // Section 2: Kelola Lingkungan & Keuangan (Menu Sidebar Ekstra)
            DrawerSectionHeader("KELOLA LINGKUNGAN & KEUANGAN")

            DrawerItem(
                label = "Kategori & Tarif Iuran",
                icon = Icons.Outlined.Tune,
                selected = currentRoute == Screen.AdminKategori.route,
                onClick = { onNavigate(Screen.AdminKategori.route) }
            )
            DrawerItem(
                label = "Pengumuman RT",
                icon = Icons.Outlined.Announcement,
                selected = currentRoute == Screen.AdminPengumuman.route,
                badge = "6 Terbit",
                onClick = { onNavigate(Screen.AdminPengumuman.route) }
            )
            DrawerItem(
                label = "Bantuan Sosial (Bansos)",
                icon = Icons.Outlined.VolunteerActivism,
                selected = currentRoute == Screen.AdminBansos.route,
                badge = "Privat",
                badgeColor = RTWarning,
                badgeTextColor = RTAmber,
                onClick = { onNavigate(Screen.AdminBansos.route) }
            )
            DrawerItem(
                label = "Statistik Lingkungan",
                icon = Icons.Outlined.BarChart,
                selected = currentRoute == Screen.AdminStatistik.route,
                badge = "Grafik",
                onClick = { onNavigate(Screen.AdminStatistik.route) }
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = RTBorder, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))

            // Section 3: Akun & Sesi
            DrawerSectionHeader("SISTEM & AKUN")

            DrawerItem(
                label = "Notifikasi Admin",
                icon = Icons.Outlined.Notifications,
                selected = currentRoute == Screen.AdminNotifikasi.route,
                onClick = { onNavigate(Screen.AdminNotifikasi.route) }
            )
            DrawerItem(
                label = "Keluar dari Akun",
                icon = Icons.Outlined.ExitToApp,
                selected = false,
                iconColor = RTRed,
                textColor = RTRed,
                onClick = onLogout
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "RT CONNECT Mobile • Panel Administrasi RT 10",
                    style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 10.sp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            color = RTMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 0.8.sp
        ),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    badge: String? = null,
    badgeColor: Color = RTMint,
    badgeTextColor: Color = RTTeal,
    iconColor: Color? = null,
    textColor: Color? = null,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor ?: if (selected) RTNavy else RTText,
                        fontSize = 13.sp
                    )
                )
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = badgeTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        },
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor ?: if (selected) RTNavy else RTMuted,
                modifier = Modifier.size(20.dp)
            )
        },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = RTMint.copy(alpha = 0.5f),
            unselectedContainerColor = Color.Transparent
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .height(48.dp)
    )
}

// 3. Stat Card Mobile Component
@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    badgeText: String? = null,
    badgeColor: Color = RTMint,
    badgeTextColor: Color = RTTeal,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, RTBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = RTWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                )
                if (badgeText != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            maxLines = 1,
                            softWrap = false,
                            style = MaterialTheme.typography.labelSmall.copy(color = badgeTextColor, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        )
                    }
                } else if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null, tint = RTNavy, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTNavy,
                    fontSize = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = RTMuted,
                    fontSize = 11.sp
                )
            )
        }
    }
}

// 4. Bar Chart for Monthly Payments (Native Compose Canvas)
@Composable
fun MonthlyPaymentBarChart(
    months: List<String> = listOf("Jul", "Agu", "Sep", "Okt", "Nov"),
    values: List<Float> = listOf(0.85f, 0.90f, 0.95f, 0.92f, 0.88f), // percentage paid
    nominalLabels: List<String> = listOf("Rp1.02M", "Rp1.08M", "Rp1.14M", "Rp1.10M", "Rp1.06M"),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = RTWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Judul & Subjudul
            Text(
                text = "Tren Pembayaran Iuran Sampah",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTText,
                    fontSize = 15.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Persentase kepatuhan warga (5 Bulan Terakhir)",
                    style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp),
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(RTMint)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Rata-rata 90%",
                        maxLines = 1,
                        softWrap = false,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = RTTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chart Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                months.forEachIndexed { index, month ->
                    val pct = values.getOrElse(index) { 0.5f }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${(pct * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RTNavy)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height((pct * 80).dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(if (index == months.lastIndex) RTTeal else RTNavy)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = month,
                            style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

// 5. Empty State Component
@Composable
fun EmptyStateView(
    title: String,
    message: String,
    icon: ImageVector = Icons.Outlined.Inbox,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(RTCanvas)
                .border(1.dp, RTBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = RTMuted, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(color = RTText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(color = RTMuted, fontSize = 13.sp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (actionText != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RTTeal)
            ) {
                Text(text = actionText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

// 6. Confirmation Dialog
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Ya, Lanjutkan",
    dismissText: String = "Batal",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, color = RTNavy)
        },
        text = {
            Text(text = message, style = MaterialTheme.typography.bodyMedium.copy(color = RTText))
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) RTRed else RTNavy,
                    contentColor = RTWhite
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = confirmText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = dismissText, color = RTMuted)
            }
        },
        containerColor = RTWhite,
        shape = RoundedCornerShape(16.dp)
    )
}
