CREATE TABLE `Customer` (
    id INT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE `Address` (
    id INT NOT NULL,
    `street` VARCHAR(255) NOT NULL,
    `city` VARCHAR(255) NOT NULL,
    `state` VARCHAR(255) NOT NULL,
    `postalCode` VARCHAR(255) NOT NULL,
    `country` VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE `Order` (
    id VARCHAR(255) NOT NULL,
    customerId INT NOT NULL,
    `shippingAddress` INT NOT NULL,
    `status` ENUM('CREATED', 'PREPARING', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'CREATED',
    `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (customerId) REFERENCES `Customer`(id),
    FOREIGN KEY (`shippingAddress`) REFERENCES `Address`(`id`)
);

CREATE TABLE `OrderItem` (
    `orderId` VARCHAR(255) NOT NULL,
    `productId` VARCHAR(255) NOT NULL,
    `quantity` INT NOT NULL,
    PRIMARY KEY (`orderId`, `productId`),
    FOREIGN KEY (`orderId`) REFERENCES `Order`(id)
);

INSERT INTO `Customer` (id, `name`, `email`) VALUES (1, 'John Doe', 'jonh.doe@example.com');
INSERT INTO `Address` (id, `street`, `city`, `state`, `postalCode`, `country`) VALUES (1, '123 Main St', 'Springfield', 'IL', '62701', 'USA');
INSERT INTO `Order` (id, customerId, `shippingAddress`, `status`) VALUES ('069738cb-adfe-4d28-964d-5bcb41d48943', 1, 1, 'DELIVERED');
