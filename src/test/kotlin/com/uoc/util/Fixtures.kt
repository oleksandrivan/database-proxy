package com.uoc.util

import com.uoc.domain.*

class Fixtures {

    companion object {
        fun order(): Order = Order(
            orderId = OrderId(),
            customerId = CustomerId(1),
            shippingAddress = AddressId(1),
            items = listOf(
                OrderItem(productId = "ProductA", quantity = 1),
                OrderItem(productId = "ProductB", quantity = 2)
            )
        )
    }
}