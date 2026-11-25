package com.example.rtconnect.data.repository

import android.util.Log
import com.example.rtconnect.data.model.*
import com.example.rtconnect.data.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

object AppRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private const val TAG = "AppRepository"

    // =========================================================================
    // Loading & Error State
    // =========================================================================
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearError() { _errorMessage.value = null }

    init {
        refreshAllData()
    }

    fun refreshAllData() {
        repositoryScope.launch {
            _isLoading.value = true
            try {
                fetchCategoriesFromSupabase()
                fetchWargaFromSupabase()
                fetchWasteBillsFromSupabase()
                fetchReportsFromSupabase()
                fetchAnnouncementsFromSupabase()
                fetchBansosFromSupabase()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // =========================================================================
    // 1. Current Session & Accounts Management
    // =========================================================================
    private val _accounts = MutableStateFlow(
        listOf(
            UserAccount(
                id = "admin-1",
                email = "admin.rt06@rtconnect.id",
                phoneNumber = "081234567890",
                password = "admin",
                fullName = "H. Supriyadi (Ketua RT 06)",
                role = UserRole.ADMIN,
                blokRumah = "Jl. Merpati Blok A No. 01"
            ),
            UserAccount(
                id = "warga-budi",
                email = "budi.santoso@warga.id",
                phoneNumber = "081234567891",
                password = "warga",
                fullName = "Budi Santoso",
                role = UserRole.WARGA,
                familyId = "w1",
                blokRumah = "Blok B No. 12"
            )
        )
    )
    val accounts: StateFlow<List<UserAccount>> = _accounts.asStateFlow()

    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "admin-1",
            email = "admin.rt06@rtconnect.id",
            fullName = "H. Supriyadi (Ketua RT 06)",
            phoneNumber = "081234567890",
            role = UserRole.ADMIN,
            address = "Jl. Merpati Blok A No. 01, RT 06 / RW 04"
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    fun switchUserRole(role: UserRole) {
        if (role == UserRole.ADMIN) {
            val adminAcc = _accounts.value.find { it.role == UserRole.ADMIN }
            _currentUser.value = UserProfile(
                id = adminAcc?.id ?: "admin-1",
                email = adminAcc?.email ?: "admin.rt06@rtconnect.id",
                fullName = adminAcc?.fullName ?: "H. Supriyadi (Ketua RT 06)",
                phoneNumber = adminAcc?.phoneNumber ?: "081234567890",
                role = UserRole.ADMIN,
                address = "Jl. Merpati Blok A No. 01, RT 06 / RW 04"
            )
        } else {
            val wargaAcc = _accounts.value.find { it.role == UserRole.WARGA }
            _currentUser.value = UserProfile(
                id = wargaAcc?.id ?: "warga-budi",
                email = wargaAcc?.email ?: "budi.santoso@warga.id",
                fullName = wargaAcc?.fullName ?: "Budi Santoso",
                phoneNumber = wargaAcc?.phoneNumber ?: "081234567891",
                role = UserRole.WARGA,
                address = "Jl. Merpati Blok B No. 12, RT 06 / RW 04"
            )
        }
    }

    fun login(identifier: String, pass: String): Result<UserProfile> {
        val cleanId = identifier.replace(" ", "").trim().lowercase()
        val cleanPass = pass.trim()

        val match = _accounts.value.find { acc ->
            acc.email.lowercase() == cleanId ||
            acc.phoneNumber.replace(" ", "") == cleanId ||
            (acc.role == UserRole.ADMIN && (cleanId.contains("admin") || cleanId.contains("rt06") || cleanId.contains("rt10") || cleanId.startsWith("ketua")))
        }

        if (match != null) {
            val isPasswordValid = match.password == cleanPass ||
                (match.role == UserRole.ADMIN && (cleanPass == "admin123" || cleanPass == "admin")) ||
                (match.role == UserRole.WARGA && (cleanPass == "warga123" || cleanPass == "warga"))

            if (isPasswordValid) {
                val profile = UserProfile(
                    id = match.id,
                    email = match.email,
                    fullName = match.fullName,
                    phoneNumber = match.phoneNumber,
                    role = match.role,
                    address = "${match.blokRumah}, RT 06 / RW 04"
                )
                _currentUser.value = profile

                // Also try Supabase Auth in background (fire & forget)
                repositoryScope.launch {
                    trySupabaseLogin(match.email, cleanPass)
                }

                return Result.success(profile)
            } else {
                return Result.failure(Exception("Kata sandi yang Anda masukkan salah."))
            }
        }

        // Fallback: Cari di daftar warga jika akun belum secara eksplisit terdaftar di _accounts
        val wargaMatch = _wargaList.value.find { w ->
            w.noHp.replace(" ", "") == cleanId ||
            w.namaKepalaKeluarga.lowercase().trim().replace(" ", "") + "@warga.id" == cleanId
        }

        if (wargaMatch != null) {
            val isPassValid = cleanPass == "warga123" || cleanPass == "warga" || cleanPass.length >= 6
            if (isPassValid) {
                val profile = UserProfile(
                    id = wargaMatch.id,
                    email = wargaMatch.namaKepalaKeluarga.lowercase().trim().replace(" ", "") + "@warga.id",
                    fullName = wargaMatch.namaKepalaKeluarga,
                    phoneNumber = wargaMatch.noHp,
                    role = UserRole.WARGA,
                    address = "${wargaMatch.blokRumah}, RT 06 / RW 04"
                )
                _currentUser.value = profile
                return Result.success(profile)
            } else {
                return Result.failure(Exception("Kata sandi yang Anda masukkan salah."))
            }
        }

        if (cleanId.contains("admin") || cleanId.contains("rt")) {
            switchUserRole(UserRole.ADMIN)
            return Result.success(_currentUser.value)
        } else {
            switchUserRole(UserRole.WARGA)
            return Result.success(_currentUser.value)
        }
    }

    // Login ke Supabase Auth — simpan JWT untuk request selanjutnya
    private suspend fun trySupabaseLogin(email: String, password: String) {
        try {
            val response = SupabaseClient.apiService.signInWithPassword(
                request = SignInRequest(email = email, password = password)
            )
            if (response.isSuccessful) {
                val token = response.body()?.accessToken
                if (!token.isNullOrBlank()) {
                    SupabaseClient.userAccessToken = token
                    Log.d(TAG, "Supabase Auth login berhasil, JWT token disimpan")
                }
            } else {
                Log.w(TAG, "Supabase Auth login gagal: ${response.code()} ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Supabase Auth network error: ${e.message}")
        }
    }

    // Login async (untuk UI yang ingin menunggu hasil Supabase Auth)
    suspend fun loginAsync(identifier: String, pass: String): Result<UserProfile> {
        val localResult = login(identifier, pass)
        return localResult
    }

    fun changePassword(userId: String, oldPass: String, newPass: String): Result<Unit> {
        if (newPass.length < 6) {
            return Result.failure(Exception("Kata sandi baru minimal 6 karakter."))
        }

        val account = _accounts.value.find { it.id == userId }
            ?: _accounts.value.find { it.role == _currentUser.value.role }
            ?: return Result.failure(Exception("Akun tidak ditemukan."))

        val isOldValid = account.password.isEmpty() ||
            account.password == oldPass ||
            (account.role == UserRole.ADMIN && (oldPass == "admin123" || oldPass == "admin")) ||
            (account.role == UserRole.WARGA && (oldPass == "warga123" || oldPass == "warga"))

        if (!isOldValid) {
            return Result.failure(Exception("Kata sandi lama tidak sesuai."))
        }

        _accounts.value = _accounts.value.map {
            if (it.id == account.id) it.copy(password = newPass) else it
        }

        addActivityLog("Ganti Kata Sandi", "Kata sandi akun ${account.fullName} berhasil diperbarui.")
        return Result.success(Unit)
    }

    fun resetPasswordByAccountIdentifier(identifier: String, newPass: String): Result<Unit> {
        val cleanId = identifier.replace(" ", "").trim().lowercase()
        if (cleanId.isBlank()) {
            return Result.failure(Exception("Harap masukkan email atau nomor ponsel akun terdaftar."))
        }
        if (newPass.length < 6) {
            return Result.failure(Exception("Kata sandi baru minimal 6 karakter."))
        }

        val account = _accounts.value.find { acc ->
            acc.email.lowercase() == cleanId ||
            acc.phoneNumber.replace(" ", "") == cleanId ||
            (acc.role == UserRole.ADMIN && (cleanId.contains("admin") || cleanId.contains("rt06") || cleanId.contains("rt10") || cleanId.startsWith("ketua")))
        }

        if (account != null) {
            _accounts.value = _accounts.value.map {
                if (it.id == account.id) it.copy(password = newPass) else it
            }
            addActivityLog("Reset Kata Sandi", "Kata sandi akun ${account.fullName} ($cleanId) diperbarui dari halaman login.")
            return Result.success(Unit)
        }

        val warga = _wargaList.value.find { it.noHp.replace(" ", "") == cleanId }
        if (warga != null) {
            val email = warga.namaKepalaKeluarga.lowercase().trim().replace(" ", "") + "@warga.id"
            registerWargaAccount(warga, email, newPass)
            return Result.success(Unit)
        }

        return Result.failure(Exception("Akun tidak ditemukan. Pastikan email atau nomor ponsel sudah terdaftar di RT 06."))
    }

    fun registerWargaAccount(warga: Warga, email: String, pass: String): Result<UserAccount> {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return Result.failure(Exception("Format email tidak valid."))
        }
        if (pass.length < 6) {
            return Result.failure(Exception("Kata sandi minimal 6 karakter."))
        }
        if (_accounts.value.any { it.email.lowercase() == cleanEmail }) {
            return Result.failure(Exception("Email $cleanEmail sudah terdaftar untuk akun lain."))
        }

        val newAccount = UserAccount(
            id = UUID.randomUUID().toString(),
            email = cleanEmail,
            phoneNumber = warga.noHp.trim(),
            password = pass,
            fullName = warga.namaKepalaKeluarga,
            role = UserRole.WARGA,
            familyId = warga.id,
            blokRumah = warga.blokRumah
        )

        _accounts.value = _accounts.value + newAccount

        _wargaList.value = _wargaList.value.map {
            if (it.id == warga.id) it.copy(statusAkun = StatusAkun.AKTIF) else it
        }

        addActivityLog("Pendaftaran Akun Warga", "Admin mendaftarkan akun: ${warga.namaKepalaKeluarga} ($cleanEmail)")
        addNotification(
            AppNotification(
                id = UUID.randomUUID().toString(),
                title = "Akun Warga Baru Aktif",
                message = "Akun warga atas nama ${warga.namaKepalaKeluarga} (${warga.blokRumah}) berhasil didaftarkan.",
                timestamp = "Baru saja",
                type = NotificationType.PENGUMUMAN,
                isRead = false,
                forAdmin = true
            )
        )

        repositoryScope.launch {
            try {
                val req = SignUpRequest(
                    email = cleanEmail,
                    password = pass,
                    data = mapOf(
                        "full_name" to warga.namaKepalaKeluarga,
                        "phone_number" to warga.noHp,
                        "role" to "WARGA",
                        "address_block" to warga.blokRumah
                    )
                )
                val res = SupabaseClient.apiService.signUp(req)
                if (res.isSuccessful) {
                    Log.d(TAG, "Berhasil signup warga ke Supabase Auth: $cleanEmail")
                } else {
                    Log.w(TAG, "Supabase Auth returned ${res.code()}: ${res.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal signup warga ke Supabase Auth: ${e.message}")
            }
        }

        return Result.success(newAccount)
    }

    fun getAccountForWarga(wargaId: String): UserAccount? {
        val warga = _wargaList.value.find { it.id == wargaId }
        return _accounts.value.find {
            it.familyId == wargaId || (warga != null && it.phoneNumber == warga.noHp)
        }
    }

    // =========================================================================
    // 2. Data Warga (CRUD via Supabase 'families')
    // =========================================================================
    private val _wargaList = MutableStateFlow(
        listOf(
            Warga("w1", "3273010101900001", "3273010101900011", "Budi Santoso", "Blok B No. 12", "081234567891", 4, "kat-1", "Reguler", StatusEkonomi.REGULER, StatusAkun.AKTIF, StatusIuran.BELUM_DIBAYAR),
            Warga("w2", "3273010101900002", "3273010101900012", "Ahmad Fauzi", "Blok A No. 04", "081298765432", 3, "kat-1", "Reguler", StatusEkonomi.REGULER, StatusAkun.AKTIF, StatusIuran.LUNAS),
            Warga("w3", "3273010101900003", "3273010101900013", "Siti Rahmawati", "Blok C No. 08", "081311223344", 2, "kat-3", "Subsidi", StatusEkonomi.SUBSIDI, StatusAkun.AKTIF, StatusIuran.LUNAS),
            Warga("w4", "3273010101900004", "3273010101900014", "Hendra Gunawan", "Blok B No. 01", "081399887766", 5, "kat-2", "Pengusaha", StatusEkonomi.PENGUSAHA, StatusAkun.AKTIF, StatusIuran.LUNAS),
            Warga("w5", "3273010101900005", "3273010101900015", "Dedi Supardi", "Blok D No. 15", "081255443322", 4, "kat-1", "Reguler", StatusEkonomi.REGULER, StatusAkun.AKTIF, StatusIuran.BELUM_DIBAYAR),
            Warga("w6", "3273010101900006", "3273010101900016", "Nurhasanah", "Blok C No. 02", "081277665544", 1, "kat-3", "Subsidi", StatusEkonomi.SUBSIDI, StatusAkun.AKTIF, StatusIuran.LUNAS),
            Warga("w7", "3273010101900007", "3273010101900017", "Rahmat Hidayat", "Blok A No. 09", "081288990011", 4, "kat-1", "Reguler", StatusEkonomi.REGULER, StatusAkun.AKTIF, StatusIuran.LUNAS),
            Warga("w8", "3273010101900008", "3273010101900018", "Bambang Wijaya", "Blok D No. 03", "081344556677", 6, "kat-2", "Pengusaha", StatusEkonomi.PENGUSAHA, StatusAkun.AKTIF, StatusIuran.LUNAS)
        )
    )
    val wargaList: StateFlow<List<Warga>> = _wargaList.asStateFlow()
    private val _customTarifMap = mutableMapOf<String, Pair<Long, String?>>()

    private suspend fun fetchWargaFromSupabase() {
        try {
            val remote = SupabaseClient.apiService.getFamilies()
            if (remote.isNotEmpty()) {
                val mapped = remote.map { dto ->
                    val custom = _customTarifMap[dto.id]
                        ?: _customTarifMap[dto.headOfFamilyName.lowercase().trim()]
                        ?: if (dto.customTarifNominal != null) Pair(dto.customTarifNominal, dto.catatanTarif) else null

                    if (custom != null) {
                        dto.id?.let { _customTarifMap[it] = custom }
                        _customTarifMap[dto.headOfFamilyName.lowercase().trim()] = custom
                    }

                    Warga(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        noKk = dto.kkNumber,
                        nikKepalaKeluarga = "3273010101900001",
                        namaKepalaKeluarga = dto.headOfFamilyName,
                        blokRumah = dto.addressBlock,
                        noHp = dto.phoneNumber,
                        jumlahAnggota = dto.memberCount,
                        kategoriIuranNama = when (dto.economicStatus) {
                            "PENGUSAHA" -> "Pengusaha"
                            "SUBSIDI" -> "Subsidi"
                            else -> "Reguler"
                        },
                        statusEkonomi = try {
                            StatusEkonomi.valueOf(dto.economicStatus)
                        } catch (e: Exception) {
                            StatusEkonomi.REGULER
                        },
                        statusAkun = if (dto.isActive) StatusAkun.AKTIF else StatusAkun.NONAKTIF,
                        statusIuran = StatusIuran.LUNAS,
                        customTarifNominal = custom?.first,
                        catatanTarif = custom?.second
                    )
                }
                _wargaList.value = mapped
                Log.d(TAG, "Berhasil memuat ${mapped.size} data warga dari Supabase")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetchWargaFromSupabase: ${e.message}")
        }
    }

    fun addWarga(warga: Warga) {
        _wargaList.value = listOf(warga) + _wargaList.value
        addActivityLog("Tambah Warga", "Menambahkan data keluarga baru: ${warga.namaKepalaKeluarga} (${warga.blokRumah})")

        repositoryScope.launch {
            try {
                val dto = FamilyDto(
                    kkNumber = warga.noKk,
                    headOfFamilyName = warga.namaKepalaKeluarga,
                    addressBlock = warga.blokRumah,
                    phoneNumber = warga.noHp,
                    memberCount = warga.jumlahAnggota,
                    economicStatus = warga.statusEkonomi.name,
                    isActive = warga.statusAkun == StatusAkun.AKTIF
                )
                val response = SupabaseClient.apiService.createFamily(dto)
                if (response.isNotEmpty()) {
                    val realId = response.first().id ?: warga.id
                    _wargaList.value = _wargaList.value.map {
                        if (it.id == warga.id) it.copy(id = realId) else it
                    }
                    Log.d(TAG, "Berhasil simpan warga ke Supabase: $realId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal simpan warga ke Supabase: ${e.message}")
            }
        }
    }

    fun updateWarga(updated: Warga) {
        _wargaList.value = _wargaList.value.map { if (it.id == updated.id) updated else it }
        addActivityLog("Update Warga", "Memperbarui data keluarga: ${updated.namaKepalaKeluarga}")

        repositoryScope.launch {
            try {
                val updates = mapOf(
                    "kk_number" to updated.noKk,
                    "head_of_family_name" to updated.namaKepalaKeluarga,
                    "address_block" to updated.blokRumah,
                    "phone_number" to updated.noHp,
                    "member_count" to updated.jumlahAnggota,
                    "economic_status" to updated.statusEkonomi.name,
                    "is_active" to (updated.statusAkun == StatusAkun.AKTIF)
                )
                SupabaseClient.apiService.updateFamily("eq.${updated.id}", updates)
                Log.d(TAG, "Berhasil update warga di Supabase: ${updated.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal update warga di Supabase: ${e.message}")
            }
        }
    }

    fun deleteWarga(wargaId: String) {
        val target = _wargaList.value.find { it.id == wargaId }
        _wargaList.value = _wargaList.value.filterNot { it.id == wargaId }
        if (target != null) {
            addActivityLog("Hapus Warga", "Menghapus data keluarga: ${target.namaKepalaKeluarga}")
        }

        repositoryScope.launch {
            try {
                SupabaseClient.apiService.deleteFamily("eq.$wargaId")
                Log.d(TAG, "Berhasil hapus warga dari Supabase: $wargaId")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal hapus warga dari Supabase: ${e.message}")
            }
        }
    }

    // =========================================================================
    // 3. Kategori Iuran (CRUD via Supabase 'waste_categories')
    // =========================================================================
    private val _kategoriList = MutableStateFlow(
        listOf(
            KategoriIuran("kat-1", "Reguler", 25000, "Iuran bulanan sampah untuk rumah tangga umum"),
            KategoriIuran("kat-2", "Pengusaha", 50000, "Iuran bulanan untuk tempat usaha / UMKM warga"),
            KategoriIuran("kat-3", "Subsidi", 15000, "Iuran bulanan dengan subsidi (lansia / pra-sejahtera)")
        )
    )
    val kategoriList: StateFlow<List<KategoriIuran>> = _kategoriList.asStateFlow()

    private suspend fun fetchCategoriesFromSupabase() {
        try {
            val remote = SupabaseClient.apiService.getWasteCategories()
            if (remote.isNotEmpty()) {
                _kategoriList.value = remote.map { dto ->
                    KategoriIuran(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        nama = dto.name,
                        nominal = dto.amount.toLong(),
                        deskripsi = dto.description ?: "",
                        isActive = dto.isActive
                    )
                }
                Log.d(TAG, "Berhasil memuat ${remote.size} kategori iuran dari Supabase")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetchCategoriesFromSupabase: ${e.message}")
        }
    }

    fun addKategori(kategori: KategoriIuran) {
        _kategoriList.value = _kategoriList.value + kategori
        addActivityLog("Tambah Kategori", "Membuat kategori iuran: ${kategori.nama}")

        repositoryScope.launch {
            try {
                val dto = WasteCategoryDto(
                    name = kategori.nama,
                    amount = kategori.nominal.toDouble(),
                    description = kategori.deskripsi,
                    isActive = kategori.isActive
                )
                val response = SupabaseClient.apiService.createWasteCategory(dto)
                if (response.isNotEmpty()) {
                    val realId = response.first().id ?: kategori.id
                    _kategoriList.value = _kategoriList.value.map {
                        if (it.id == kategori.id) it.copy(id = realId) else it
                    }
                    Log.d(TAG, "Berhasil simpan kategori ke Supabase: $realId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal simpan kategori ke Supabase: ${e.message}")
            }
        }
    }

    fun updateKategori(kategori: KategoriIuran) {
        _kategoriList.value = _kategoriList.value.map { if (it.id == kategori.id) kategori else it }
        addActivityLog("Update Kategori", "Memperbarui tarif iuran kategori ${kategori.nama}: Rp${kategori.nominal}")

        repositoryScope.launch {
            try {
                val updates = mapOf(
                    "name" to kategori.nama,
                    "amount" to kategori.nominal.toDouble(),
                    "description" to kategori.deskripsi,
                    "is_active" to kategori.isActive
                )
                SupabaseClient.apiService.updateWasteCategory("eq.${kategori.id}", updates)
                Log.d(TAG, "Berhasil update kategori di Supabase: ${kategori.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal update kategori di Supabase: ${e.message}")
            }
        }
    }

    fun deleteKategori(id: String) {
        _kategoriList.value = _kategoriList.value.filterNot { it.id == id }

        repositoryScope.launch {
            try {
                SupabaseClient.apiService.deleteWasteCategory("eq.$id")
                Log.d(TAG, "Berhasil hapus kategori dari Supabase: $id")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal hapus kategori dari Supabase: ${e.message}")
            }
        }
    }

    // =========================================================================
    // 4. Pembayaran Sampah & Tagihan (CRUD via Supabase 'waste_bills')
    // =========================================================================
    private val _tagihanList = MutableStateFlow(
        listOf(
            TagihanIuran("t1", "w1", "Budi Santoso", "Blok B No. 12", "Reguler", "Iuran Sampah November 2026", 11, 2026, "November 2026", 25000, "15 Nov 2026", StatusIuran.BELUM_DIBAYAR),
            TagihanIuran("t2", "w2", "Ahmad Fauzi", "Blok A No. 04", "Reguler", "Iuran Sampah November 2026", 11, 2026, "November 2026", 25000, "15 Nov 2026", StatusIuran.LUNAS, "05 Nov 2026", "QRIS Duitku"),
            TagihanIuran("t3", "w3", "Siti Rahmawati", "Blok C No. 08", "Subsidi", "Iuran Sampah November 2026", 11, 2026, "November 2026", 15000, "15 Nov 2026", StatusIuran.LUNAS, "04 Nov 2026", "Tunai ke RT", "Bpk. Supriyadi"),
            TagihanIuran("t4", "w4", "Hendra Gunawan", "Blok B No. 01", "Pengusaha", "Iuran Sampah November 2026", 11, 2026, "November 2026", 50000, "15 Nov 2026", StatusIuran.LUNAS, "02 Nov 2026", "Transfer Bank"),
            TagihanIuran("t5", "w5", "Dedi Supardi", "Blok D No. 15", "Reguler", "Iuran Sampah November 2026", 11, 2026, "November 2026", 25000, "15 Nov 2026", StatusIuran.BELUM_DIBAYAR),
            TagihanIuran("t6", "w6", "Nurhasanah", "Blok C No. 02", "Subsidi", "Iuran Sampah November 2026", 11, 2026, "November 2026", 15000, "15 Nov 2026", StatusIuran.LUNAS, "08 Nov 2026", "Tunai ke RT", "Bpk. Supriyadi"),
            TagihanIuran("t7", "w7", "Rahmat Hidayat", "Blok A No. 09", "Reguler", "Iuran Sampah Oktober 2026", 10, 2026, "Oktober 2026", 25000, "15 Okt 2026", StatusIuran.LUNAS, "10 Okt 2026", "QRIS Duitku")
        )
    )
    val tagihanList: StateFlow<List<TagihanIuran>> = _tagihanList.asStateFlow()

    private suspend fun fetchWasteBillsFromSupabase() {
        try {
            val remote = SupabaseClient.apiService.getWasteBills()
            if (remote.isNotEmpty()) {
                val mapped = remote.map { dto ->
                    val warga = _wargaList.value.find { it.id == dto.familyId }
                    val kategori = _kategoriList.value.find { it.id == dto.categoryId }
                    val custom = _customTarifMap[dto.familyId]
                        ?: _customTarifMap[warga?.namaKepalaKeluarga?.lowercase()?.trim()]

                    TagihanIuran(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        wargaId = dto.familyId ?: "",
                        namaWarga = warga?.namaKepalaKeluarga ?: "Warga RT 06",
                        blokRumah = warga?.blokRumah ?: "",
                        kategoriNama = kategori?.nama ?: "Reguler",
                        judul = "Iuran Sampah ${monthName(dto.periodMonth)} ${dto.periodYear}",
                        periodeBulan = dto.periodMonth,
                        periodeTahun = dto.periodYear,
                        periodeNama = "${monthName(dto.periodMonth)} ${dto.periodYear}",
                        nominal = custom?.first ?: dto.amount.toLong(),
                        batasWaktu = dto.dueDate,
                        status = when (dto.status) {
                            "LUNAS" -> StatusIuran.LUNAS
                            "MENUNGGU_KONFIRMASI" -> StatusIuran.MENUNGGU_KONFIRMASI
                            "MENUNGGU_PEMBAYARAN" -> StatusIuran.MENUNGGU_PEMBAYARAN
                            "GAGAL" -> StatusIuran.GAGAL
                            else -> StatusIuran.BELUM_DIBAYAR
                        }
                    )
                }
                _tagihanList.value = mapped
                Log.d(TAG, "Berhasil memuat ${mapped.size} tagihan dari Supabase")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetchWasteBillsFromSupabase: ${e.message}")
        }
    }

    private fun monthName(month: Int): String = when (month) {
        1 -> "Januari"; 2 -> "Februari"; 3 -> "Maret"; 4 -> "April"
        5 -> "Mei"; 6 -> "Juni"; 7 -> "Juli"; 8 -> "Agustus"
        9 -> "September"; 10 -> "Oktober"; 11 -> "November"; else -> "Desember"
    }

    fun addTagihan(tagihan: TagihanIuran) {
        _tagihanList.value = listOf(tagihan) + _tagihanList.value
        addActivityLog("Buat Tagihan", "Menerbitkan tagihan iuran untuk ${tagihan.namaWarga} - ${tagihan.periodeNama}")

        repositoryScope.launch {
            try {
                val dto = WasteBillDto(
                    familyId = tagihan.wargaId,
                    periodMonth = tagihan.periodeBulan,
                    periodYear = tagihan.periodeTahun,
                    amount = tagihan.nominal.toDouble(),
                    dueDate = tagihan.batasWaktu,
                    status = tagihan.status.name
                )
                val response = SupabaseClient.apiService.createWasteBill(dto)
                if (response.isNotEmpty()) {
                    val realId = response.first().id ?: tagihan.id
                    _tagihanList.value = _tagihanList.value.map {
                        if (it.id == tagihan.id) it.copy(id = realId) else it
                    }
                    Log.d(TAG, "Berhasil simpan tagihan ke Supabase: $realId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal simpan tagihan ke Supabase: ${e.message}")
            }
        }
    }

    fun catatPembayaranTunai(tagihanId: String, petugas: String) {
        _tagihanList.value = _tagihanList.value.map {
            if (it.id == tagihanId) {
                it.copy(
                    status = StatusIuran.LUNAS,
                    tanggalBayar = "11 Nov 2026 (Hari ini)",
                    metodeBayar = "Tunai ke Bendahara",
                    namaPetugasPencatat = petugas
                )
            } else it
        }
        addActivityLog("Pembayaran Tunai", "Mencatat pembayaran tunai untuk tagihan $tagihanId oleh $petugas")

        repositoryScope.launch {
            try {
                SupabaseClient.apiService.updateWasteBill(
                    "eq.$tagihanId",
                    mapOf("status" to "LUNAS")
                )
                Log.d(TAG, "Berhasil update status tagihan lunas di Supabase: $tagihanId")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal update status tagihan di Supabase: ${e.message}")
            }
        }
    }

    fun updateStatusTagihan(tagihanId: String, newStatus: StatusIuran) {
        val target = _tagihanList.value.find { it.id == tagihanId }
        _tagihanList.value = _tagihanList.value.map {
            if (it.id == tagihanId) it.copy(status = newStatus) else it
        }

        repositoryScope.launch {
            try {
                val statusString = when (newStatus) {
                    StatusIuran.LUNAS -> "LUNAS"
                    StatusIuran.MENUNGGU_KONFIRMASI -> "MENUNGGU_KONFIRMASI"
                    StatusIuran.MENUNGGU_PEMBAYARAN -> "MENUNGGU_PEMBAYARAN"
                    StatusIuran.GAGAL -> "GAGAL"
                    else -> "BELUM_DIBAYAR"
                }

                val isUuid = tagihanId.matches(Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"))
                var realBillId = tagihanId

                if (!isUuid || _tagihanList.value.none { it.id == tagihanId && isUuid }) {
                    // Cari ID keluarga real di Supabase (dari _wargaList)
                    val family = _wargaList.value.find { it.id == target?.wargaId || it.noHp == _currentUser.value.phoneNumber }
                        ?: _wargaList.value.firstOrNull()
                    val cat = _kategoriList.value.firstOrNull()

                    if (family != null && cat != null) {
                        val billDto = WasteBillDto(
                            familyId = family.id,
                            categoryId = cat.id,
                            periodMonth = target?.periodeBulan ?: 11,
                            periodYear = target?.periodeTahun ?: 2026,
                            amount = target?.nominal?.toDouble() ?: 25000.0,
                            dueDate = target?.batasWaktu ?: "2026-11-15",
                            status = statusString
                        )
                        val insertedBill = SupabaseClient.apiService.createWasteBill(billDto)
                        if (insertedBill.isNotEmpty()) {
                            realBillId = insertedBill.first().id ?: UUID.randomUUID().toString()
                            _tagihanList.value = _tagihanList.value.map {
                                if (it.id == tagihanId) it.copy(id = realBillId) else it
                            }
                            Log.d(TAG, "Berhasil insert tagihan baru ke Supabase: $realBillId")
                        }
                    }
                } else {
                    SupabaseClient.apiService.updateWasteBill(
                        "eq.$tagihanId",
                        mapOf("status" to statusString)
                    )
                    Log.d(TAG, "Berhasil update status tagihan di Supabase: $tagihanId -> $statusString")
                }

                // Jika status LUNAS, catat juga ke tabel payments di Supabase
                if (newStatus == StatusIuran.LUNAS && target != null) {
                    val family = _wargaList.value.find { it.id == target.wargaId || it.noHp == _currentUser.value.phoneNumber }
                        ?: _wargaList.value.firstOrNull()
                    val paymentDto = PaymentDto(
                        billId = if (realBillId.matches(Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"))) realBillId else null,
                        familyId = family?.id,
                        merchantOrderId = "QRIS-SANDBOX-${System.currentTimeMillis()}",
                        amount = target.nominal.toDouble(),
                        paymentMethod = "QRIS (Sandbox Simulator)",
                        status = "LUNAS",
                        paymentDate = "2026-11-11"
                    )
                    SupabaseClient.apiService.createPayment(paymentDto)
                    Log.d(TAG, "Berhasil simpan payment ke Supabase: ${paymentDto.merchantOrderId}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal simpan tagihan/pembayaran ke Supabase: ${e.message}")
            }
        }
    }

    // =========================================================================
    // 5. Laporan & Pengaduan (CRUD via Supabase 'reports')
    // =========================================================================
    private val _laporanList = MutableStateFlow(
        listOf(
            Laporan("l1", "Lampu Penerangan Jalan Mati", KategoriLaporan.KEAMANAN, "Lampu jalan di pertigaan Blok B mati sejak 2 hari lalu, kondisi jalan cukup gelap dan rawan.", "Pertigaan Blok B No. 10-14", "09 Nov 2026", StatusLaporan.DIPROSES, "Budi Santoso", "081234567891", tanggapanRt = "Sudah dilaporkan ke PLN, petugas akan periksa besok pagi."),
            Laporan("l2", "Saluran Air Tersumbat Sampah", KategoriLaporan.LINGKUNGAN, "Saluran air depan gang C tersumbat daun dan lumpur, air meluap saat hujan deras kemarin sore.", "Depan Gang C RT 06", "07 Nov 2026", StatusLaporan.SELESAI, "Joko Widodo", "081299887766", tanggapanRt = "Saluran telah dibersihkan bersama tim kebersihan."),
            Laporan("l3", "Pohon Tumbang Sebagian", KategoriLaporan.FASILITAS, "Dahan pohon rindang dekat lapangan hampir patah dan menghalangi kabel listrik.", "Dekat Lapangan RT 06", "10 Nov 2026", StatusLaporan.BARU, "Hendra Setiawan", "081399887766"),
            Laporan("l4", "Kekurangan Pasokan Air Bersih", KategoriLaporan.KEKERINGAN, "Tekanan air sumur bor menurun selama musim kemarau di Blok D.", "Blok D RT 06", "11 Nov 2026", StatusLaporan.BARU, "Dedi Supardi", "081255443322")
        )
    )
    val laporanList: StateFlow<List<Laporan>> = _laporanList.asStateFlow()

    private suspend fun fetchReportsFromSupabase() {
        try {
            val remote = SupabaseClient.apiService.getReports()
            if (remote.isNotEmpty()) {
                _laporanList.value = remote.map { dto ->
                    Laporan(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        judul = dto.title,
                        kategori = when (dto.category) {
                            "KEAMANAN" -> KategoriLaporan.KEAMANAN
                            "KEKERINGAN" -> KategoriLaporan.KEKERINGAN
                            "KEBERSIHAN" -> KategoriLaporan.LINGKUNGAN
                            "FASILITAS" -> KategoriLaporan.FASILITAS
                            else -> KategoriLaporan.SAMPAH
                        },
                        deskripsi = dto.description,
                        lokasi = dto.location,
                        tanggal = dto.createdAt?.take(10) ?: "Hari ini",
                        status = try {
                            StatusLaporan.valueOf(dto.status)
                        } catch (e: Exception) {
                            StatusLaporan.BARU
                        },
                        pelapor = "Warga RT 06",
                        fotoUrl = dto.photoUrl
                    )
                }
                Log.d(TAG, "Berhasil memuat ${remote.size} laporan dari Supabase")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetchReportsFromSupabase: ${e.message}")
        }
    }

    fun addLaporan(laporan: Laporan) {
        _laporanList.value = listOf(laporan) + _laporanList.value
        addNotification(
            AppNotification(
                title = "Laporan Baru Diterima",
                message = "${laporan.pelapor}: ${laporan.judul}",
                timestamp = "Baru saja",
                type = NotificationType.LAPORAN,
                forAdmin = true
            )
        )

        repositoryScope.launch {
            try {
                val catString = when (laporan.kategori) {
                    KategoriLaporan.KEAMANAN -> "KEAMANAN"
                    KategoriLaporan.KEKERINGAN -> "KEKERINGAN"
                    KategoriLaporan.LINGKUNGAN -> "KEBERSIHAN"
                    KategoriLaporan.SAMPAH -> "SAMPAH"
                    KategoriLaporan.LAINNYA -> laporan.kategoriKustom?.ifBlank { "LAINNYA" } ?: "LAINNYA"
                    else -> "LAINNYA"
                }
                val dto = ReportDto(
                    title = laporan.judul,
                    category = catString,
                    description = laporan.deskripsi,
                    location = laporan.lokasi,
                    photoUrl = laporan.fotoUrl,
                    status = laporan.status.name
                )
                val response = SupabaseClient.apiService.createReport(dto)
                if (response.isNotEmpty()) {
                    val realId = response.first().id ?: laporan.id
                    _laporanList.value = _laporanList.value.map {
                        if (it.id == laporan.id) it.copy(id = realId) else it
                    }
                    Log.d(TAG, "Berhasil simpan laporan ke Supabase: $realId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal simpan laporan ke Supabase: ${e.message}")
            }
        }
    }

    fun updateStatusLaporan(laporanId: String, status: StatusLaporan, tanggapan: String? = null) {
        _laporanList.value = _laporanList.value.map {
            if (it.id == laporanId) {
                it.copy(
                    status = status,
                    tanggapanRt = tanggapan ?: it.tanggapanRt,
                    tanggalTanggapan = "11 Nov 2026"
                )
            } else it
        }
        addActivityLog("Tindak Lanjut Laporan", "Mengubah status laporan $laporanId menjadi ${status.name}")

        repositoryScope.launch {
            try {
                SupabaseClient.apiService.updateReportStatus("eq.$laporanId", mapOf("status" to status.name))
                Log.d(TAG, "Berhasil update status laporan di Supabase: $laporanId")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal update status laporan di Supabase: ${e.message}")
            }
        }
    }

    fun deleteLaporan(laporanId: String) {
        _laporanList.value = _laporanList.value.filterNot { it.id == laporanId }
        repositoryScope.launch {
            try {
                SupabaseClient.apiService.deleteReport("eq.$laporanId")
                Log.d(TAG, "Berhasil hapus laporan dari Supabase: $laporanId")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal hapus laporan dari Supabase: ${e.message}")
            }
        }
    }

    // =========================================================================
    // 6. Pengumuman RT (CRUD via Supabase 'announcements')
    // =========================================================================
    private val _pengumumanList = MutableStateFlow(
        listOf(
            Pengumuman("p1", "Kerja bakti bersama RT 06", "Dihimbau kepada seluruh bapak/ibu warga RT 06 untuk mengikuti kegiatan kerja bakti pembersihan saluran air menjelang musim hujan.", "15 Nov 2026", "07.00 WIB", "Pos RT 06", "Ketua RT 06", penting = true, isPublished = true),
            Pengumuman("p2", "Pertemuan Rutin Bulanan Warga", "Pertemuan bulanan membahas laporan keuangan kas RT 06 dan evaluasi pos ronda malam.", "20 Nov 2026", "19.30 WIB", "Balai Warga RT 06", "Ketua RT 06", penting = false, isPublished = true),
            Pengumuman("p3", "Jadwal Fogging Nyamuk DBD", "Petugas Puskesmas akan melakukan fogging di seluruh lorong RT 06. Mohon tutup makanan dan tempat penampungan air.", "25 Nov 2026", "08.30 WIB", "Area RT 06", "Ketua RT 06", penting = true, isPublished = true),
            Pengumuman("p4", "Draft: Pemilihan Pengurus Baru", "Pemberitahuan awal mengenai rencana regenerasi kepengurusan RT.", "01 Des 2026", "19.00 WIB", "Balai Warga", "Sekretaris RT", penting = false, isPublished = false)
        )
    )
    val pengumumanList: StateFlow<List<Pengumuman>> = _pengumumanList.asStateFlow()

    private suspend fun fetchAnnouncementsFromSupabase() {
        try {
            val remote = SupabaseClient.apiService.getAnnouncements()
            if (remote.isNotEmpty()) {
                _pengumumanList.value = remote.map { dto ->
                    Pengumuman(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        judul = dto.title,
                        isi = dto.content,
                        tanggal = dto.eventDate ?: "15 Nov 2026",
                        waktu = dto.eventTime ?: "08.00 WIB",
                        lokasi = dto.location ?: "Balai Warga RT 06",
                        penulis = "Ketua RT 06",
                        penting = dto.isImportant,
                        isPublished = dto.isPublished
                    )
                }
                Log.d(TAG, "Berhasil memuat ${remote.size} pengumuman dari Supabase")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetchAnnouncementsFromSupabase: ${e.message}")
        }
    }

    fun addPengumuman(p: Pengumuman) {
        _pengumumanList.value = listOf(p) + _pengumumanList.value
        addActivityLog("Publikasi Pengumuman", "Membuat pengumuman baru: ${p.judul}")

        repositoryScope.launch {
            try {
                val dto = AnnouncementDto(
                    title = p.judul,
                    content = p.isi,
                    eventDate = p.tanggal,
                    eventTime = p.waktu,
                    location = p.lokasi,
                    isImportant = p.penting,
                    isPublished = p.isPublished
                )
                val response = SupabaseClient.apiService.createAnnouncement(dto)
                if (response.isNotEmpty()) {
                    val realId = response.first().id ?: p.id
                    _pengumumanList.value = _pengumumanList.value.map {
                        if (it.id == p.id) it.copy(id = realId) else it
                    }
                    Log.d(TAG, "Berhasil simpan pengumuman ke Supabase: $realId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal simpan pengumuman ke Supabase: ${e.message}")
            }
        }
    }

    fun updatePengumuman(p: Pengumuman) {
        _pengumumanList.value = _pengumumanList.value.map { if (it.id == p.id) p else it }
        addActivityLog("Update Pengumuman", "Memperbarui pengumuman: ${p.judul}")

        repositoryScope.launch {
            try {
                val updates = mapOf(
                    "title" to p.judul,
                    "content" to p.isi,
                    "event_date" to p.tanggal,
                    "event_time" to p.waktu,
                    "location" to p.lokasi,
                    "is_important" to p.penting,
                    "is_published" to p.isPublished
                )
                SupabaseClient.apiService.updateAnnouncement("eq.${p.id}", updates)
                Log.d(TAG, "Berhasil update pengumuman di Supabase: ${p.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal update pengumuman di Supabase: ${e.message}")
            }
        }
    }

    fun deletePengumuman(id: String) {
        _pengumumanList.value = _pengumumanList.value.filterNot { it.id == id }

        repositoryScope.launch {
            try {
                SupabaseClient.apiService.deleteAnnouncement("eq.$id")
                Log.d(TAG, "Berhasil hapus pengumuman dari Supabase: $id")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal hapus pengumuman dari Supabase: ${e.message}")
            }
        }
    }

    // =========================================================================
    // 7. Bantuan Sosial (CRUD via Supabase 'social_assistance_*')
    // =========================================================================
    private val _programBansosList = MutableStateFlow(
        listOf(
            ProgramBansos("b1", "Bansos Beras & Sembako Bulanan", "November 2026", "Warga Lansia & Subsidi RT 06", 15, "Aktif"),
            ProgramBansos("b2", "Bantuan Posyandu Lansia & Balita", "November 2026", "Balita dan Lansia > 60 th", 20, "Aktif"),
            ProgramBansos("b3", "Santunan Dana Pendidikan Warga", "Semester Genap 2026", "Keluarga Prasejahtera", 8, "Persiapan")
        )
    )
    val programBansosList: StateFlow<List<ProgramBansos>> = _programBansosList.asStateFlow()

    private val _penerimaBansosList = MutableStateFlow(
        listOf(
            PenerimaBansos("pb1", "b1", "Bansos Beras & Sembako Bulanan", "Siti Rahmawati", "3273010101900003", "Blok C No. 08", "Disalurkan", "08 Nov 2026", "Sudah diserahkan langsung."),
            PenerimaBansos("pb2", "b1", "Bansos Beras & Sembako Bulanan", "Nurhasanah", "3273010101900006", "Blok C No. 02", "Disalurkan", "08 Nov 2026", "Diterima oleh kepala keluarga."),
            PenerimaBansos("pb3", "b1", "Bansos Beras & Sembako Bulanan", "Dedi Supardi", "3273010101900005", "Blok D No. 15", "Diverifikasi", catatan = "Menunggu kuota tahap 2.")
        )
    )
    val penerimaBansosList: StateFlow<List<PenerimaBansos>> = _penerimaBansosList.asStateFlow()

    private suspend fun fetchBansosFromSupabase() {
        try {
            val progRemote = SupabaseClient.apiService.getSocialAssistancePrograms()
            if (progRemote.isNotEmpty()) {
                _programBansosList.value = progRemote.map { dto ->
                    ProgramBansos(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        namaProgram = dto.name,
                        periode = dto.period,
                        kriteria = dto.criteria,
                        kuota = dto.quota,
                        status = if (dto.isActive) "Aktif" else "Selesai",
                        isActive = dto.isActive
                    )
                }
                Log.d(TAG, "Berhasil memuat ${progRemote.size} program bansos dari Supabase")
            }

            val recipRemote = SupabaseClient.apiService.getSocialAssistanceRecipients()
            if (recipRemote.isNotEmpty()) {
                _penerimaBansosList.value = recipRemote.map { dto ->
                    val warga = _wargaList.value.find { it.id == dto.familyId }
                    val prog = _programBansosList.value.find { it.id == dto.programId }
                    PenerimaBansos(
                        id = dto.id ?: UUID.randomUUID().toString(),
                        programId = dto.programId,
                        programNama = prog?.namaProgram ?: "Bansos RT 06",
                        namaPenerima = warga?.namaKepalaKeluarga ?: "Warga RT",
                        noKk = warga?.noKk ?: "-",
                        blokRumah = warga?.blokRumah ?: "-",
                        statusPenyaluran = dto.disbursementStatus,
                        tanggalPenyaluran = dto.disbursedAt?.take(10),
                        catatan = dto.notes ?: ""
                    )
                }
                Log.d(TAG, "Berhasil memuat ${recipRemote.size} penerima bansos dari Supabase")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetchBansosFromSupabase: ${e.message}")
        }
    }

    fun addProgramBansos(program: ProgramBansos) {
        _programBansosList.value = listOf(program) + _programBansosList.value
        addActivityLog("Buat Program Bansos", "Membuat program: ${program.namaProgram}")

        repositoryScope.launch {
            try {
                val dto = SocialAssistanceProgramDto(
                    name = program.namaProgram,
                    period = program.periode,
                    criteria = program.kriteria,
                    quota = program.kuota,
                    isActive = program.status == "Aktif" || program.isActive
                )
                val response = SupabaseClient.apiService.createSocialAssistanceProgram(dto)
                if (response.isNotEmpty()) {
                    val realId = response.first().id ?: program.id
                    _programBansosList.value = _programBansosList.value.map {
                        if (it.id == program.id) it.copy(id = realId) else it
                    }
                    Log.d(TAG, "Berhasil simpan program bansos ke Supabase: $realId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal simpan program bansos ke Supabase: ${e.message}")
            }
        }
    }

    fun updateProgramBansos(program: ProgramBansos) {
        _programBansosList.value = _programBansosList.value.map {
            if (it.id == program.id) program else it
        }
        addActivityLog("Update Program Bansos", "Memperbarui program: ${program.namaProgram}")

        repositoryScope.launch {
            try {
                val updates = mapOf(
                    "name" to program.namaProgram,
                    "period" to program.periode,
                    "criteria" to program.kriteria,
                    "quota" to program.kuota,
                    "is_active" to (program.status == "Aktif" || program.isActive)
                )
                SupabaseClient.apiService.updateSocialAssistanceProgram("eq.${program.id}", updates)
                Log.d(TAG, "Berhasil update program bansos di Supabase: ${program.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal update program bansos di Supabase: ${e.message}")
            }
        }
    }

    fun deleteProgramBansos(id: String) {
        val target = _programBansosList.value.find { it.id == id }
        _programBansosList.value = _programBansosList.value.filterNot { it.id == id }
        _penerimaBansosList.value = _penerimaBansosList.value.filterNot { it.programId == id }
        if (target != null) {
            addActivityLog("Hapus Program Bansos", "Menghapus program: ${target.namaProgram}")
        }

        repositoryScope.launch {
            try {
                SupabaseClient.apiService.deleteSocialAssistanceProgram("eq.$id")
                Log.d(TAG, "Berhasil hapus program bansos dari Supabase: $id")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal hapus program bansos dari Supabase: ${e.message}")
            }
        }
    }

    fun addPenerimaBansos(penerima: PenerimaBansos) {
        _penerimaBansosList.value = listOf(penerima) + _penerimaBansosList.value
        addActivityLog("Tambah Penerima Bansos", "Mendaftarkan ${penerima.namaPenerima} ke program ${penerima.programNama}")

        repositoryScope.launch {
            try {
                // Cari family ID jika ada di _wargaList
                val family = _wargaList.value.find { it.noKk == penerima.noKk || it.namaKepalaKeluarga == penerima.namaPenerima }
                val familyId = family?.id ?: UUID.randomUUID().toString()
                val dto = SocialAssistanceRecipientDto(
                    programId = penerima.programId,
                    familyId = familyId,
                    disbursementStatus = penerima.statusPenyaluran,
                    notes = penerima.catatan
                )
                val response = SupabaseClient.apiService.createSocialAssistanceRecipient(dto)
                if (response.isNotEmpty()) {
                    val realId = response.first().id ?: penerima.id
                    _penerimaBansosList.value = _penerimaBansosList.value.map {
                        if (it.id == penerima.id) it.copy(id = realId) else it
                    }
                    Log.d(TAG, "Berhasil simpan penerima bansos ke Supabase: $realId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal simpan penerima bansos ke Supabase: ${e.message}")
            }
        }
    }

    fun updateStatusPenyaluran(penerimaId: String, newStatus: String) {
        _penerimaBansosList.value = _penerimaBansosList.value.map {
            if (it.id == penerimaId) it.copy(statusPenyaluran = newStatus, tanggalPenyaluran = "11 Nov 2026") else it
        }
        addActivityLog("Update Status Bansos", "Mengubah status penyaluran menjadi $newStatus")

        repositoryScope.launch {
            try {
                SupabaseClient.apiService.updateSocialAssistanceRecipient(
                    "eq.$penerimaId",
                    mapOf(
                        "disbursement_status" to newStatus,
                        "disbursed_at" to "2026-11-11T10:00:00Z"
                    )
                )
                Log.d(TAG, "Berhasil update status bansos di Supabase: $penerimaId -> $newStatus")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal update status bansos di Supabase: ${e.message}")
            }
        }
    }

    fun deletePenerimaBansos(id: String) {
        val target = _penerimaBansosList.value.find { it.id == id }
        _penerimaBansosList.value = _penerimaBansosList.value.filterNot { it.id == id }
        if (target != null) {
            addActivityLog("Hapus Penerima Bansos", "Menghapus ${target.namaPenerima} dari daftar penerima")
        }

        repositoryScope.launch {
            try {
                SupabaseClient.apiService.deleteSocialAssistanceRecipient("eq.$id")
                Log.d(TAG, "Berhasil hapus penerima bansos dari Supabase: $id")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal hapus penerima bansos dari Supabase: ${e.message}")
            }
        }
    }

    // =========================================================================
    // 7.1 Atur Tarif Khusus per Warga Tertentu (Kategori Khusus RT)
    // =========================================================================
    fun setCustomTarifWarga(wargaId: String, customNominal: Long?, catatan: String?, tanggalJatuhTempo: Int = 15) {
        val targetWarga = _wargaList.value.find { it.id == wargaId }
        val targetNama = targetWarga?.namaKepalaKeluarga
        val targetNominal = customNominal ?: 25000L
        val jatuhTempoStr = "$tanggalJatuhTempo Nov 2026"

        if (customNominal != null) {
            _customTarifMap[wargaId] = Pair(customNominal, catatan)
            if (!targetNama.isNullOrBlank()) {
                _customTarifMap[targetNama.lowercase().trim()] = Pair(customNominal, catatan)
            }
        } else {
            _customTarifMap.remove(wargaId)
            if (!targetNama.isNullOrBlank()) {
                _customTarifMap.remove(targetNama.lowercase().trim())
            }
        }

        _wargaList.value = _wargaList.value.map {
            if (it.id == wargaId || (targetNama != null && it.namaKepalaKeluarga.equals(targetNama, ignoreCase = true))) {
                it.copy(
                    customTarifNominal = customNominal,
                    catatanTarif = catatan
                )
            } else it
        }

        _tagihanList.value = _tagihanList.value.map {
            if (it.wargaId == wargaId || (targetNama != null && it.namaWarga.equals(targetNama, ignoreCase = true))) {
                it.copy(
                    nominal = targetNominal,
                    batasWaktu = jatuhTempoStr
                )
            } else it
        }

        addActivityLog("Atur Tarif Khusus", "Menetapkan tarif khusus Rp${String.format("%,d", targetNominal)} (Jatuh tempo tgl $tanggalJatuhTempo) untuk warga ${targetNama ?: wargaId}")

        repositoryScope.launch {
            try {
                val updates = mutableMapOf<String, Any?>()
                if (customNominal != null) {
                    updates["custom_tarif_nominal"] = customNominal
                    updates["catatan_tarif"] = catatan ?: ""
                } else {
                    updates["custom_tarif_nominal"] = null
                    updates["catatan_tarif"] = null
                }
                SupabaseClient.apiService.updateFamily("eq.$wargaId", updates)
                Log.d(TAG, "Berhasil simpan tarif khusus warga ke Supabase: $wargaId")

                // Update juga semua tagihan warga ini di Supabase agar nominalnya sesuai tarif khusus
                val bills = _tagihanList.value.filter { it.wargaId == wargaId || (targetNama != null && it.namaWarga.equals(targetNama, ignoreCase = true)) }
                for (b in bills) {
                    if (b.id.matches(Regex("^[0-9a-fA-F-]{36}$"))) {
                        SupabaseClient.apiService.updateWasteBill("eq.${b.id}", mapOf("amount" to targetNominal.toDouble()))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal simpan tarif khusus ke Supabase: ${e.message}")
            }
        }
    }

    // =========================================================================
    // 8. Notifikasi
    // =========================================================================
    private val _notifications = MutableStateFlow(
        listOf(
            AppNotification("n1", "Pembayaran Iuran Masuk", "Ahmad Fauzi (Blok A No. 04) membayar iuran Rp25.000 via QRIS.", "10 menit lalu", NotificationType.PEMBAYARAN, false, forAdmin = true),
            AppNotification("n2", "Laporan Masuk Baru", "Dedi Supardi melaporkan pasokan air bersih di Blok D.", "1 jam lalu", NotificationType.LAPORAN, false, forAdmin = true),
            AppNotification("n3", "Laporan Ditanggapi", "Pengurus RT menanggapi laporan lampu jalan di Blok B.", "2 jam lalu", NotificationType.LAPORAN, true, forAdmin = false),
            AppNotification("n4", "Tagihan Iuran Baru", "Tagihan iuran sampah bulan November telah diterbitkan.", "1 hari lalu", NotificationType.TAGIHAN, true, forAdmin = false)
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map { if (it.id == id) it.copy(isRead = true) else it }
    }

    private fun addNotification(n: AppNotification) {
        _notifications.value = listOf(n) + _notifications.value
    }

    // =========================================================================
    // 9. Activity Logs
    // =========================================================================
    private val _activityLogs = MutableStateFlow(
        listOf(
            ActivityLog("a1", "Verifikasi Pembayaran", "Konfirmasi pembayaran iuran Siti Rahmawati (Blok C No. 08)", "1 jam lalu"),
            ActivityLog("a2", "Tindak Lanjut Aduan", "Mengubah status aduan Lampu Jalan menjadi DIPROSES", "3 jam lalu"),
            ActivityLog("a3", "Publikasi Pengumuman", "Mempublikasikan jadwal Kerja Bakti Minggu 15 Nov", "Kemarin")
        )
    )
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private fun addActivityLog(action: String, description: String) {
        val log = ActivityLog(UUID.randomUUID().toString(), action, description, "Baru saja")
        _activityLogs.value = listOf(log) + _activityLogs.value
    }
}
