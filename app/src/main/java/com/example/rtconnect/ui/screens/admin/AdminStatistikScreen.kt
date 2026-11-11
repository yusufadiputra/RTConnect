package com.example.rtconnect.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.ui.components.AdminTopAppBar
import com.example.rtconnect.ui.components.MonthlyPaymentBarChart
import com.example.rtconnect.ui.components.StatCard
import com.example.rtconnect.ui.theme.*

@Composable
fun AdminStatistikScreen(
    onBackClick: () -> Unit = {},
    onNavigateToNotifikasi: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Statistik Lingkungan",
                subtitle = "Ringkasan data RT 10",
                unreadNotifCount = 2,
                showBack = true,
                onBackClick = onBackClick,
                onNotificationClick = onNavigateToNotifikasi
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

            // Title & Subtitle matching Figma Screen 8
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Statistik lingkungan",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = 22.sp
                        )
                    )
                    Text(
                        text = "Gambaran pembayaran dan penanganan laporan RT 10.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = RTMuted,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Period Selector Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(RTWhite)
                    .border(1.dp, RTBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = RTTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "November 2026 ▼",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = RTText,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4-Grid Key Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Kepatuhan iuran",
                    value = "85%",
                    subtitle = "45 dari 53 keluarga",
                    badgeText = "TARGET 100%",
                    badgeColor = RTMint,
                    badgeTextColor = RTTeal,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Laporan masuk",
                    value = "12",
                    subtitle = "Selama November 2026",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Laporan selesai",
                    value = "7",
                    subtitle = "58% dari seluruh laporan",
                    badgeText = "SELESAI",
                    badgeColor = RTMint,
                    badgeTextColor = RTTeal,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Rata penanganan",
                    value = "2 hari",
                    subtitle = "Dari laporan hingga selesai",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tren Pembayaran Iuran Sampah (Bar Chart)
            MonthlyPaymentBarChart(
                months = listOf("Jun", "Jul", "Agu", "Sep", "Okt", "Nov"),
                values = listOf(0.75f, 0.80f, 0.88f, 0.92f, 0.86f, 0.85f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Laporan per Kategori Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Laporan per kategori",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    CategoryProgressRow(category = "Sampah", count = 5, total = 12, barColor = RTTeal)
                    Spacer(modifier = Modifier.height(10.dp))
                    CategoryProgressRow(category = "Keamanan", count = 3, total = 12, barColor = RTNavy)
                    Spacer(modifier = Modifier.height(10.dp))
                    CategoryProgressRow(category = "Kekeringan", count = 2, total = 12, barColor = RTAmber)
                    Spacer(modifier = Modifier.height(10.dp))
                    CategoryProgressRow(category = "Lingkungan", count = 2, total = 12, barColor = RTTeal.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Penanganan Laporan Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Status penanganan laporan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatusCountBox(count = "7", label = "Selesai", bgColor = RTMint, textColor = RTTeal, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusCountBox(count = "3", label = "Diproses", bgColor = RTWarning, textColor = RTAmber, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusCountBox(count = "2", label = "Menunggu", bgColor = RTDanger, textColor = RTRed, modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footnote
            Text(
                text = "Sumber data pembayaran dan laporan warga RT 10 • Diperbarui 10 November 2026",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = RTMuted,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(bottom = 28.dp)
            )
        }
    }
}

@Composable
private fun CategoryProgressRow(
    category: String,
    count: Int,
    total: Int,
    barColor: Color
) {
    val progress = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category, style = MaterialTheme.typography.bodyMedium.copy(color = RTText, fontSize = 13.sp))
            Text(text = "$count", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 13.sp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(RTCanvas)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
private fun StatusCountBox(
    count: String,
    label: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                )
            )
        }
    }
}
