package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.parseMessageToMap
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.delcom.services.XiaomiProductService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val plantService: PlantService by inject()
    val profileService: ProfileService by inject()
    val xiaomiProductService: XiaomiProductService by inject()

    install(StatusPages) {
        // Tangkap AppException
        exception<AppException> { call, cause ->
            val dataMap: Map<String, List<String>> = parseMessageToMap(cause.message)

            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data yang dikirimkan tidak valid!",
                    data = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }

        // Tangkap semua Throwable lainnya
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.fromValue(500),
                message = ErrorResponse(
                    status = "error",
                    message = cause.message ?: "Unknown error",
                    data = ""
                )
            )
        }
    }

    routing {
        get("/") {
            call.respondText("API telah berjalan. Dibuat oleh Paul Bornok Manurung (11S23023).")
        }

        // Route Plants
        route("/plants") {
            get {
                plantService.getAllPlants(call)
            }
            post {
                plantService.createPlant(call)
            }
            get("/{id}") {
                plantService.getPlantById(call)
            }
            put("/{id}") {
                plantService.updatePlant(call)
            }
            delete("/{id}") {
                plantService.deletePlant(call)
            }
            get("/{id}/image") {
                plantService.getPlantImage(call)
            }
        }

        // Route Xiaomi Products
        route("/xiaomi-products") {
            get {
                xiaomiProductService.getAllProducts(call)
            }
            post {
                xiaomiProductService.createProduct(call)
            }
            get("/{id}") {
                xiaomiProductService.getProductById(call)
            }
            put("/{id}") {
                xiaomiProductService.updateProduct(call)
            }
            delete("/{id}") {
                xiaomiProductService.deleteProduct(call)
            }
            get("/{id}/image") {
                xiaomiProductService.getProductImage(call)
            }
        }

        // Route Profile
        route("/profile") {
            get {
                profileService.getProfile(call)
            }
            get("/photo") {
                profileService.getProfilePhoto(call)
            }
        }
    }
}