CREATE TABLE IF NOT EXISTS plants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(100) NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    manfaat TEXT NOT NULL,
    efek_samping TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Migration: Create xiaomi_products table
CREATE TABLE IF NOT EXISTS xiaomi_products (
                                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(150) NOT NULL,
    harga BIGINT NOT NULL,
    deskripsi TEXT NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
    );