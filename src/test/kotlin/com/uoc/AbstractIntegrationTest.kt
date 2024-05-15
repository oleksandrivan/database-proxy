package com.uoc

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File


private const val MY_SQL_CONTAINER = "mysql:8.0.26"
private const val REDIS_CONTAINER = "redis:7-alpine"
private const val KAFKA_CONTAINER = "confluentinc/cp-kafka:7.6.1"

@Testcontainers
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractIntegrationTest: TestPropertyProvider {

    companion object {

        private val logger = LoggerFactory.getLogger(AbstractIntegrationTest::class.java)

        @Container
        val environment: ComposeContainer = ComposeContainer(File("src/test/resources/docker-compose.yml"))
            .withExposedService("redis", 6379, Wait.forListeningPort())
            .withExposedService("broker", 9092, Wait.forListeningPort())
            .withExposedService("mysql", 3306, Wait.forListeningPort())

        @BeforeAll
        @JvmStatic
        fun setup() {
            environment.start()
            logger.info("DOCKER COMPOSE STARTED!")
            logger.info("Kafka container started at: ${environment.getServicePort("broker", 9092)}")
            logger.info("MySQL container started at: ${environment.getServicePort("mysql", 3306)}")
            logger.info("Redis container started at: ${environment.getServicePort("redis", 6379)}")
        }
    }

    override fun getProperties(): MutableMap<String, String> {
        return mutableMapOf(
            "redis.uri" to "redis://localhost:${environment.getServicePort("redis", 6379)}",
            "kafka.bootstrap.servers" to "localhost:${environment.getServicePort("broker", 9092)}",
            "database.url" to "jdbc:mysql://localhost:${environment.getServicePort("mysql", 3306)}/db",
            "database.username" to "test",
            "database.password" to "test"
        )
    }
}
