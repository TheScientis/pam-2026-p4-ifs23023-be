package org.delcom.repositories

import org.delcom.entities.XiaomiProduct

interface IXiaomiProductRepository {
    suspend fun getProducts(search: String): List<XiaomiProduct>
    suspend fun getProductById(id: String): XiaomiProduct?
    suspend fun getProductByName(name: String): XiaomiProduct?
    suspend fun addProduct(product: XiaomiProduct): String
    suspend fun updateProduct(id: String, newProduct: XiaomiProduct): Boolean
    suspend fun removeProduct(id: String): Boolean
}