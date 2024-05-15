package com.uoc

import com.uoc.repository.RedisCacheRepository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


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
        val mySQLContainer: MySQLContainer<Nothing> = MySQLContainer<Nothing>(MY_SQL_CONTAINER)
            .apply {
                withDatabaseName("db")
                withExposedPorts(3306)
                withInitScript("init_mysql.sql")
                waitingFor(Wait.forHealthcheck())
            }

        @Container
        val redisContainer: GenericContainer<Nothing> =
            GenericContainer<Nothing>(DockerImageName.parse(REDIS_CONTAINER))
                .apply {
                    withExposedPorts(6379)
                    waitingFor(Wait.forLogMessage(".*Ready to accept connections.*", 1))
                }

        @Container
        val kafkaContainer: KafkaContainer =
            KafkaContainer(DockerImageName.parse(KAFKA_CONTAINER))
                .apply {
                    withKraft()
                    waitingFor(Wait.forLogMessage(".*Kafka Server started.*", 1))
                }

        @BeforeAll
        @JvmStatic
        fun setup() {
            mySQLContainer.start()
            redisContainer.start()
            kafkaContainer.start()
            logger.info("Kafka container started at: ${kafkaContainer.bootstrapServers}")
            logger.info("MySQL container started at: ${mySQLContainer.jdbcUrl}")
            logger.info("Redis container started at: ${redisContainer.host}:${redisContainer.firstMappedPort}")

        }
    }

    override fun getProperties(): MutableMap<String, String> {
        return mutableMapOf(
            "kafka.bootstrap.servers" to "localhost:${kafkaContainer.firstMappedPort}",
            "redis.uri" to "redis://localhost:${redisContainer.firstMappedPort}",
            "database.url" to mySQLContainer.jdbcUrl,
            "database.username" to mySQLContainer.username,
            "database.password" to mySQLContainer.password
        )
    }
}