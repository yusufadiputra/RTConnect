-- ==============================================================
-- RT CONNECT SUPABASE DATABASE SCHEMA & RLS POLICIES
-- ==============================================================

-- 1. ENUM TYPES
CREATE TYPE user_role AS ENUM ('ADMIN', 'WARGA');
CREATE TYPE economic_status AS ENUM ('REGULER', 'SUBSIDI', 'PENGUSAHA');
CREATE TYPE bill_status AS ENUM ('BELUM_DIBAYAR', 'MENUNGGU_PEMBAYARAN', 'MENUNGGU_KONFIRMASI', 'LUNAS', 'GAGAL');
CREATE TYPE report_category AS ENUM ('SAMPAH', 'KEKERINGAN', 'KEAMANAN', 'KEBERSIHAN', 'LAINNYA');
CREATE TYPE report_status AS ENUM ('BARU', 'DIPROSES', 'SELESAI', 'DITOLAK');

-- 2. PROFILES
CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT UNIQUE,
    full_name TEXT NOT NULL,
    phone_number TEXT UNIQUE NOT NULL,
    role user_role DEFAULT 'WARGA',
    avatar_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. FAMILIES
CREATE TABLE IF NOT EXISTS families (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    kk_number VARCHAR(16) UNIQUE NOT NULL,
    head_of_family_name TEXT NOT NULL,
    address_block VARCHAR(50) NOT NULL,
    rt_rw VARCHAR(10) DEFAULT '010/004',
    economic_status economic_status DEFAULT 'REGULER',
    member_count INT DEFAULT 1,
    phone_number TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. RESIDENTS
CREATE TABLE IF NOT EXISTS residents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID REFERENCES families(id) ON DELETE CASCADE,
    nik VARCHAR(16) UNIQUE NOT NULL,
    full_name TEXT NOT NULL,
    gender VARCHAR(10) CHECK (gender IN ('L', 'P')),
    relationship_to_head TEXT NOT NULL,
    birth_date DATE,
    phone_number TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5. WASTE_CATEGORIES
CREATE TABLE IF NOT EXISTS waste_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. WASTE_BILLS
CREATE TABLE IF NOT EXISTS waste_bills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    family_id UUID REFERENCES families(id) ON DELETE RESTRICT,
    category_id UUID REFERENCES waste_categories(id) ON DELETE RESTRICT,
    period_month INT NOT NULL,
    period_year INT NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    due_date DATE NOT NULL,
    status bill_status DEFAULT 'BELUM_DIBAYAR',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (family_id, period_month, period_year)
);

-- 7. PAYMENTS
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bill_id UUID REFERENCES waste_bills(id) ON DELETE RESTRICT,
    family_id UUID REFERENCES families(id) ON DELETE RESTRICT,
    merchant_order_id TEXT UNIQUE NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    payment_method TEXT NOT NULL,
    status bill_status DEFAULT 'MENUNGGU_PEMBAYARAN',
    payment_url TEXT,
    payment_date TIMESTAMPTZ,
    recorded_by_admin_id UUID REFERENCES profiles(id),
    receipt_image_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 8. REPORTS
CREATE TABLE IF NOT EXISTS reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reporter_id UUID REFERENCES profiles(id) ON DELETE SET NULL,
    family_id UUID REFERENCES families(id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    category report_category NOT NULL,
    description TEXT NOT NULL,
    location TEXT NOT NULL,
    photo_url TEXT,
    status report_status DEFAULT 'BARU',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 9. REPORT_RESPONSES
CREATE TABLE IF NOT EXISTS report_responses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id UUID REFERENCES reports(id) ON DELETE CASCADE,
    responder_id UUID REFERENCES profiles(id) ON DELETE RESTRICT,
    response_text TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 10. ANNOUNCEMENTS
CREATE TABLE IF NOT EXISTS announcements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id UUID REFERENCES profiles(id) ON DELETE RESTRICT,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    event_date DATE,
    event_time TIME,
    location TEXT,
    photo_url TEXT,
    is_important BOOLEAN DEFAULT FALSE,
    is_published BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 11. SOCIAL_ASSISTANCE_PROGRAMS
CREATE TABLE IF NOT EXISTS social_assistance_programs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    period TEXT NOT NULL,
    criteria TEXT NOT NULL,
    quota INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 12. SOCIAL_ASSISTANCE_RECIPIENTS
CREATE TABLE IF NOT EXISTS social_assistance_recipients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    program_id UUID REFERENCES social_assistance_programs(id) ON DELETE CASCADE,
    family_id UUID REFERENCES families(id) ON DELETE CASCADE,
    disbursement_status TEXT DEFAULT 'TERDAFTAR',
    disbursed_at TIMESTAMPTZ,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (program_id, family_id)
);

-- 13. NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES profiles(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    body TEXT NOT NULL,
    type TEXT NOT NULL,
    reference_id TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 14. ACTIVITY_LOGS
CREATE TABLE IF NOT EXISTS activity_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admin_id UUID REFERENCES profiles(id) ON DELETE SET NULL,
    action TEXT NOT NULL,
    target_table TEXT NOT NULL,
    target_id TEXT,
    details JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ==============================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ==============================================================
ALTER TABLE profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE families ENABLE ROW LEVEL SECURITY;
ALTER TABLE residents ENABLE ROW LEVEL SECURITY;
ALTER TABLE waste_categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE waste_bills ENABLE ROW LEVEL SECURITY;
ALTER TABLE payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE reports ENABLE ROW LEVEL SECURITY;
ALTER TABLE report_responses ENABLE ROW LEVEL SECURITY;
ALTER TABLE announcements ENABLE ROW LEVEL SECURITY;
ALTER TABLE social_assistance_programs ENABLE ROW LEVEL SECURITY;
ALTER TABLE social_assistance_recipients ENABLE ROW LEVEL SECURITY;
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;
ALTER TABLE activity_logs ENABLE ROW LEVEL SECURITY;

CREATE OR REPLACE FUNCTION is_admin() RETURNS BOOLEAN AS $$
BEGIN
  RETURN EXISTS (
    SELECT 1 FROM profiles 
    WHERE id = auth.uid() AND role = 'ADMIN'
  );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE POLICY "Admin full access profiles" ON profiles FOR ALL USING (is_admin());
CREATE POLICY "Admin full access families" ON families FOR ALL USING (is_admin());
CREATE POLICY "Admin full access residents" ON residents FOR ALL USING (is_admin());
CREATE POLICY "Admin full access waste_categories" ON waste_categories FOR ALL USING (is_admin());
CREATE POLICY "Admin full access waste_bills" ON waste_bills FOR ALL USING (is_admin());
CREATE POLICY "Admin full access payments" ON payments FOR ALL USING (is_admin());
CREATE POLICY "Admin full access reports" ON reports FOR ALL USING (is_admin());
CREATE POLICY "Admin full access report_responses" ON report_responses FOR ALL USING (is_admin());
CREATE POLICY "Admin full access announcements" ON announcements FOR ALL USING (is_admin());
CREATE POLICY "Admin full access bansos_programs" ON social_assistance_programs FOR ALL USING (is_admin());
CREATE POLICY "Admin full access bansos_recipients" ON social_assistance_recipients FOR ALL USING (is_admin());
CREATE POLICY "Admin full access notifications" ON notifications FOR ALL USING (is_admin());
CREATE POLICY "Admin full access activity_logs" ON activity_logs FOR ALL USING (is_admin());

CREATE POLICY "Warga read announcements" ON announcements FOR SELECT USING (is_published = TRUE);
CREATE POLICY "Warga read categories" ON waste_categories FOR SELECT USING (is_active = TRUE);

CREATE POLICY "Warga read own bills" ON waste_bills FOR SELECT USING (
    family_id IN (SELECT id FROM families WHERE phone_number = (SELECT phone_number FROM profiles WHERE id = auth.uid()))
);
CREATE POLICY "Warga read own payments" ON payments FOR SELECT USING (
    family_id IN (SELECT id FROM families WHERE phone_number = (SELECT phone_number FROM profiles WHERE id = auth.uid()))
);

CREATE POLICY "Warga insert report" ON reports FOR INSERT WITH CHECK (reporter_id = auth.uid());
CREATE POLICY "Warga read own reports" ON reports FOR SELECT USING (reporter_id = auth.uid());

CREATE POLICY "Warga read own bansos" ON social_assistance_recipients FOR SELECT USING (
    family_id IN (SELECT id FROM families WHERE phone_number = (SELECT phone_number FROM profiles WHERE id = auth.uid()))
);
