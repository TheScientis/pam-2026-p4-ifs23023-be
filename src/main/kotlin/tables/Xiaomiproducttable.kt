package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object XiaomiProductTable : UUIDTable("xiaomi_products") {
    val nama = varchar("nama", 150)
    val harga = long("harga")
    val deskripsi = text("deskripsi")
    val pathGambar = varchar("path_gambar", 255)
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}
