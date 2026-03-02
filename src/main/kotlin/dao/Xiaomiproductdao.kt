package org.delcom.dao

import org.delcom.tables.XiaomiProductTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class XiaomiProductDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, XiaomiProductDAO>(XiaomiProductTable)

    var nama by XiaomiProductTable.nama
    var harga by XiaomiProductTable.harga
    var deskripsi by XiaomiProductTable.deskripsi
    var pathGambar by XiaomiProductTable.pathGambar
    var createdAt by XiaomiProductTable.createdAt
    var updatedAt by XiaomiProductTable.updatedAt
}