package com.example.rtconnect

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.rtconnect.data.model.UserRole
import com.example.rtconnect.data.repository.AppRepository
import com.example.rtconnect.navigation.Screen
import com.example.rtconnect.ui.components.AdminBottomBar
import com.example.rtconnect.ui.components.AdminNavigationDrawerContent
import com.example.rtconnect.ui.components.LocalAdminMenuAction
import com.example.rtconnect.ui.components.RTBottomBar
import com.example.rtconnect.ui.screens.admin.*
import com.example.rtconnect.ui.screens.auth.LoginRole
import com.example.rtconnect.ui.screens.auth.LoginScreen
import com.example.rtconnect.ui.screens.intro.SplashScreen
import com.example.rtconnect.ui.screens.intro.TermsOfServiceScreen
import com.example.rtconnect.ui.screens.warga.*
import com.example.rtconnect.ui.theme.RTCanvas
import com.example.rtconnect.ui.theme.RTConnectTheme
import kotlinx.coroutines.launch

@Composable
fun RTConnectApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Login.route

    // Warga Bottom Bar Tabs
    val wargaBottomRoutes = listOf(
        Screen.Beranda.route,
        Screen.Riwayat.route,
        Screen.Laporan.route,
        Screen.Profil.route
    )

    // Admin Bottom Bar Tabs
    val adminBottomRoutes = listOf(
        Screen.AdminDashboard.route,
        Screen.AdminWarga.route,
        Screen.AdminPembayaran.route,
        Screen.AdminLaporan.route,
        Screen.AdminProfil.route
    )

    val showWargaBottomBar = currentRoute in wargaBottomRoutes
    val showAdminBottomBar = currentRoute in adminBottomRoutes
    val isAdminRoute = currentRoute.startsWith("admin_")

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Pastikan drawer tertutup setiap kali berpindah layar/route
    LaunchedEffect(currentRoute) {
        if (drawerState.isOpen) {
            drawerState.close()
        }
    }

    RTConnectTheme {
        CompositionLocalProvider(
            LocalAdminMenuAction provides {
                coroutineScope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            }
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = false, // Hanya buka saat klik tombol ☰ agar tidak terbuka otomatis karena gesture
                drawerContent = {
                    if (isAdminRoute) {
                        AdminNavigationDrawerContent(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                coroutineScope.launch { drawerState.close() }
                                navController.navigate(route) {
                                    popUpTo(Screen.AdminDashboard.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            onLogout = {
                                coroutineScope.launch { drawerState.close() }
                                AppRepository.switchUserRole(UserRole.WARGA)
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            ) {
                Scaffold(
                    bottomBar = {
                        when {
                            showWargaBottomBar -> {
                                RTBottomBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { route ->
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                            showAdminBottomBar -> {
                                AdminBottomBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { route ->
                                        navController.navigate(route) {
                                            popUpTo(Screen.AdminDashboard.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    },
                    containerColor = RTCanvas
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (showWargaBottomBar || showAdminBottomBar) innerPadding.calculateBottomPadding() else 0.dp)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Splash.route
                        ) {
                            // ==========================================
                            // 0. INTRO (Splash & One-Time Terms of Service)
                            // ==========================================
                            composable(Screen.Splash.route) {
                                SplashScreen(
                                    onNavigateNext = { targetRoute ->
                                        navController.navigate(targetRoute) {
                                            popUpTo(Screen.Splash.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.TermsOfService.route) {
                                TermsOfServiceScreen(
                                    onAccepted = {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(Screen.TermsOfService.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // ==========================================
                            // 1. AUTHENTICATION (Unified Single Login)
                            // ==========================================
                            composable(Screen.Login.route) {
                                LoginScreen(
                                    onLoginAsWarga = {
                                        AppRepository.switchUserRole(UserRole.WARGA)
                                        navController.navigate(Screen.Beranda.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    },
                                    onLoginAsAdmin = {
                                        AppRepository.switchUserRole(UserRole.ADMIN)
                                        navController.navigate(Screen.AdminDashboard.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // Aliases for deep links / legacy routes
                            composable(Screen.LoginWarga.route) {
                                LoginScreen(
                                    initialRole = LoginRole.WARGA,
                                    onLoginAsWarga = {
                                        AppRepository.switchUserRole(UserRole.WARGA)
                                        navController.navigate(Screen.Beranda.route) {
                                            popUpTo(Screen.LoginWarga.route) { inclusive = true }
                                        }
                                    },
                                    onLoginAsAdmin = {
                                        AppRepository.switchUserRole(UserRole.ADMIN)
                                        navController.navigate(Screen.AdminDashboard.route) {
                                            popUpTo(Screen.LoginWarga.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.LoginAdmin.route) {
                                LoginScreen(
                                    initialRole = LoginRole.ADMIN,
                                    onLoginAsWarga = {
                                        AppRepository.switchUserRole(UserRole.WARGA)
                                        navController.navigate(Screen.Beranda.route) {
                                            popUpTo(Screen.LoginAdmin.route) { inclusive = true }
                                        }
                                    },
                                    onLoginAsAdmin = {
                                        AppRepository.switchUserRole(UserRole.ADMIN)
                                        navController.navigate(Screen.AdminDashboard.route) {
                                            popUpTo(Screen.LoginAdmin.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // ==========================================
                            // 2. ADMIN MOBILE SCREENS
                            // ==========================================
                            composable(Screen.AdminDashboard.route) {
                                AdminDashboardScreen(
                                    onNavigateToWarga = { navController.navigate(Screen.AdminWarga.route) },
                                    onNavigateToPembayaran = { navController.navigate(Screen.AdminPembayaran.route) },
                                    onNavigateToKategori = { navController.navigate(Screen.AdminKategori.route) },
                                    onNavigateToLaporan = { navController.navigate(Screen.AdminLaporan.route) },
                                    onNavigateToPengumuman = { navController.navigate(Screen.AdminPengumuman.route) },
                                    onNavigateToBansos = { navController.navigate(Screen.AdminBansos.route) },
                                    onNavigateToStatistik = { navController.navigate(Screen.AdminStatistik.route) },
                                    onNavigateToNotifikasi = { navController.navigate(Screen.AdminNotifikasi.route) }
                                )
                            }

                            composable(Screen.AdminWarga.route) {
                                DataWargaScreen(
                                    onBackClick = { navController.navigate(Screen.AdminDashboard.route) }
                                )
                            }

                            composable(Screen.AdminPembayaran.route) {
                                PembayaranSampahScreen(
                                    onBackClick = { navController.navigate(Screen.AdminDashboard.route) }
                                )
                            }

                            composable(Screen.AdminKategori.route) {
                                KategoriIuranScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.AdminLaporan.route) {
                                KelolaLaporanAdminScreen(
                                    onBackClick = { navController.navigate(Screen.AdminDashboard.route) }
                                )
                            }

                            composable(Screen.AdminPengumuman.route) {
                                KelolaPengumumanAdminScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.AdminBansos.route) {
                                KelolaBansosAdminScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.AdminStatistik.route) {
                                AdminStatistikScreen(
                                    onBackClick = { navController.popBackStack() },
                                    onNavigateToNotifikasi = { navController.navigate(Screen.AdminNotifikasi.route) }
                                )
                            }

                            composable(Screen.AdminProfil.route) {
                                AdminProfileScreen(
                                    onLogout = {
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(Screen.AdminNotifikasi.route) {
                                AdminNotificationScreen(
                                    onBackClick = { navController.popBackStack() }
                                )
                            }

                    // ==========================================
                    // 3. WARGA MOBILE SCREENS
                    // ==========================================
                    composable(Screen.Beranda.route) {
                        BerandaScreen(
                            onNavigateToBayar = { navController.navigate(Screen.BayarIuran.route) },
                            onNavigateToProfil = { navController.navigate(Screen.Profil.route) },
                            onNavigateToLapor = { navController.navigate(Screen.LaporMasalah.route) },
                            onNavigateToPengumuman = { navController.navigate(Screen.Pengumuman.route) },
                            onNavigateToDetailPengumuman = { id -> navController.navigate(Screen.DetailPengumuman.createRoute(id)) },
                            onNavigateToBansos = { navController.navigate(Screen.BantuanSosial.route) },
                            onNavigateToNotifikasi = { navController.navigate(Screen.Notifikasi.route) }
                        )
                    }

                    composable(Screen.Riwayat.route) {
                        RiwayatIuranScreen()
                    }

                    composable(Screen.Laporan.route) {
                        StatusLaporanScreen(
                            onLaporBaruClick = { navController.navigate(Screen.LaporMasalah.route) },
                            onDetailClick = { id -> navController.navigate(Screen.DetailLaporan.createRoute(id)) }
                        )
                    }

                    composable(Screen.Profil.route) {
                        ProfilKeluargaScreen(
                            onLogout = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // Warga Sub-Screens
                    composable(Screen.BayarIuran.route) {
                        BayarIuranScreen(
                            onBackClick = { navController.popBackStack() },
                            onPaymentSuccess = { navController.navigate(Screen.PembayaranBerhasil.route) }
                        )
                    }

                    composable(Screen.PembayaranBerhasil.route) {
                        PembayaranBerhasilScreen(
                            onDoneClick = {
                                navController.navigate(Screen.Beranda.route) {
                                    popUpTo(Screen.Beranda.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.LaporMasalah.route) {
                        LaporMasalahScreen(
                            onBackClick = { navController.popBackStack() },
                            onSubmitSuccess = { navController.navigate(Screen.LaporanTerkirim.route) }
                        )
                    }

                    composable(Screen.LaporanTerkirim.route) {
                        LaporanTerkirimScreen(
                            onDoneClick = {
                                navController.navigate(Screen.Laporan.route) {
                                    popUpTo(Screen.Beranda.route)
                                }
                            }
                        )
                    }

                    composable(Screen.DetailLaporan.route) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("laporanId") ?: "l1"
                        DetailLaporanScreen(
                            laporanId = id,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Pengumuman.route) {
                        PengumumanScreen(
                            onDetailClick = { id -> navController.navigate(Screen.DetailPengumuman.createRoute(id)) },
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.DetailPengumuman.route) { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("pengumumanId") ?: "p1"
                        DetailPengumumanScreen(
                            pengumumanId = id,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.BantuanSosial.route) {
                        BantuanSosialScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Notifikasi.route) {
                        NotifikasiScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    }
                }
            }
        }
    }
}
}
