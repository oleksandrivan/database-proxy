package com.uoc.config

import com.mysql.cj.jdbc.MysqlDataSource
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import jakarta.inject.Singleton
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory

@Factory
class DatabaseConfig(
    private val configReader: ConfigReader
) {

    private val log = LoggerFactory.getLogger(DatabaseConfig::class.java)

    @Bean
    @Singleton
    fun dslContext(): DSLContext {
        val url = configReader.databaseUrl
        val username = configReader.username
        val password = configReader.password
        log.info("Database configuration: $url, $username, $password")
        val dataSource = MysqlDataSource().apply {
            setURL(url)
            user = username
            setPassword(password)
        }
        return DSL.using(dataSource, SQLDialect.MYSQL)
    }
}
