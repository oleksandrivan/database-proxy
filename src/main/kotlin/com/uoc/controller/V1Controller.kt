package com.uoc.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.uoc.domain.*
import com.uoc.service.ProxyOrderService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*

@Controller("/v1")
class V1Controller(
    private val proxyOrderService: ProxyOrderService
) {

    private val mapper = jacksonObjectMapper()

    @Post("/orders")
    fun createOrder(@Body request: String): HttpResponse<String> {
        val createRequest = mapper.readValue(request, CreateOrderRequest::class.java)
        val result = proxyOrderService.createOrder(createRequest.toDomain())
        return result.fold(
            onSuccess = { HttpResponse.created(mapper.writeValueAsString(OrderSuccessResponse(it))) },
            onFailure = { HttpResponse.serverError(mapper.writeValueAsString(OrderFailureResponse(it.message!!))) }
        )
    }

    @Get("/orders/{orderId}")
    fun getOrder(@PathVariable orderId: String): HttpResponse<String> {
        val result = proxyOrderService.getOrder(OrderId(orderId))
        return result.fold(
            onSuccess = { HttpResponse.ok(mapper.writeValueAsString(it.toResponse())) },
            onFailure = { HttpResponse.serverError(mapper.writeValueAsString(OrderFailureResponse(it.message!!))) }
        )
    }

    @Patch("/orders/{orderId}")
    fun updateOrder(@PathVariable orderId: OrderId, @Body request: String): HttpResponse<String> {
        val updateOrderRequest = mapper.readValue(request, UpdateOrderRequest::class.java)
        val result = proxyOrderService.updateOrder(orderId, updateOrderRequest.toDomain())
        return result.fold(
            onSuccess = { HttpResponse.ok() },
            onFailure = { HttpResponse.serverError(mapper.writeValueAsString(OrderFailureResponse(it.message!!))) }
        )
    }

    companion object{
        private fun CreateOrderRequest.toDomain() = Order(
            orderId = OrderId(),
            customerId = CustomerId(1),
            items = items.map { OrderItem(it.key, it.value) },
            shippingAddress = AddressId(1)
        )
        fun UpdateOrderRequest.toDomain() = OrderStatus.valueOf(status)

        private fun Order.toResponse() = OrderInfoResponse(
            orderId = orderId.value,
            status = status.name,
            customerId = customerId.value,
            addressId = shippingAddress.value
        )
    }
}

sealed class OrderResponse
data class OrderSuccessResponse(val orderId: OrderId): OrderResponse()
data class OrderFailureResponse(val message: String): OrderResponse()
data class OrderInfoResponse(val orderId: String, val status: String, val customerId: Int, val addressId: Int)

data class CreateOrderRequest(val items: Map<String, Int>)
data class UpdateOrderRequest(val status: String)