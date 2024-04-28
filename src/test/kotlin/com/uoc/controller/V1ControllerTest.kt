package com.uoc.controller

import com.uoc.AbstractIntegrationTest
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import jakarta.inject.Inject
import org.junit.jupiter.api.Test


private const val NOT_CACHED_ORDER_ID = "069738cb-adfe-4d28-964d-5bcb41d48943"

class V1ControllerTest : AbstractIntegrationTest() {

    @Inject
    @Client("/")
    lateinit var client: HttpClient

    @Test
    fun testCreateOrderAndGetFromCache() {
        val json = """
            {
                "items": {
                    "ProductA": 1,
                    "ProductB": 2
                }
            }"""

        val postRequest = HttpRequest.POST("/v1/orders", json)
        val postResponse = client.toBlocking().exchange(postRequest, String::class.java)

        val uuidRegex =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}".toRegex()
        assert(postResponse.status.code == 201)
        assert(postResponse.body().contains("orderId"))
        assert(postResponse.body().contains(uuidRegex))

        val orderUuid = uuidRegex.find(postResponse.body())!!.value

        //Update order status
        val updateJson = """
            {
                "status": "PREPARING"
            }
        """
        val updateRequest = HttpRequest.PATCH("/v1/orders/$orderUuid", updateJson)
        val updateResponse = client.toBlocking().exchange(updateRequest, String::class.java)
        assert(updateResponse.status.code == 200)

        val getRequest = HttpRequest.GET<String>("/v1/orders/$orderUuid")
        val orderInfoResponse = client.toBlocking().exchange(getRequest, String::class.java)
        assert(orderInfoResponse.status.code == 200)
        assert(orderInfoResponse.body().contains("\"status\":\"PREPARING\""))
    }

    @Test
    fun testGetOrderFromDatabase() {
        val request = HttpRequest.GET<String>("/v1/orders/$NOT_CACHED_ORDER_ID")
        val response = client.toBlocking().exchange(request, String::class.java)

        assert(response.status.code == 200)
        assert(response.body().contains("\"status\":\"DELIVERED\""))
    }

}