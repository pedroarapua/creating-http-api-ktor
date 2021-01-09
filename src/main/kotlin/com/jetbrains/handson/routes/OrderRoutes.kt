package com.jetbrains.handson.routes

import com.jetbrains.handson.models.Order
import com.jetbrains.handson.models.orderStorage
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.orderRouting() {
    route("/order") {
        get {
            if (orderStorage.isNotEmpty()) {
                call.respond(orderStorage)
            } else {
                call.respondText("No orders found", status = HttpStatusCode.NotFound)
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val order =
                orderStorage.find { it.number == id } ?: return@get call.respondText(
                    "No order with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(order)
        }
        get("{id}/total") {
            val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
            val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
                "Not Found",
                status = HttpStatusCode.NotFound
            )
            val total = order.contents.map { it.price * it.amount }.sum()
            call.respond(total)
        }
    }
}
fun Application.registerOrderRoutes() {
    routing {
        orderRouting()
    }
}