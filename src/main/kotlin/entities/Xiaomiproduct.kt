package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class XiaomiProduct(
    var id: String = UUID.randomUUID().toString(),
    var nama: String,
    var harga: Long,
    var deskripsi: String,
    var pathGambar: String,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)