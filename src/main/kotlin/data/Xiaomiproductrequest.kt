package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.XiaomiProduct

@Serializable
data class XiaomiProductRequest(
    var nama: String = "",
    var harga: Long = 0L,
    var deskripsi: String = "",
    var pathGambar: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "nama" to nama,
            "harga" to harga,
            "deskripsi" to deskripsi,
            "pathGambar" to pathGambar
        )
    }

    fun toEntity(): XiaomiProduct {
        return XiaomiProduct(
            nama = nama,
            harga = harga,
            deskripsi = deskripsi,
            pathGambar = pathGambar,
        )
    }
}