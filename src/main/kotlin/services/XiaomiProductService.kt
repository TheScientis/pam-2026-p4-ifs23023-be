package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.XiaomiProductRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IXiaomiProductRepository
import java.io.File
import java.util.*

class XiaomiProductService(private val productRepository: IXiaomiProductRepository) {

    suspend fun getAllProducts(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val products = productRepository.getProducts(search)
        call.respond(DataResponse("success", "Berhasil mengambil daftar produk Xiaomi", mapOf("products" to products)))
    }

    suspend fun getProductById(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID produk tidak boleh kosong!")
        val product = productRepository.getProductById(id) ?: throw AppException(404, "Data produk tidak tersedia!")
        call.respond(DataResponse("success", "Berhasil mengambil data produk", mapOf("product" to product)))
    }

    private suspend fun getProductRequest(call: ApplicationCall): XiaomiProductRequest {
        val req = XiaomiProductRequest()
        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "nama" -> req.nama = part.value.trim()
                        "harga" -> req.harga = part.value.trim().toLongOrNull() ?: 0L
                        "deskripsi" -> req.deskripsi = part.value
                    }
                }
                is PartData.FileItem -> {
                    val ext = part.originalFileName?.substringAfterLast('.', "")?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/xiaomi/$fileName"
                    val file = File(filePath)
                    file.parentFile.mkdirs()
                    part.provider().copyAndClose(file.writeChannel())
                    req.pathGambar = filePath
                }
                else -> {}
            }
            part.dispose()
        }
        return req
    }

    private fun validateProductRequest(req: XiaomiProductRequest) {
        val v = ValidatorHelper(req.toMap())
        v.required("nama", "Nama produk tidak boleh kosong")
        v.required("deskripsi", "Deskripsi tidak boleh kosong")
        v.required("pathGambar", "Gambar tidak boleh kosong")
        v.validate()
        if (req.harga <= 0) throw AppException(400, "harga: Harga harus lebih dari 0")
        if (!File(req.pathGambar).exists()) throw AppException(400, "Gambar produk gagal diupload!")
    }

    suspend fun createProduct(call: ApplicationCall) {
        val req = getProductRequest(call)
        validateProductRequest(req)
        if (productRepository.getProductByName(req.nama) != null) {
            File(req.pathGambar).takeIf { it.exists() }?.delete()
            throw AppException(409, "Produk dengan nama ini sudah terdaftar!")
        }
        val productId = productRepository.addProduct(req.toEntity())
        call.respond(DataResponse("success", "Berhasil menambahkan produk Xiaomi", mapOf("productId" to productId)))
    }

    suspend fun updateProduct(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID produk tidak boleh kosong!")
        val oldProduct = productRepository.getProductById(id) ?: throw AppException(404, "Data produk tidak tersedia!")
        val req = getProductRequest(call)
        if (req.pathGambar.isEmpty()) req.pathGambar = oldProduct.pathGambar
        validateProductRequest(req)
        if (req.nama != oldProduct.nama && productRepository.getProductByName(req.nama) != null) {
            File(req.pathGambar).takeIf { it.exists() }?.delete()
            throw AppException(409, "Produk dengan nama ini sudah terdaftar!")
        }
        if (req.pathGambar != oldProduct.pathGambar) File(oldProduct.pathGambar).takeIf { it.exists() }?.delete()
        if (!productRepository.updateProduct(id, req.toEntity())) throw AppException(400, "Gagal memperbarui data produk!")
        call.respond(DataResponse("success", "Berhasil mengubah data produk", null))
    }

    suspend fun deleteProduct(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID produk tidak boleh kosong!")
        val oldProduct = productRepository.getProductById(id) ?: throw AppException(404, "Data produk tidak tersedia!")
        val oldFile = File(oldProduct.pathGambar)
        if (!productRepository.removeProduct(id)) throw AppException(400, "Gagal menghapus data produk!")
        oldFile.takeIf { it.exists() }?.delete()
        call.respond(DataResponse("success", "Berhasil menghapus data produk", null))
    }

    suspend fun getProductImage(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
        val product = productRepository.getProductById(id) ?: return call.respond(HttpStatusCode.NotFound)
        val file = File(product.pathGambar)
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}