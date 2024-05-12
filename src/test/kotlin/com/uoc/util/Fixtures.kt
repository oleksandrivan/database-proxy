package com.uoc.util

import com.uoc.domain.*
import java.time.LocalDateTime

class Fixtures {

    companion object {
        fun order(): Order = Order(
            orderId = OrderId(),
            customerId = CustomerId(1),
            shippingAddress = AddressId(1),
            items = listOf(
                OrderItem(productId = "ProductA", quantity = 1),
                OrderItem(productId = "ProductB", quantity = 2)
            ),
            createdAt = LocalDateTime.of(2000, 1, 1, 14, 0, 1),
            updatedAt = LocalDateTime.of(2010, 1, 1, 14, 0, 1)
        )
    }
}
