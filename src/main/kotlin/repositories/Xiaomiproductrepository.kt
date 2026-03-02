package org.delcom.repositories

import org.delcom.dao.XiaomiProductDAO
import org.delcom.entities.XiaomiProduct
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.XiaomiProductTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class XiaomiProductRepository : IXiaomiProductRepository {

    private fun daoToModel(dao: XiaomiProductDAO) = XiaomiProduct(
        dao.id.value.toString(),
        dao.nama,
        dao.harga,
        dao.deskripsi,
        dao.pathGambar,
        dao.createdAt,
        dao.updatedAt
    )

    override suspend fun getProducts(search: String): List<XiaomiProduct> = suspendTransaction {
        if (search.isBlank()) {
            XiaomiProductDAO.all()
                .orderBy(XiaomiProductTable.createdAt to SortOrder.DESC)
                .limit(20)
                .map(::daoToModel)
        } else {
            val keyword = "%${search.lowercase()}%"
            XiaomiProductDAO
                .find { XiaomiProductTable.nama.lowerCase() like keyword }
                .orderBy(XiaomiProductTable.nama to SortOrder.ASC)
                .limit(20)
                .map(::daoToModel)
        }
    }

    override suspend fun getProductById(id: String): XiaomiProduct? = suspendTransaction {
        XiaomiProductDAO
            .find { XiaomiProductTable.id eq UUID.fromString(id) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun getProductByName(name: String): XiaomiProduct? = suspendTransaction {
        XiaomiProductDAO
            .find { XiaomiProductTable.nama eq name }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun addProduct(product: XiaomiProduct): String = suspendTransaction {
        val dao = XiaomiProductDAO.new {
            nama = product.nama
            harga = product.harga
            deskripsi = product.deskripsi
            pathGambar = product.pathGambar
            createdAt = product.createdAt
            updatedAt = product.updatedAt
        }
        dao.id.value.toString()
    }

    override suspend fun updateProduct(id: String, newProduct: XiaomiProduct): Boolean = suspendTransaction {
        val dao = XiaomiProductDAO
            .find { XiaomiProductTable.id eq UUID.fromString(id) }
            .limit(1)
            .firstOrNull()

        if (dao != null) {
            dao.nama = newProduct.nama
            dao.harga = newProduct.harga
            dao.deskripsi = newProduct.deskripsi
            dao.pathGambar = newProduct.pathGambar
            dao.updatedAt = newProduct.updatedAt
            true
        } else {
            false
        }
    }

    override suspend fun removeProduct(id: String): Boolean = suspendTransaction {
        val rowsDeleted = XiaomiProductTable.deleteWhere {
            XiaomiProductTable.id eq UUID.fromString(id)
        }
        rowsDeleted == 1
    }
}